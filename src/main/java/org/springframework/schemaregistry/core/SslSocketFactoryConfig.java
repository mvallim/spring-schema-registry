package org.springframework.schemaregistry.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.ResourceUtils;

import io.confluent.rest.RestConfig;

public class SslSocketFactoryConfig extends HashMap<String, Object> {

  private static final long serialVersionUID = 3822429823527345551L;

  public SslSocketFactoryConfig() {
    super();
  }

  public SslSocketFactoryConfig(final Map<String, ?> configs) {
    putAll(configs);
  }

  public String getKeyPassword() {
    return Optional.ofNullable(get(RestConfig.SSL_KEY_PASSWORD_CONFIG)).isPresent() ? get(RestConfig.SSL_KEY_PASSWORD_CONFIG).toString() : null;
  }

  public void setKeyPassword(final String keyPassword) {
    put(RestConfig.SSL_KEY_PASSWORD_CONFIG, keyPassword);
  }

  public File getKeyStoreLocation() throws FileNotFoundException {
    return Optional.ofNullable(get(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG)).isPresent() ? ResourceUtils.getFile(get(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG).toString()) : null;
  }

  public void setKeyStoreLocation(final String keyStoreLocation) {
    put(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG, keyStoreLocation);
  }

  public String getKeyStorePassword() {
    return Optional.ofNullable(get(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG)).isPresent() ? get(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG).toString() : null;
  }

  public void setKeyStorePassword(final String keyStorePassword) {
    put(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG, keyStorePassword);
  }

  public String getKeyStoreType() {
    return Optional.ofNullable(get(RestConfig.SSL_KEYSTORE_TYPE_CONFIG)).isPresent() ? get(RestConfig.SSL_KEYSTORE_TYPE_CONFIG).toString() : null;
  }

  public void setKeyStoreType(final String keyStoreType) {
    put(RestConfig.SSL_KEYSTORE_TYPE_CONFIG, keyStoreType);
  }

  public String getKeyManagerAlgorithm() {
    return Optional.ofNullable(get(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG)).isPresent() ? get(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG).toString() : null;
  }

  public void setKeyManagerAlgorithm(final String keyManagerAlgorithm) {
    put(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG, keyManagerAlgorithm);
  }

  public File getTrustStoreLocation() throws FileNotFoundException {
    return Optional.ofNullable(get(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG)).isPresent() ? ResourceUtils.getFile(get(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG).toString()) : null;
  }

  public void setTrustStoreLocation(final String trustStoreLocation) {
    put(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStoreLocation);
  }

  public String getTrustStorePassword() {
    return Optional.ofNullable(get(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG)).isPresent() ? get(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG).toString() : null;
  }

  public void setTrustStorePassword(final String trustStorePassword) {
    put(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePassword);
  }

  public String getTrustStoreType() {
    return Optional.ofNullable(get(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG)).isPresent() ? get(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG).toString() : null;
  }

  public void setTrustStoreType(final String trustStoreType) {
    put(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG, trustStoreType);
  }

  public String getTrustManagerAlgorithm() {
    return Optional.ofNullable(get(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG)).isPresent() ? get(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG).toString() : null;
  }

  public void setTrustManagerAlgorithm(final String trustManagerAlgorithm) {
    put(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG, trustManagerAlgorithm);
  }

  public String getProtocol() {
    return Optional.ofNullable(get(RestConfig.SSL_PROTOCOL_CONFIG)).isPresent() ? get(RestConfig.SSL_PROTOCOL_CONFIG).toString() : null;
  }

  public void setProtocol(final String protocol) {
    put(RestConfig.SSL_PROTOCOL_CONFIG, protocol);
  }

  public Provider getProvider() {
    return Optional.ofNullable(get(RestConfig.SSL_PROVIDER_CONFIG)).isPresent() ? Provider.class.cast(get(RestConfig.SSL_PROVIDER_CONFIG)) : null;
  }

  public void setProvider(final Provider provider) {
    put(RestConfig.SSL_PROVIDER_CONFIG, provider);
  }

}
