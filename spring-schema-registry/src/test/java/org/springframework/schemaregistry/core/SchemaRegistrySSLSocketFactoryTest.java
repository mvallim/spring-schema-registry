package org.springframework.schemaregistry.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.net.ssl.SSLSocketFactory;

import org.junit.Before;
import org.junit.Test;

public class SchemaRegistrySSLSocketFactoryTest {

	private final SslSocketFactoryConfig properties = new SslSocketFactoryConfig();

	@Before
	public void SetUp() {
		this.properties.setProtocol("SSL");
		this.properties.setKeyPassword("changeit");
		this.properties.setKeyStoreLocation("classpath:keystore-test.jks");
		this.properties.setKeyStorePassword("changeit");
		this.properties.setKeyManagerAlgorithm("SunX509");
		this.properties.setKeyStoreType("JKS");
		this.properties.setTrustStoreLocation("classpath:truststore-test.jks");
		this.properties.setTrustStorePassword("changeit");
		this.properties.setTrustManagerAlgorithm("SunX509");
		this.properties.setTrustStoreType("JKS");
	}

	@Test
	public void whenMinimalValidConfigurationGetSslSocketFactorySuccess() {

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(notNullValue()));
	}

	@Test
	public void whenInvalidKeyPassworConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyPassword("xpto");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenInvalidProtocolConfigurationGetSslSocketFactoryFail() {

		this.properties.setProtocol("XPTO");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenInvalidKeyStoreLocationConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyStoreLocation("tmp/keystore-test.jks");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenInvalidKeyStorePasswordConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyStorePassword("xpto");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenInvalidKeyStoreTypeConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyStoreType("TXT");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenInvalidKeyManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyManagerAlgorithm("XPTO");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenNullKeyStoreLocationConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyStoreLocation(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenNullKeyStorePasswordConfigurationGetSslSocketFactorySuccess() {

		this.properties.setKeyStorePassword(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(notNullValue()));
	}

	@Test
	public void whenNullKeyPasswordAndKeyStorePasswordConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyPassword(null);
		this.properties.setKeyStorePassword(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenNullKeyStoreTypeConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyStoreType(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenNullKeyManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

		this.properties.setKeyManagerAlgorithm(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(notNullValue()));
	}

	@Test
	public void whenInvalidTrustStoreLocationConfigurationGetSslSocketFactoryFail() {

		this.properties.setTrustStoreLocation("tmp/keystore-test.jks");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenInvalidTrustStorePasswordConfigurationGetSslSocketFactoryFail() {

		this.properties.setTrustStorePassword("xpto");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenInvalidTrustStoreTypeConfigurationGetSslSocketFactoryFail() {

		this.properties.setTrustStoreType("TXT");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenInvalidTrustManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

		this.properties.setTrustManagerAlgorithm("XPTO");

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenNullTrustStoreLocationConfigurationGetSslSocketFactoryFail() {

		this.properties.setTrustStoreLocation(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenNullTrustStorePasswordConfigurationGetSslSocketFactorySuccess() {

		this.properties.setTrustStorePassword(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(notNullValue()));
	}

	@Test
	public void whenNullTrustStoreTypeConfigurationGetSslSocketFactoryFail() {

		this.properties.setTrustStoreType(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(nullValue()));
	}

	@Test
	public void whenNullTrustManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

		this.properties.setTrustManagerAlgorithm(null);

		final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory
				.createSslSocketFactory(this.properties);

		assertThat(sslSocketFactory, is(notNullValue()));
	}

}
