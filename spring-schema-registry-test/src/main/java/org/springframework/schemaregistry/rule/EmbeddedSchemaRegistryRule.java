package org.springframework.schemaregistry.rule;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.springframework.schemaregistry.EmbeddedSchemaRegistryServer;

public class EmbeddedSchemaRegistryRule extends ExternalResource implements TestRule {

	private static final Integer DEFAULT_SCHEMA_REGISTRY_PORT = 8081;
	private static final String DEFAULT_KAFKA_CONNECTION_URL = "localhost:2181";

	private final EmbeddedSchemaRegistryServer embeddedSchemaRegistryServer;

	public EmbeddedSchemaRegistryRule() {
		this(DEFAULT_SCHEMA_REGISTRY_PORT, DEFAULT_KAFKA_CONNECTION_URL);
	}

	public EmbeddedSchemaRegistryRule(Integer port) {
		this(port, DEFAULT_KAFKA_CONNECTION_URL);
	}

	public EmbeddedSchemaRegistryRule(String kafkaConnectionUrl) {
		this(DEFAULT_SCHEMA_REGISTRY_PORT, kafkaConnectionUrl);
	}

	public EmbeddedSchemaRegistryRule(Integer port, String kafkaConnectionUrl) {
		this.embeddedSchemaRegistryServer = new EmbeddedSchemaRegistryServer(port, kafkaConnectionUrl);
	}

	@Override
	protected void before() throws Throwable {
		this.embeddedSchemaRegistryServer.afterPropertiesSet();
	}

	@Override
	protected void after() {
		this.embeddedSchemaRegistryServer.destroy();
	}
}
