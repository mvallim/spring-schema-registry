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

package org.springframework.schemaregistry.deserializer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.schemaregistry.core.SslSocketFactoryConfig;
import org.springframework.schemaregistry.serializer.SpecificKafkaAvroSerializer;
import org.springframework.util.ResourceUtils;

import example.avro.User;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class SpecificKafkaAvroDeserializerTest {

  private Map<String, Object> props;

  private MockSchemaRegistryClient schemaRegistry;

  private Serializer<Object> serializer;

  private Deserializer<Object> deserializer;

  private final static String TOPIC = "xpto";

  @Before
  public void setUp() {

    props = new HashMap<>();
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
    props.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
    props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

    final Properties defaultConfig = new Properties();
    defaultConfig.putAll(props);

    schemaRegistry = new MockSchemaRegistryClient();

    serializer = new SpecificKafkaAvroSerializer(schemaRegistry, props);

    deserializer = new SpecificKafkaAvroDeserializer(schemaRegistry, props);

  }

  private IndexedRecord createAvroRecord() throws IOException {
    final File userSchema = ResourceUtils.getFile("classpath:avro/user.avsc");
    final Parser parser = new Parser();
    final Schema schema = parser.parse(userSchema);
    final GenericRecord avroRecord = new GenericData.Record(schema);
    avroRecord.put("name", "Test user");
    return avroRecord;
  }

  @Test
  public void assertSchemaValidAndAvroLocalExists() throws IOException, RestClientException {
    final IndexedRecord avroRecord = createAvroRecord();
    schemaRegistry.register(TOPIC + "-value", avroRecord.getSchema());
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass(), equalTo(User.class));
  }

  @Test
  public void testKafkaAvroDeserializerConfigureWithSSL() throws IOException, NoSuchAlgorithmException {
    final SslSocketFactoryConfig properties = new SslSocketFactoryConfig();
    properties.setProtocol("SSL");
    properties.setKeyPassword("changeit");
    properties.setKeyStoreLocation("classpath:keystore-test.jks");
    properties.setKeyStorePassword("changeit");
    properties.setKeyManagerAlgorithm("SunX509");
    properties.setKeyStoreType("JKS");
    properties.setTrustStoreLocation("classpath:truststore-test.jks");
    properties.setTrustStorePassword("changeit");
    properties.setTrustManagerAlgorithm("SunX509");
    properties.setTrustStoreType("JKS");
    properties.setProvider(SSLContext.getDefault().getProvider());
    properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
    properties.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, false);

    try (final Deserializer<Object> deserializer = new SpecificKafkaAvroDeserializer()) {
      deserializer.configure(properties, false);
      assertEquals(null, deserializer.deserialize("test", null));
    }
  }

  @Test
  public void testKafkaAvroDeserializerConfigureWithOutSSL() throws IOException, NoSuchAlgorithmException {
    final SslSocketFactoryConfig properties = new SslSocketFactoryConfig();
    properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
    properties.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, false);

    try (final Deserializer<Object> deserializer = new SpecificKafkaAvroDeserializer()) {
      deserializer.configure(properties, false);
      assertEquals(null, deserializer.deserialize("test", null));
    }
  }

  @Test
  public void testConfigure() {
    try (final Deserializer<Object> deserializer = new SpecificKafkaAvroDeserializer(new MockSchemaRegistryClient())) {
      final Map<String, Object> props = new HashMap<>();
      props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
      props.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
      deserializer.configure(props, false);
      assertEquals(null, deserializer.deserialize("test", null));
    }
  }

  @Test
  public void testNull() {
    try (final Deserializer<Object> nullAvroSerializer = new SpecificKafkaAvroDeserializer(null)) {
      assertEquals(null, nullAvroSerializer.deserialize("test", null));
    }
  }

}
