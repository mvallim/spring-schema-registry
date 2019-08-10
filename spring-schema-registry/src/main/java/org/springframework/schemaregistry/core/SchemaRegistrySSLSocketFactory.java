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
import java.util.Optional;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public final class SchemaRegistrySSLSocketFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchemaRegistrySSLSocketFactory.class);

	private SchemaRegistrySSLSocketFactory() {

	}

	public static javax.net.ssl.SSLSocketFactory createSslSocketFactory(final Map<String, ?> configs) {
		return new InternalSchemaRegistrySSLSocketFactory(configs).createSslSocketFactory();
	}

	static class InternalSchemaRegistrySSLSocketFactory implements SSLSocketFactory {

		private final SslSocketFactoryConfig config;

		InternalSchemaRegistrySSLSocketFactory(final Map<String, ?> configs) {
			this.config = new SslSocketFactoryConfig(configs);
		}

		@Override
		public javax.net.ssl.SSLSocketFactory createSslSocketFactory() {
			try {

				final String protocol = Optional.ofNullable(this.config.getProtocol())
						.orElseThrow(() -> new IllegalArgumentException("property ssl.protocol not found"));

				final KeyManager[] keyManagers = this.getKeyManagers();

				final TrustManager[] trustManagers = this.getTrustManagers();

				final SSLContext sslContext;

				if (StringUtils.isEmpty(this.config.getProvider())) {
					sslContext = SSLContext.getInstance(protocol);
				} else {
					sslContext = SSLContext.getInstance(protocol, this.config.getProvider());
				}

				sslContext.init(keyManagers, trustManagers, new SecureRandom());

				return sslContext.getSocketFactory();

			} catch (final Exception e) {
				final String cause = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
				LOGGER.warn("Disabled SSL comunication caused by: '{}'", cause);
				return null;
			}
		}

		KeyManager[] getKeyManagers() throws NoSuchAlgorithmException, KeyStoreException, CertificateException,
				IOException, UnrecoverableKeyException {

			final String keyManagerAlgorithm = StringUtils.isEmpty(this.config.getKeyManagerAlgorithm())
					? KeyManagerFactory.getDefaultAlgorithm()
					: this.config.getKeyManagerAlgorithm();

			final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(keyManagerAlgorithm);

			final KeyStore keyStore = this.createKeyStore();

			final String keyPassword = this.config.getKeyPassword();
			final String keyStorePassword = this.config.getKeyStorePassword();

			if (StringUtils.isEmpty(keyPassword) && StringUtils.isEmpty(keyStorePassword)) {
				throw new IllegalArgumentException("property ssl.key.password and ssl.keystore.password not found");
			}

			final char[] passwordChars = StringUtils.isEmpty(keyPassword) ? keyStorePassword.toCharArray()
					: keyPassword.toCharArray();

			keyManagerFactory.init(keyStore, passwordChars);

			return keyManagerFactory.getKeyManagers();
		}

		TrustManager[] getTrustManagers()
				throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {

			final String trustManagerAlgorithm = StringUtils.isEmpty(this.config.getTrustManagerAlgorithm())
					? TrustManagerFactory.getDefaultAlgorithm()
					: this.config.getTrustManagerAlgorithm();

			final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgorithm);

			final KeyStore trustStore = this.createTrustStore();

			trustManagerFactory.init(trustStore);

			return trustManagerFactory.getTrustManagers();
		}

		KeyStore createKeyStore()
				throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

			final String keyStoreType = Optional.ofNullable(this.config.getKeyStoreType())
					.orElseThrow(() -> new IllegalArgumentException("property ssl.keystore.type not found"));

			final KeyStore keyStore = KeyStore.getInstance(keyStoreType);

			final String password = this.config.getKeyStorePassword();
			final char[] passwordChars = StringUtils.isEmpty(password) ? null : password.toCharArray();

			final File keyStoreLocation = Optional.ofNullable(this.config.getKeyStoreLocation())
					.orElseThrow(() -> new IllegalArgumentException("property ssl.keystore.location not found"));

			try (final InputStream inputStream = new FileInputStream(keyStoreLocation)) {
				keyStore.load(inputStream, passwordChars);
			}

			return keyStore;
		}

		KeyStore createTrustStore()
				throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

			final String trustStyoreType = Optional.ofNullable(this.config.getTrustStoreType())
					.orElseThrow(() -> new IllegalArgumentException("property ssl.truststore.type not found"));

			final KeyStore keyStore = KeyStore.getInstance(trustStyoreType);

			final String password = this.config.getTrustStorePassword();
			final char[] passwordChars = StringUtils.isEmpty(password) ? null : password.toCharArray();

			final File trustStoreLocation = Optional.ofNullable(this.config.getTrustStoreLocation())
					.orElseThrow(() -> new IllegalArgumentException("property ssl.truststore.location not found"));

			try (final InputStream inputStream = new FileInputStream(trustStoreLocation)) {
				keyStore.load(inputStream, passwordChars);
			}

			return keyStore;
		}
	}

}
