package org.springframework.schemaregistry;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.SocketUtils;

import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryRestApplication;

public class EmbeddedSchemaRegistryServer implements InitializingBean, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedSchemaRegistryServer.class);

	public static final String BEAN_NAME = "embeddedSchemaRegistry";

	private static final String DEFAULT_KAFKA_CONNECTION_URL = "localhost:2181";

	private Server server;
	private final Integer port;
	private final String kafkaConnectionUrl;

	public EmbeddedSchemaRegistryServer() {
		this(null, DEFAULT_KAFKA_CONNECTION_URL);
	}

	public EmbeddedSchemaRegistryServer(Integer port) {
		this(port, DEFAULT_KAFKA_CONNECTION_URL);
	}

	public EmbeddedSchemaRegistryServer(String kafkaConnectionUrl) {
		this(null, kafkaConnectionUrl);
	}

	public EmbeddedSchemaRegistryServer(Integer port, String kafkaConnectionUrl) {
		this.port = port == null ? SocketUtils.findAvailableTcpPort() : port;
		this.kafkaConnectionUrl = isBlank(kafkaConnectionUrl) ? DEFAULT_KAFKA_CONNECTION_URL : kafkaConnectionUrl;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final Properties props = new Properties();
		props.put(SchemaRegistryConfig.PORT_CONFIG, this.port.toString());
		props.put(SchemaRegistryConfig.KAFKASTORE_CONNECTION_URL_CONFIG, this.kafkaConnectionUrl);

		final SchemaRegistryConfig config = new SchemaRegistryConfig(props);
		final SchemaRegistryRestApplication app = new SchemaRegistryRestApplication(config);

		this.server =  app.createServer();
		this.server.start();

		LOGGER.info("Server started, listening for requests...");
	}

	@Override
	public void destroy() {
		try {
			stopServer();
		} catch (final Exception e) {
			LOGGER.error("Error shutdown embedded schema registry...", e);
			throw new RuntimeException("Error shutdown embedded schema registry...", e);
		}
	}

	void stopServer() throws Exception {
		this.server.stop();
	}

	public Server getServer() {
		return server;
	}

	public Integer getPort() {
		return port;
	}

	public String getKafkaConnectionUrl() {
		return kafkaConnectionUrl;
	}
}
