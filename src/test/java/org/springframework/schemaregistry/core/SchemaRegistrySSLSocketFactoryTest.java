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

package org.springframework.schemaregistry.core;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchemaRegistrySSLSocketFactoryTest {

  private final SslSocketFactoryConfig properties = new SslSocketFactoryConfig();

  @BeforeEach
  void SetUp() throws NoSuchAlgorithmException {
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
  void whenMinimalValidConfigurationGetSslSocketFactorySuccess() {

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, notNullValue());
  }

  @Test
  void whenInvalidSSLConfigurationGetSslSocketFactoryFail() {

    properties.setProtocol(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidProviderConfigurationGetSslSocketFactorySuccess() {

    properties.setProvider(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, notNullValue());
  }

  @Test
  void whenInvalidKeyPassworConfigurationGetSslSocketFactoryFail() {

    properties.setKeyPassword("xpto");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidKeyPassworIsNullConfigurationGetSslSocketFactorySuccess() {

    properties.setKeyPassword(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, notNullValue());
  }

  @Test
  void whenInvalidProtocolConfigurationGetSslSocketFactoryFail() {

    properties.setProtocol("XPTO");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidKeyStoreLocationConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStoreLocation("tmp/keystore-test.jks");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidKeyStorePasswordConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStorePassword("xpto");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidKeyStoreTypeConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStoreType("TXT");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidKeyManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

    properties.setKeyManagerAlgorithm("XPTO");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenNullKeyStoreLocationConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStoreLocation(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenNullKeyStorePasswordConfigurationGetSslSocketFactorySuccess() {

    properties.setKeyStorePassword(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, notNullValue());
  }

  @Test
  void whenNullKeyPasswordAndKeyStorePasswordConfigurationGetSslSocketFactoryFail() {

    properties.setKeyPassword(null);
    properties.setKeyStorePassword(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenNullKeyStoreTypeConfigurationGetSslSocketFactoryFail() {

    properties.setKeyStoreType(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenNullKeyManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

    properties.setKeyManagerAlgorithm(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, notNullValue());
  }

  @Test
  void whenInvalidTrustStoreLocationConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStoreLocation("tmp/keystore-test.jks");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidTrustStorePasswordConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStorePassword("xpto");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidTrustStoreTypeConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStoreType("TXT");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenInvalidTrustManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

    properties.setTrustManagerAlgorithm("XPTO");

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenNullTrustStoreLocationConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStoreLocation(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenNullTrustStorePasswordConfigurationGetSslSocketFactorySuccess() {

    properties.setTrustStorePassword(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, notNullValue());
  }

  @Test
  void whenNullTrustStoreTypeConfigurationGetSslSocketFactoryFail() {

    properties.setTrustStoreType(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, nullValue());
  }

  @Test
  void whenNullTrustManagerAlgorithmConfigurationGetSslSocketFactoryFail() {

    properties.setTrustManagerAlgorithm(null);

    final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(properties);

    assertThat(sslSocketFactory, notNullValue());
  }

}
