package org.springframework.schemaregistry.deserializer;

import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLSocketFactory;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.schemaregistry.core.SchemaRegistrySSLSocketFactory;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class WrapperKafkaAvroDeserializer extends KafkaAvroDeserializer implements Deserializer<Object> {

  public WrapperKafkaAvroDeserializer() {
    super();
  }

  public WrapperKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistryClient) {
    super(schemaRegistryClient);
  }

  public WrapperKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistryClient, final Map<String, ?> configs) {
    super(schemaRegistryClient, configs);
  }

  @Override
  public void configure(final Map<String, ?> configs, final boolean isKey) {

    if (Objects.isNull(schemaRegistry)) {

      final AbstractKafkaAvroSerDeConfig deserializerConfig = new KafkaAvroDeserializerConfig(configs);

      final RestService restService = new RestService(deserializerConfig.getSchemaRegistryUrls());

      final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);

      if (Objects.nonNull(sslSocketFactory)) {
        restService.setSslSocketFactory(sslSocketFactory);
      }

      final int maxSchemaObject = deserializerConfig.getMaxSchemasPerSubject();
      final Map<String, Object> originals = deserializerConfig.originalsWithPrefix("");

      schemaRegistry = new CachedSchemaRegistryClient(restService, maxSchemaObject, originals);
    }

    super.configure(configs, isKey);
  }

}