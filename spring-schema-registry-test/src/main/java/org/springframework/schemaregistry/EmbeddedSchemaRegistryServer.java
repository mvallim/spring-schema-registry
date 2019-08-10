package org.springframework.schemaregistry;

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryRestApplication;

public class EmbeddedSchemaRegistryServer implements InitializingBean, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedSchemaRegistryServer.class);

	public static final String BEAN_NAME = "embeddedSchemaRegistry";

	private static final Integer DEFAULT_SCHEMA_REGISTRY_PORT = 8081;
	private static final String DEFAULT_KAFKA_CONNECTION_URL = "localhost:2181";

	private Server server;
	private final Integer port;
	private final String kafkaConnectionUrl;

	public EmbeddedSchemaRegistryServer() {
		this(DEFAULT_SCHEMA_REGISTRY_PORT, DEFAULT_KAFKA_CONNECTION_URL);
	}

	public EmbeddedSchemaRegistryServer(Integer port) {
		this(port, DEFAULT_KAFKA_CONNECTION_URL);
	}

	public EmbeddedSchemaRegistryServer(String kafkaConnectionUrl) {
		this(DEFAULT_SCHEMA_REGISTRY_PORT, kafkaConnectionUrl);
	}

	public EmbeddedSchemaRegistryServer(Integer port, String kafkaConnectionUrl) {
		this.port = port;
		this.kafkaConnectionUrl = kafkaConnectionUrl;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final Properties props = new Properties();
		props.put(SchemaRegistryConfig.PORT_CONFIG, this.port.toString());
		props.put(SchemaRegistryConfig.KAFKASTORE_CONNECTION_URL_CONFIG, this.kafkaConnectionUrl);

		final SchemaRegistryConfig config = new SchemaRegistryConfig(props);
		final SchemaRegistryRestApplication app = new SchemaRegistryRestApplication(config);

		this.server = app.createServer();
		this.server.start();

		LOGGER.info("Server started, listening for requests...");
	}

	@Override
	public void destroy() {
		try {
			this.server.stop();
		} catch (final Exception e) {
			LOGGER.error("Error shutdown embedded schema registry...", e);
		}
	}
}
