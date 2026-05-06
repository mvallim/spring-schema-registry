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
import java.io.FileNotFoundException;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.ResourceUtils;

import io.confluent.rest.RestConfig;

/**
 * Configuration class for SSL socket factory settings.
 * Extends HashMap to provide typed accessors for SSL-related configuration properties.
 */
public class SslSocketFactoryConfig extends HashMap<String, Object> {

  private static final long serialVersionUID = 3822429823527345551L;

  /**
   * Constructs a new SslSocketFactoryConfig with default settings.
   */
  public SslSocketFactoryConfig() {
    super();
  }

  /**
   * Constructs a new SslSocketFactoryConfig with the specified configuration.
   *
   * @param configs the configuration properties to initialize with
   */
  public SslSocketFactoryConfig(final Map<String, ?> configs) {
    putAll(configs);
  }

  /**
   * Gets the key password used for accessing the keystore.
   *
   * @return the key password, or null if not configured
   */
  public String getKeyPassword() {
    return Optional.ofNullable(get(RestConfig.SSL_KEY_PASSWORD_CONFIG)).isPresent() ? get(RestConfig.SSL_KEY_PASSWORD_CONFIG).toString() : null;
  }

  /**
   * Sets the key password used for accessing the keystore.
   *
   * @param keyPassword the key password to set
   */
  public void setKeyPassword(final String keyPassword) {
    put(RestConfig.SSL_KEY_PASSWORD_CONFIG, keyPassword);
  }

  /**
   * Gets the keystore location as a File object.
   *
   * @return the keystore file location, or null if not configured
   * @throws FileNotFoundException if the keystore file is not found
   */
  public File getKeyStoreLocation() throws FileNotFoundException {
    return Optional.ofNullable(get(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG)).isPresent() ? ResourceUtils.getFile(get(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG).toString()) : null;
  }

  /**
   * Sets the keystore location.
   *
   * @param keyStoreLocation the path to the keystore file
   */
  public void setKeyStoreLocation(final String keyStoreLocation) {
    put(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG, keyStoreLocation);
  }

  /**
   * Gets the keystore password.
   *
   * @return the keystore password, or null if not configured
   */
  public String getKeyStorePassword() {
    return Optional.ofNullable(get(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG)).isPresent() ? get(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG).toString() : null;
  }

  /**
   * Sets the keystore password.
   *
   * @param keyStorePassword the keystore password to set
   */
  public void setKeyStorePassword(final String keyStorePassword) {
    put(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG, keyStorePassword);
  }

  /**
   * Gets the keystore type (e.g., JKS, PKCS12).
   *
   * @return the keystore type, or null if not configured
   */
  public String getKeyStoreType() {
    return Optional.ofNullable(get(RestConfig.SSL_KEYSTORE_TYPE_CONFIG)).isPresent() ? get(RestConfig.SSL_KEYSTORE_TYPE_CONFIG).toString() : null;
  }

  /**
   * Sets the keystore type.
   *
   * @param keyStoreType the keystore type to set (e.g., JKS, PKCS12)
   */
  public void setKeyStoreType(final String keyStoreType) {
    put(RestConfig.SSL_KEYSTORE_TYPE_CONFIG, keyStoreType);
  }

  /**
   * Gets the key manager algorithm (e.g., SunX509).
   *
   * @return the key manager algorithm, or null if not configured
   */
  public String getKeyManagerAlgorithm() {
    return Optional.ofNullable(get(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG)).isPresent() ? get(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG).toString() : null;
  }

  /**
   * Sets the key manager algorithm.
   *
   * @param keyManagerAlgorithm the key manager algorithm to set
   */
  public void setKeyManagerAlgorithm(final String keyManagerAlgorithm) {
    put(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG, keyManagerAlgorithm);
  }

  /**
   * Gets the truststore location as a File object.
   *
   * @return the truststore file location, or null if not configured
   * @throws FileNotFoundException if the truststore file is not found
   */
  public File getTrustStoreLocation() throws FileNotFoundException {
    return Optional.ofNullable(get(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG)).isPresent() ? ResourceUtils.getFile(get(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG).toString()) : null;
  }

  /**
   * Sets the truststore location.
   *
   * @param trustStoreLocation the path to the truststore file
   */
  public void setTrustStoreLocation(final String trustStoreLocation) {
    put(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStoreLocation);
  }

  /**
   * Gets the truststore password.
   *
   * @return the truststore password, or null if not configured
   */
  public String getTrustStorePassword() {
    return Optional.ofNullable(get(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG)).isPresent() ? get(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG).toString() : null;
  }

  /**
   * Sets the truststore password.
   *
   * @param trustStorePassword the truststore password to set
   */
  public void setTrustStorePassword(final String trustStorePassword) {
    put(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePassword);
  }

  /**
   * Gets the truststore type (e.g., JKS, PKCS12).
   *
   * @return the truststore type, or null if not configured
   */
  public String getTrustStoreType() {
    return Optional.ofNullable(get(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG)).isPresent() ? get(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG).toString() : null;
  }

  /**
   * Sets the truststore type.
   *
   * @param trustStoreType the truststore type to set (e.g., JKS, PKCS12)
   */
  public void setTrustStoreType(final String trustStoreType) {
    put(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG, trustStoreType);
  }

  /**
   * Gets the trust manager algorithm (e.g., SunX509).
   *
   * @return the trust manager algorithm, or null if not configured
   */
  public String getTrustManagerAlgorithm() {
    return Optional.ofNullable(get(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG)).isPresent() ? get(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG).toString() : null;
  }

  /**
   * Sets the trust manager algorithm.
   *
   * @param trustManagerAlgorithm the trust manager algorithm to set
   */
  public void setTrustManagerAlgorithm(final String trustManagerAlgorithm) {
    put(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG, trustManagerAlgorithm);
  }

  /**
   * Gets the SSL/TLS protocol (e.g., TLS, TLSv1.2).
   *
   * @return the SSL protocol, or null if not configured
   */
  public String getProtocol() {
    return Optional.ofNullable(get(RestConfig.SSL_PROTOCOL_CONFIG)).isPresent() ? get(RestConfig.SSL_PROTOCOL_CONFIG).toString() : null;
  }

  /**
   * Sets the SSL/TLS protocol.
   *
   * @param protocol the SSL protocol to set (e.g., TLS, TLSv1.2)
   */
  public void setProtocol(final String protocol) {
    put(RestConfig.SSL_PROTOCOL_CONFIG, protocol);
  }

  /**
   * Gets the security provider to use for SSL/TLS.
   *
   * @return the security Provider, or null if not configured
   */
  public Provider getProvider() {
    return Optional.ofNullable(get(RestConfig.SSL_PROVIDER_CONFIG)).isPresent() ? Provider.class.cast(get(RestConfig.SSL_PROVIDER_CONFIG)) : null;
  }

  /**
   * Sets the security provider for SSL/TLS.
   *
   * @param provider the security Provider to set
   */
  public void setProvider(final Provider provider) {
    put(RestConfig.SSL_PROVIDER_CONFIG, provider);
  }

}
