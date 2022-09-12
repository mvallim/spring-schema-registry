package org.springframework.schemaregistry.serializer;

import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLSocketFactory;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.schemaregistry.core.SchemaRegistrySSLSocketFactory;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

public class SpecificKafkaAvroSerializer extends KafkaAvroSerializer implements Serializer<Object> {

  public SpecificKafkaAvroSerializer() {
    super();
  }

  public SpecificKafkaAvroSerializer(final SchemaRegistryClient schemaRegistryClient) {
    super(schemaRegistryClient);
  }

  public SpecificKafkaAvroSerializer(final SchemaRegistryClient schemaRegistryClient, final Map<String, ?> configs) {
    super(schemaRegistryClient, configs);
  }

  @Override
  public void configure(final Map<String, ?> configs, final boolean isKey) {

    if (Objects.isNull(schemaRegistry)) {

      final AbstractKafkaSchemaSerDeConfig serializerConfig = new KafkaAvroSerializerConfig(configs);

      final RestService restService = new RestService(serializerConfig.getSchemaRegistryUrls());

      final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);

      if (sslSocketFactory != null) {
        restService.setSslSocketFactory(sslSocketFactory);
      }

      final int maxSchemaObject = serializerConfig.getMaxSchemasPerSubject();
      final Map<String, Object> originals = serializerConfig.originalsWithPrefix("");

      schemaRegistry = new CachedSchemaRegistryClient(restService, maxSchemaObject, originals);
    }

    super.configure(configs, isKey);
  }

}