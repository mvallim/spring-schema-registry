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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating SSL socket factories configured for Schema Registry communication.
 * Provides SSL/TLS configuration with support for keystores, truststores, and custom protocols.
 */
public final class SchemaRegistrySSLSocketFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SchemaRegistrySSLSocketFactory.class);

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private SchemaRegistrySSLSocketFactory() {

  }

  /**
   * Creates an SSL socket factory using the provided configuration.
   *
   * @param configs the configuration map containing SSL properties
   * @return a configured SSLSocketFactory, or null if SSL initialization fails
   */
  public static javax.net.ssl.SSLSocketFactory createSslSocketFactory(final Map<String, ?> configs) {
    return new InternalSchemaRegistrySSLSocketFactory(configs).createSslSocketFactory();
  }

  /**
   * Internal implementation of SSLSocketFactory that handles the SSL configuration.
   */
  static class InternalSchemaRegistrySSLSocketFactory implements SSLSocketFactory {

    private final SslSocketFactoryConfig config;

    /**
     * Constructs a new InternalSchemaRegistrySSLSocketFactory with the specified configuration.
     *
     * @param configs the configuration map containing SSL properties
     */
    InternalSchemaRegistrySSLSocketFactory(final Map<String, ?> configs) {
      config = new SslSocketFactoryConfig(configs);
    }

    /**
     * Creates an SSL socket factory configured with key managers, trust managers, and SSL context.
     *
     * @return a configured SSLSocketFactory, or null if creation fails
     */
    @Override
    public javax.net.ssl.SSLSocketFactory createSslSocketFactory() {
      try {

        final String protocol = Optional.ofNullable(config.getProtocol()).orElseThrow(() -> new IllegalArgumentException("property ssl.protocol not found"));

        final KeyManager[] keyManagers = getKeyManagers();

        final TrustManager[] trustManagers = getTrustManagers();

        final SSLContext sslContext;

        if (Objects.isNull(config.getProvider())) {
          sslContext = SSLContext.getInstance(protocol);
        } else {
          sslContext = SSLContext.getInstance(protocol, config.getProvider());
        }

        sslContext.init(keyManagers, trustManagers, new SecureRandom());

        return sslContext.getSocketFactory();

      } catch (final Exception e) {
        final String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        LOGGER.warn("Disabled SSL comunication caused by: '{}'", cause);
        return null;
      }
    }

    /**
     * Creates key managers from the configured keystore.
     *
     * @return an array of KeyManager instances
     * @throws NoSuchAlgorithmException if the key manager algorithm is not available
     * @throws KeyStoreException if there is an error accessing the keystore
     * @throws CertificateException if there is an error reading certificates
     * @throws IOException if there is an error reading the keystore file
     * @throws UnrecoverableKeyException if the key cannot be recovered
     */
    KeyManager[] getKeyManagers() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException {

      final String keyManagerAlgorithm = StringUtils.isEmpty(config.getKeyManagerAlgorithm()) ? KeyManagerFactory.getDefaultAlgorithm() : config.getKeyManagerAlgorithm();

      final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keyManagerAlgorithm);

      final KeyStore keyStore = createKeyStore();

      final String keyPassword = config.getKeyPassword();
      final String keyStorePassword = config.getKeyStorePassword();

      if (StringUtils.isEmpty(keyPassword) && StringUtils.isEmpty(keyStorePassword)) {
        throw new IllegalArgumentException("property ssl.key.password and ssl.keystore.password not found");
      }

      final char[] passwordChars = StringUtils.isEmpty(keyPassword) ? keyStorePassword.toCharArray() : keyPassword.toCharArray();

      keyManagerFactory.init(keyStore, passwordChars);

      return keyManagerFactory.getKeyManagers();
    }

    /**
     * Creates trust managers from the configured truststore.
     *
     * @return an array of TrustManager instances
     * @throws NoSuchAlgorithmException if the trust manager algorithm is not available
     * @throws KeyStoreException if there is an error accessing the truststore
     * @throws CertificateException if there is an error reading certificates
     * @throws IOException if there is an error reading the truststore file
     */
    TrustManager[] getTrustManagers() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {

      final String trustManagerAlgorithm = StringUtils.isEmpty(config.getTrustManagerAlgorithm()) ? TrustManagerFactory.getDefaultAlgorithm() : config.getTrustManagerAlgorithm();

      final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgorithm);

      final KeyStore trustStore = createTrustStore();

      trustManagerFactory.init(trustStore);

      return trustManagerFactory.getTrustManagers();
    }

    /**
     * Creates and loads a keystore from the configured location.
     *
     * @return the loaded KeyStore instance
     * @throws KeyStoreException if the keystore type is not available
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws CertificateException if there is an error reading certificates
     * @throws IOException if there is an error reading the keystore file
     */
    KeyStore createKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

      final String keyStoreType = Optional.ofNullable(config.getKeyStoreType()).orElseThrow(() -> new IllegalArgumentException("property ssl.keystore.type not found"));

      final KeyStore keyStore = KeyStore.getInstance(keyStoreType);

      final String password = config.getKeyStorePassword();
      final char[] passwordChars = StringUtils.isEmpty(password) ? null : password.toCharArray();

      final File keyStoreLocation = Optional.ofNullable(config.getKeyStoreLocation()).orElseThrow(() -> new IllegalArgumentException("property ssl.keystore.location not found"));

      try (final InputStream inputStream = new FileInputStream(keyStoreLocation)) {
        keyStore.load(inputStream, passwordChars);
      }

      return keyStore;
    }

    /**
     * Creates and loads a truststore from the configured location.
     *
     * @return the loaded KeyStore instance (used as truststore)
     * @throws KeyStoreException if the truststore type is not available
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws CertificateException if there is an error reading certificates
     * @throws IOException if there is an error reading the truststore file
     */
    KeyStore createTrustStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

      final String trustStyoreType = Optional.ofNullable(config.getTrustStoreType()).orElseThrow(() -> new IllegalArgumentException("property ssl.truststore.type not found"));

      final KeyStore keyStore = KeyStore.getInstance(trustStyoreType);

      final String password = config.getTrustStorePassword();
      final char[] passwordChars = StringUtils.isEmpty(password) ? null : password.toCharArray();

      final File trustStoreLocation = Optional.ofNullable(config.getTrustStoreLocation()).orElseThrow(() -> new IllegalArgumentException("property ssl.truststore.location not found"));

      try (final InputStream inputStream = new FileInputStream(trustStoreLocation)) {
        keyStore.load(inputStream, passwordChars);
      }

      return keyStore;
    }
  }

}
