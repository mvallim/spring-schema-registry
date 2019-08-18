package org.springframework.schemaregistry.serializer;

import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLSocketFactory;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.schemaregistry.core.SchemaRegistrySSLSocketFactory;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

public class WrapperKafkaAvroSerializer implements Serializer<Object> {

	AbstractKafkaAvroSerDeConfig serializerConfig;
	
	SchemaRegistryClient schemaRegistryClient;

	KafkaAvroSerializer serializer;

	public WrapperKafkaAvroSerializer() {
		this.serializer = new KafkaAvroSerializer();
	}

	public WrapperKafkaAvroSerializer(final SchemaRegistryClient schemaRegistryClient) {
		this.schemaRegistryClient = schemaRegistryClient;
		this.serializer = new KafkaAvroSerializer(this.schemaRegistryClient);
	}

	public WrapperKafkaAvroSerializer(final SchemaRegistryClient schemaRegistryClient, final Map<String, ?> configs) {
		this.schemaRegistryClient = schemaRegistryClient;
		this.serializerConfig = new KafkaAvroSerializerConfig(configs);
		this.serializer = new KafkaAvroSerializer(this.schemaRegistryClient, this.serializerConfig.originalsWithPrefix(""));
	}

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey) {
		
		if (!Optional.ofNullable(this.schemaRegistryClient).isPresent()) {
			this.serializerConfig = new KafkaAvroSerializerConfig(configs);
			
			final RestService restService = new RestService(serializerConfig.getSchemaRegistryUrls());

			final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);

			if (sslSocketFactory != null) {
				restService.setSslSocketFactory(sslSocketFactory);
			}
			
			this.schemaRegistryClient = new CachedSchemaRegistryClient(restService, serializerConfig.getMaxSchemasPerSubject(), this.serializerConfig.originalsWithPrefix(""));			
		}

		this.serializer = new KafkaAvroSerializer(this.schemaRegistryClient, configs);

		this.serializer.configure(configs, isKey);
	}

	@Override
	public byte[] serialize(final String topic, final Object data) {
		return this.serializer.serialize(topic, data);
	}

	@Override
	public void close() {
		this.serializer.close();
	}

}