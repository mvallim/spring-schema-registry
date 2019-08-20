package org.springframework.schemaregistry.rule;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.springframework.schemaregistry.EmbeddedSchemaRegistryServer;

public class EmbeddedSchemaRegistryRule extends ExternalResource implements TestRule {

	final EmbeddedSchemaRegistryServer embeddedSchemaRegistryServer;

	public EmbeddedSchemaRegistryRule() {
		this(null, null);
	}

	public EmbeddedSchemaRegistryRule(Integer port) {
		this(port, null);
	}

	public EmbeddedSchemaRegistryRule(String kafkaConnectionUrl) {
		this(null, kafkaConnectionUrl);
	}

	public EmbeddedSchemaRegistryRule(Integer port, String kafkaConnectionUrl) {
		this.embeddedSchemaRegistryServer = new EmbeddedSchemaRegistryServer(port, kafkaConnectionUrl);
	}

	public Integer getPort() {
		return this.embeddedSchemaRegistryServer.getPort();
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
