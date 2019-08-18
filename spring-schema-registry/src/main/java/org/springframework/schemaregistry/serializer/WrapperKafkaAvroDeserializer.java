package org.springframework.schemaregistry.serializer;

import java.util.Map;

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
	
	SchemaRegistryClient schemaRegistry;
	
	KafkaAvroDeserializer deserializer;

	public WrapperKafkaAvroDeserializer() {

	}

	public WrapperKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistry) {
		this.schemaRegistry = schemaRegistry;
		this.deserializer = new KafkaAvroDeserializer(this.schemaRegistry);
	}

	public WrapperKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistry, final Map<String, ?> configs) {
		this.schemaRegistry = schemaRegistry;
		this.deserializerConfig = new KafkaAvroSerializerConfig(configs);
		this.deserializer = new KafkaAvroDeserializer(this.schemaRegistry, this.deserializerConfig.originalsWithPrefix(""));
	}
	
	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey) {
		this.deserializerConfig = new KafkaAvroDeserializerConfig(configs);
		
		final RestService restService = new RestService(deserializerConfig.getSchemaRegistryUrls());

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);

		if (sslSocketFactory != null) {
			restService.setSslSocketFactory(sslSocketFactory);
		}

		this.schemaRegistry = new CachedSchemaRegistryClient(restService, deserializerConfig.getMaxSchemasPerSubject(), configs);

		this.deserializer = new KafkaAvroDeserializer(this.schemaRegistry, this.deserializerConfig.originalsWithPrefix(""));

		this.deserializer.configure(configs, isKey);
	}

	@Override
	public Object deserialize(final String topic, final byte[] data) {
		return this.deserializer.deserialize(topic, data);
	}

	@Override
	public void close() {
		if (this.deserializer != null) {
			this.deserializer.close();
		}
	}

}