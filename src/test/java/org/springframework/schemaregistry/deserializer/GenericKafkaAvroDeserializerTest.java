package org.springframework.schemaregistry.deserializer;


import example.avro.User;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.schemaregistry.serializer.SpecificKafkaAvroSerializer;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class GenericKafkaAvroDeserializerTest {

  private Map<String, Object> props;

  private MockSchemaRegistryClient schemaRegistry;

  private Serializer<Object> serializer;

  private Deserializer<Object> deserializer;

  private final static String TOPIC = "xpto";

  @BeforeEach
  public void setUp() {

    props = new HashMap<>();
    props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
    props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
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
    final AvroSchema avroSchema = new AvroSchema(avroRecord.getSchema());
    schemaRegistry.register(TOPIC + "-value", avroSchema);
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);


    assertThat(deserialize.getClass()).isEqualTo(User.class);
  }

  @Test
  public void assertSchemaValidAndAvroLocalExistsAndCached() throws IOException, RestClientException {
    final IndexedRecord avroRecord = createKnowAvroRecord();
    final AvroSchema avroSchema = new AvroSchema(avroRecord.getSchema());
    schemaRegistry.register(TOPIC + "-value", avroSchema);
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    deserializer.deserialize(TOPIC, bytes);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass()).isEqualTo(User.class);
  }

  @Test
  public void assertSchemaValidAndAvroLocalDontExists() throws IOException, RestClientException {
    final IndexedRecord avroRecord = createUnknowAvroRecord();
    final AvroSchema avroSchema = new AvroSchema(avroRecord.getSchema());
    schemaRegistry.register(TOPIC + "-value", avroSchema);
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass()).isEqualTo(GenericData.Record.class);
  }

  @Test
  public void assertSchemaValidAndAvroLocalDontExistsAndCached() throws IOException, RestClientException {
    final IndexedRecord avroRecord = createUnknowAvroRecord();
    final AvroSchema avroSchema = new AvroSchema(avroRecord.getSchema());
    schemaRegistry.register(TOPIC + "-value", avroSchema);
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    deserializer.deserialize(TOPIC, bytes);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass()).isEqualTo((GenericData.Record.class));
  }

  @Test
  public void assertSchemaInvalidAndDataInvalid()  {
    assertThatThrownBy(() -> deserializer.deserialize(TOPIC, new byte[] { 0x10 }))
        .isInstanceOf(SerializationException.class);
  }

}