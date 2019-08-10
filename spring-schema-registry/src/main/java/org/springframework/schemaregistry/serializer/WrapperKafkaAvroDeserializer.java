package org.springframework.schemaregistry.serializer;

import java.util.List;
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

public class WrapperKafkaAvroDeserializer implements Deserializer<Object> {

	KafkaAvroDeserializer deserializer;

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey) {
		final KafkaAvroDeserializerConfig deserializerConfig = new KafkaAvroDeserializerConfig(configs);
		final List<String> urls = deserializerConfig.getList(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG);

		final RestService restService = new RestService(urls);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);

		if (sslSocketFactory != null) {
			restService.setSslSocketFactory(sslSocketFactory);
		}

		final SchemaRegistryClient client = new CachedSchemaRegistryClient(restService, 100, configs);

		this.deserializer = new KafkaAvroDeserializer(client);

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