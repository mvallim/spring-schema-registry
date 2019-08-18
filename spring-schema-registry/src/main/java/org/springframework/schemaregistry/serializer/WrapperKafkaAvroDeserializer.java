package org.springframework.schemaregistry.serializer;

import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLSocketFactory;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.schemaregistry.core.SchemaRegistrySSLSocketFactory;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

public class WrapperKafkaAvroDeserializer implements Deserializer<Object> {

	AbstractKafkaAvroSerDeConfig deserializerConfig;
	
	SchemaRegistryClient schemaRegistryClient;
	
	KafkaAvroDeserializer deserializer;

	public WrapperKafkaAvroDeserializer() {
		this.deserializer = new KafkaAvroDeserializer();
	}

	public WrapperKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistryClient) {
		this.schemaRegistryClient = schemaRegistryClient;
		this.deserializer = new KafkaAvroDeserializer(this.schemaRegistryClient);
	}

	public WrapperKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistryClient, final Map<String, ?> configs) {
		this.schemaRegistryClient = schemaRegistryClient;
		this.deserializerConfig = new KafkaAvroSerializerConfig(configs);
		this.deserializer = new KafkaAvroDeserializer(this.schemaRegistryClient, this.deserializerConfig.originalsWithPrefix(""));
	}
	
	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey) {
		
		if (!Optional.ofNullable(this.schemaRegistryClient).isPresent()) {
			this.deserializerConfig = new KafkaAvroDeserializerConfig(configs);
			
			final RestService restService = new RestService(deserializerConfig.getSchemaRegistryUrls());
	
			final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);
	
			if (sslSocketFactory != null) {
				restService.setSslSocketFactory(sslSocketFactory);
			}
	
			this.schemaRegistryClient = new CachedSchemaRegistryClient(restService, deserializerConfig.getMaxSchemasPerSubject(), this.deserializerConfig.originalsWithPrefix(""));
		}
		
		this.deserializer = new KafkaAvroDeserializer(this.schemaRegistryClient, configs);

		this.deserializer.configure(configs, isKey);
	}

	@Override
	public Object deserialize(final String topic, final byte[] data) {
		return this.deserializer.deserialize(topic, data);
	}

	@Override
	public void close() {
		this.deserializer.close();
	}

}