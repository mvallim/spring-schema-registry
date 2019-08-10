package org.springframework.schemaregistry.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.ResourceUtils;

import io.confluent.rest.RestConfig;

class SslSocketFactoryConfig extends HashMap<String, Object> {

	private static final long serialVersionUID = 3822429823527345551L;

	public SslSocketFactoryConfig() {
		super();
	}

	public SslSocketFactoryConfig(final Map<String, ?> configs) {
		this.putAll(configs);
	}

	public String getKeyPassword() {
		return Optional.ofNullable(this.get(RestConfig.SSL_KEY_PASSWORD_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_KEY_PASSWORD_CONFIG).toString()
				: null;
	}

	public void setKeyPassword(String keyPassword) {
		this.put(RestConfig.SSL_KEY_PASSWORD_CONFIG, keyPassword);
	}

	public File getKeyStoreLocation() throws FileNotFoundException {
		return Optional.ofNullable(this.get(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG)).isPresent()
				? ResourceUtils.getFile(this.get(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG).toString())
				: null;
	}

	public void setKeyStoreLocation(final String keyStoreLocation) {
		this.put(RestConfig.SSL_KEYSTORE_LOCATION_CONFIG, keyStoreLocation);
	}

	public String getKeyStorePassword() {
		return Optional.ofNullable(this.get(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG).toString()
				: null;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.put(RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG, keyStorePassword);
	}

	public String getKeyStoreType() {
		return Optional.ofNullable(this.get(RestConfig.SSL_KEYSTORE_TYPE_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_KEYSTORE_TYPE_CONFIG).toString()
				: null;
	}

	public void setKeyStoreType(String keyStoreType) {
		this.put(RestConfig.SSL_KEYSTORE_TYPE_CONFIG, keyStoreType);
	}

	public String getKeyManagerAlgorithm() {
		return Optional.ofNullable(this.get(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG).toString()
				: null;
	}

	public void setKeyManagerAlgorithm(String keyManagerAlgorithm) {
		this.put(RestConfig.SSL_KEYMANAGER_ALGORITHM_CONFIG, keyManagerAlgorithm);
	}

	public File getTrustStoreLocation() throws FileNotFoundException {
		return Optional.ofNullable(this.get(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG)).isPresent()
				? ResourceUtils.getFile(this.get(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG).toString())
				: null;
	}

	public void setTrustStoreLocation(String trustStoreLocation) {
		this.put(RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStoreLocation);
	}

	public String getTrustStorePassword() {
		return Optional.ofNullable(this.get(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG).toString()
				: null;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.put(RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePassword);
	}

	public String getTrustStoreType() {
		return Optional.ofNullable(this.get(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG).toString()
				: null;
	}

	public void setTrustStoreType(String trustStoreType) {
		this.put(RestConfig.SSL_TRUSTSTORE_TYPE_CONFIG, trustStoreType);
	}

	public String getTrustManagerAlgorithm() {
		return Optional.ofNullable(this.get(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG).toString()
				: null;
	}

	public void setTrustManagerAlgorithm(String trustManagerAlgorithm) {
		this.put(RestConfig.SSL_TRUSTMANAGER_ALGORITHM_CONFIG, trustManagerAlgorithm);
	}

	public String getProtocol() {
		return Optional.ofNullable(this.get(RestConfig.SSL_PROTOCOL_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_PROTOCOL_CONFIG).toString()
				: null;
	}

	public void setProtocol(String protocol) {
		this.put(RestConfig.SSL_PROTOCOL_CONFIG, protocol);
	}

	public String getProvider() {
		return Optional.ofNullable(this.get(RestConfig.SSL_PROVIDER_CONFIG)).isPresent()
				? this.get(RestConfig.SSL_PROVIDER_CONFIG).toString()
				: null;
	}

	public void setProvider(String provider) {
		this.put(RestConfig.SSL_PROVIDER_CONFIG, provider);
	}

}
