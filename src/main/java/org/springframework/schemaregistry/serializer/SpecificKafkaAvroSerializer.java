/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.schemaregistry.serializer;

import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLSocketFactory;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.schemaregistry.core.SchemaRegistrySSLSocketFactory;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
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

      final AbstractKafkaAvroSerDeConfig serializerConfig = new KafkaAvroSerializerConfig(configs);

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