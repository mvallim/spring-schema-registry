package org.springframework.schemaregistry.core;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;


public class SchemaRegistrySSLSocketFactoryTest {

  private final SslSocketFactoryConfig properties = new SslSocketFactoryConfig();

  @BeforeEach
  public void SetUp() throws NoSuchAlgorithmException {
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
  }

  @Test
  public void whenMinimalValidConfigurationGetSslSocketFactorySuccess() {

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNotNull();
  }

  @Test
  public void whenInvalidSSLConfigurationGetSslSocketFactoryFail() {

    properties.setProtocol(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidProviderConfigurationGetSslSocketFactorySuccess() {

    properties.setProvider(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNotNull();
  }

  @Test
  public void whenInvalidKeyPassworConfigurationGetSslSocketFactoryFail() {

    properties.setKeyPassword("xpto");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidKeyPassworIsNullConfigurationGetSslSocketFactorySuccess() {

    properties.setKeyPassword(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNotNull();
  }

  @Test
  public void whenInvalidProtocolConfigurationGetSslSocketFactoryFail() {

    properties.setProtocol("XPTO");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidKeyStoreLocationConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStoreLocation("tmp/keystore-test.jks");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidKeyStorePasswordConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStorePassword("xpto");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidKeyStoreTypeConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStoreType("TXT");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidKeyManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

    properties.setKeyManagerAlgorithm("XPTO");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenNullKeyStoreLocationConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStoreLocation(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenNullKeyStorePasswordConfigurationGetSslSocketFactorySuccess() {

    properties.setKeyStorePassword(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNotNull();
  }

  @Test
  public void whenNullKeyPasswordAndKeyStorePasswordConfigurationGetSslSocketFactoryFail() {

    properties.setKeyPassword(null);
    properties.setKeyStorePassword(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenNullKeyStoreTypeConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStoreType(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenNullKeyManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

    properties.setKeyManagerAlgorithm(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNotNull();
  }

  @Test
  public void whenInvalidTrustStoreLocationConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStoreLocation("tmp/keystore-test.jks");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidTrustStorePasswordConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStorePassword("xpto");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidTrustStoreTypeConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStoreType("TXT");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenInvalidTrustManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

    properties.setTrustManagerAlgorithm("XPTO");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenNullTrustStoreLocationConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStoreLocation(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenNullTrustStorePasswordConfigurationGetSslSocketFactorySuccess() {

    properties.setTrustStorePassword(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNotNull();
  }

  @Test
  public void whenNullTrustStoreTypeConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStoreType(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNull();
  }

  @Test
  public void whenNullTrustManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

    properties.setTrustManagerAlgorithm(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory).isNotNull();
  }

}
