package br.com.embedded.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.schemaregistry.EmbeddedSchemaRegistryServer;

@Configuration
class ApplicationConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

	private static final Integer DEFAULT_KAFKA_PORT = 9092;

	@Bean
	public EmbeddedKafkaBroker embeddedKafkaBroker() {
		final EmbeddedKafkaBroker broker = new EmbeddedKafkaBroker(1, true, 10);
		broker.kafkaPorts(DEFAULT_KAFKA_PORT);
		LOGGER.info("Listen Kafka Server on : {}", broker.getBrokersAsString());
		return broker;
	}

	@Bean
	public EmbeddedSchemaRegistryServer embeddedSchemaRegistryServer(final EmbeddedKafkaBroker embeddedKafkaBroker) {
		final EmbeddedSchemaRegistryServer schemaRegistry = new EmbeddedSchemaRegistryServer(embeddedKafkaBroker.getZookeeperConnectionString());
		LOGGER.info("Listen Schema Registry on : http://localhost:8081");
		return schemaRegistry;
	}

}
