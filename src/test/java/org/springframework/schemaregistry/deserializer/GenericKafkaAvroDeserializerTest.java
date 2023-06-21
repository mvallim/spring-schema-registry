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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.schemaregistry.serializer.SpecificKafkaAvroSerializer;
import org.springframework.util.ResourceUtils;

import example.avro.User;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class GenericKafkaAvroDeserializerTest {

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

    deserializer = new GenericKafkaAvroDeserializer(schemaRegistry, props);
  }

  private IndexedRecord createUnknowAvroRecord() throws IOException {
    final String otherSchema = "{\"namespace\": \"example.avro\", \"type\": \"record\", \"name\": \"Other\", \"fields\": [{\"name\": \"f1\", \"type\": \"string\"}, {\"name\": \"f2\", \"type\": \"string\"}]}";
    final Parser parser = new Parser();
    final Schema schema = parser.parse(otherSchema);
    final GenericRecord avroRecord = new GenericData.Record(schema);
    avroRecord.put("f1", "Other user");
    avroRecord.put("f2", "Other user");
    return avroRecord;
  }

  private IndexedRecord createKnowAvroRecord() throws IOException {
    final File userSchema = ResourceUtils.getFile("classpath:avro/user.avsc");
    final Parser parser = new Parser();
    final Schema schema = parser.parse(userSchema);
    final GenericRecord avroRecord = new GenericData.Record(schema);
    avroRecord.put("name", "Test user");
    return avroRecord;
  }

  @Test
  public void assertSchemaValidAndAvroLocalExists() throws IOException, RestClientException {
    final IndexedRecord avroRecord = createKnowAvroRecord();
    schemaRegistry.register(TOPIC + "-value", avroRecord.getSchema());
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass(), equalTo(User.class));
  }

  @Test
  public void assertSchemaValidAndAvroLocalExistsAndCached() throws IOException, RestClientException {
    final IndexedRecord avroRecord = createKnowAvroRecord();
    schemaRegistry.register(TOPIC + "-value", avroRecord.getSchema());
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    deserializer.deserialize(TOPIC, bytes);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass(), equalTo(User.class));
  }

  @Test
  public void assertSchemaValidAndAvroLocalDontExists() throws IOException, RestClientException {
    final IndexedRecord avroRecord = createUnknowAvroRecord();
    schemaRegistry.register(TOPIC + "-value", avroRecord.getSchema());
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass(), equalTo(GenericData.Record.class));
  }

  @Test
  public void assertSchemaValidAndAvroLocalDontExistsAndCached() throws IOException, RestClientException {
    final IndexedRecord avroRecord = createUnknowAvroRecord();
    schemaRegistry.register(TOPIC + "-value", avroRecord.getSchema());
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    deserializer.deserialize(TOPIC, bytes);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass(), equalTo(GenericData.Record.class));
  }

  @Test(expected = SerializationException.class)
  public void assertSchemaInvalidAndDataInvalid() throws IOException, RestClientException {
    deserializer.deserialize(TOPIC, new byte[] { 0x10 });
  }

}