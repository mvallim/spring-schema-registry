package org.springframework.schemaregistry.serializer;

import java.util.Map;

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
	
	SchemaRegistryClient schemaRegistry;

	KafkaAvroSerializer serializer;

	public WrapperKafkaAvroSerializer() {

	}

	public WrapperKafkaAvroSerializer(final SchemaRegistryClient schemaRegistry) {
		this.schemaRegistry = schemaRegistry;
		this.serializer = new KafkaAvroSerializer(this.schemaRegistry);
	}

	public WrapperKafkaAvroSerializer(final SchemaRegistryClient schemaRegistry, final Map<String, ?> configs) {
		this.schemaRegistry = schemaRegistry;
		this.serializerConfig = new KafkaAvroSerializerConfig(configs);
		this.serializer = new KafkaAvroSerializer(this.schemaRegistry, this.serializerConfig.originalsWithPrefix(""));
	}

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey) {
		this.serializerConfig = new KafkaAvroSerializerConfig(configs);
		
		final RestService restService = new RestService(serializerConfig.getSchemaRegistryUrls());

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);

		if (sslSocketFactory != null) {
			restService.setSslSocketFactory(sslSocketFactory);
		}
		
		this.schemaRegistry = new CachedSchemaRegistryClient(restService, serializerConfig.getMaxSchemasPerSubject(), configs);

		this.serializer = new KafkaAvroSerializer(this.schemaRegistry, this.serializerConfig.originalsWithPrefix(""));

		this.serializer.configure(configs, isKey);
	}

	@Override
	public byte[] serialize(final String topic, final Object data) {
		return this.serializer.serialize(topic, data);
	}

	@Override
	public void close() {
		if (this.serializer != null) {
			this.serializer.close();
		}
	}

}