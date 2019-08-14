package br.com.embedded.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.schemaregistry.EmbeddedSchemaRegistryServer;

@Configuration
class ApplicationConfig implements BeanFactoryPostProcessor, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

	private EmbeddedKafkaBroker embeddedKafkaBroker;
	private EmbeddedSchemaRegistryServer embeddedSchemaRegistryServer;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) {
		try {
			this.embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, 2);
			this.embeddedKafkaBroker.afterPropertiesSet();

			final String kafkaBootstrapServers = this.embeddedKafkaBroker.getBrokersAsString();
			System.setProperty("spring.embedded.kafka.brokers", kafkaBootstrapServers);

			this.embeddedSchemaRegistryServer = new EmbeddedSchemaRegistryServer(this.embeddedKafkaBroker.getZookeeperConnectionString());

			this.embeddedSchemaRegistryServer.afterPropertiesSet();
			
			LOGGER.info("Listen Kafka Server on : {}", kafkaBootstrapServers);
			LOGGER.info("Listen Schema Registry on : http://localhost:8081");
		} catch (final Exception e) {
			LOGGER.error("Error creating Embedded Schema Registry Server", e);
			throw new BeanInitializationException("Error creating Embedded Schema Registry Server", e);
		}
	}

	@Override
	public void destroy() throws Exception {
		this.embeddedKafkaBroker.destroy();
		this.embeddedSchemaRegistryServer.destroy();
	}
}
