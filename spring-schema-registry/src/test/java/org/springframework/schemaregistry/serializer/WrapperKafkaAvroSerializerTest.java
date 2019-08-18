package org.springframework.schemaregistry.serializer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
import org.apache.avro.util.Utf8;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDecoder;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import kafka.utils.VerifiableProperties;

public class WrapperKafkaAvroSerializerTest {

	private Map<String, String> props;

	private MockSchemaRegistryClient schemaRegistry;
	
	private KafkaAvroDecoder avroDecoder;
	
	private final static String TOPIC = "xpto";
	
	@Before
	public void setUp() {
		schemaRegistry = new MockSchemaRegistryClient();
		
		props = new HashMap<String, String>();
	    props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
	    props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
	    
	    final Properties defaultConfig = new Properties();
	    defaultConfig.putAll(props);
	    
	    avroDecoder = new KafkaAvroDecoder(schemaRegistry, new VerifiableProperties(defaultConfig));
	}
	
	@Test
	public void testWrapperKafkaAvroSerializer() throws IOException {

		final Serializer<Object> serializer = new WrapperKafkaAvroSerializer(schemaRegistry, props);
		final Deserializer<Object> deserializer = new WrapperKafkaAvroDeserializer(schemaRegistry, props);

	    byte[] bytes;
	    
		final IndexedRecord avroRecord = createAvroRecord();
		bytes = serializer.serialize(TOPIC, avroRecord);
	    assertEquals(avroRecord, deserializer.deserialize(TOPIC, bytes));
	    assertEquals(avroRecord, avroDecoder.fromBytes(bytes));
	    
	    bytes = serializer.serialize(TOPIC, null);
	    assertEquals(null, deserializer.deserialize(TOPIC, bytes));
	    assertEquals(null, avroDecoder.fromBytes(bytes));

	    bytes = serializer.serialize(TOPIC, true);
	    assertEquals(true, deserializer.deserialize(TOPIC, bytes));
	    assertEquals(true, avroDecoder.fromBytes(bytes));

	    bytes = serializer.serialize(TOPIC, 123);
	    assertEquals(123, deserializer.deserialize(TOPIC, bytes));
	    assertEquals(123, avroDecoder.fromBytes(bytes));

	    bytes = serializer.serialize(TOPIC, 345L);
	    assertEquals(345l, deserializer.deserialize(TOPIC, bytes));
	    assertEquals(345l, avroDecoder.fromBytes(bytes));

	    bytes = serializer.serialize(TOPIC, 1.23f);
	    assertEquals(1.23f, deserializer.deserialize(TOPIC, bytes));
	    assertEquals(1.23f, avroDecoder.fromBytes(bytes));

	    bytes = serializer.serialize(TOPIC, 2.34d);
	    assertEquals(2.34, deserializer.deserialize(TOPIC, bytes));
	    assertEquals(2.34, avroDecoder.fromBytes(bytes));

	    bytes = serializer.serialize(TOPIC, "abc");
	    assertEquals("abc", deserializer.deserialize(TOPIC, bytes));
	    assertEquals("abc", avroDecoder.fromBytes(bytes));

	    bytes = serializer.serialize(TOPIC, "abc".getBytes());
	    assertArrayEquals("abc".getBytes(), (byte[]) deserializer.deserialize(TOPIC, bytes));
	    assertArrayEquals("abc".getBytes(), (byte[]) avroDecoder.fromBytes(bytes));

	    bytes = serializer.serialize(TOPIC, new Utf8("abc"));
	    assertEquals("abc", deserializer.deserialize(TOPIC, bytes));
	    assertEquals("abc", avroDecoder.fromBytes(bytes));
	    
		serializer.close();
		deserializer.close();
	}

	private IndexedRecord createAvroRecord() throws IOException {
		final File userSchema = ResourceUtils.getFile("classpath:avro/user.avsc");
		final Parser parser = new Parser();
		final Schema schema = parser.parse(userSchema);
		final GenericRecord avroRecord = new GenericData.Record(schema);
		avroRecord.put("name", "Test user");
		return avroRecord;
	}
	
}
