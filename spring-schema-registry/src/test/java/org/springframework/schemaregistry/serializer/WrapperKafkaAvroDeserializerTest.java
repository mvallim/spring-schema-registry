package org.springframework.schemaregistry.serializer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.kafka.common.serialization.Deserializer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.schemaregistry.core.SslSocketFactoryConfig;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

public class WrapperKafkaAvroDeserializerTest {

	private Map<String, String> props;

	@Before
	public void setUp() {

		props = new HashMap<String, String>();
		props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");

		final Properties defaultConfig = new Properties();
		defaultConfig.putAll(props);
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
		properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
		properties.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, false);

		try(final Deserializer<Object> deserializer = new WrapperKafkaAvroDeserializer()) {
			deserializer.configure(properties, false);
		}
	}

	@Test
	public void testKafkaAvroDeserializerConfigureWithOutSSL() throws IOException, NoSuchAlgorithmException {
		final SslSocketFactoryConfig properties = new SslSocketFactoryConfig();
		properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
		properties.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, false);

		try(final Deserializer<Object> deserializer = new WrapperKafkaAvroDeserializer()) {
			deserializer.configure(properties, false);
		}
	}

	@Test
	public void testNull() {
		try(final Deserializer<Object> nullAvroSerializer = new WrapperKafkaAvroDeserializer(null)) {	
			assertEquals(null, nullAvroSerializer.deserialize("test", null));
		};
	}
	
}
