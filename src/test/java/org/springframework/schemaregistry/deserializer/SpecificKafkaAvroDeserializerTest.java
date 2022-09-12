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
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.schemaregistry.core.SslSocketFactoryConfig;
import org.springframework.schemaregistry.serializer.SpecificKafkaAvroSerializer;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecificKafkaAvroDeserializerTest {

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
    final AvroSchema avroSchema = new AvroSchema(avroRecord.getSchema());
    schemaRegistry.register(TOPIC + "-value", avroSchema);
    final byte[] bytes = serializer.serialize(TOPIC, avroRecord);
    final Object deserialize = deserializer.deserialize(TOPIC, bytes);

    assertThat(deserialize.getClass()).isEqualTo(User.class);
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
    properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
    properties.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false);

    try (final Deserializer<Object> deserializer = new SpecificKafkaAvroDeserializer()) {
      deserializer.configure(properties, false);
      assertThat(deserializer.deserialize("test", null)).isNull();;
    }
  }

  @Test
  public void testKafkaAvroDeserializerConfigureWithOutSSL() throws IOException, NoSuchAlgorithmException {
    final SslSocketFactoryConfig properties = new SslSocketFactoryConfig();
    properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
    properties.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false);

    try (final Deserializer<Object> deserializer = new SpecificKafkaAvroDeserializer()) {
      deserializer.configure(properties, false);
      assertThat(deserializer.deserialize("test", null)).isNull();;
    }
  }

  @Test
  public void testConfigure() {
    try (final Deserializer<Object> deserializer = new SpecificKafkaAvroDeserializer(new MockSchemaRegistryClient())) {
      final Map<String, Object> props = new HashMap<>();
      props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
      props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
      deserializer.configure(props, false);
      assertThat(deserializer.deserialize("test", null)).isNull();;
    }
  }

  @Test
  public void testNull() {
    try (final Deserializer<Object> nullAvroSerializer = new SpecificKafkaAvroDeserializer(null)) {
      assertThat(deserializer.deserialize("test", null)).isNull();;
    }
  }

}
