package org.springframework.schemaregistry.serializer;

import java.util.List;
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

	KafkaAvroSerializer serializer;

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey) {
		final KafkaAvroSerializerConfig serializerConfig = new KafkaAvroSerializerConfig(configs);
		final List<String> urls = serializerConfig.getList(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG);

		final RestService restService = new RestService(urls);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);

		if (sslSocketFactory != null) {
			restService.setSslSocketFactory(sslSocketFactory);
		}

		final SchemaRegistryClient client = new CachedSchemaRegistryClient(restService, 100, configs);

		this.serializer = new KafkaAvroSerializer(client, configs);

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