package br.com.embedded.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.schemaregistry.EmbeddedSchemaRegistryServer;

@Configuration
class ApplicationConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

  @Value("${brokers:1}")
  private Integer brokers;

  @Value("${broker-port:9092}")
  private Integer brokerPort;

  @Value("${schema-registry-port:8081}")
  private Integer schemaRegistryPort;

  @Value("${topic-partitions:20}")
  private Integer partitions;

  @Value("${topics:null}")
  private String[] topics;

  @Bean
  public EmbeddedKafkaBroker embeddedKafkaBroker() {
    final EmbeddedKafkaBroker broker = new EmbeddedKafkaBroker(brokers, true, partitions, topics);

    final int[] ports = new int[brokers];

    for (int i = 0; i < brokers; i++) {
      ports[i] = brokerPort + i;
    }

    broker.kafkaPorts(ports);

    LOGGER.info("Listen Kafka Server on : {}", broker.getBrokersAsString());
    return broker;
  }

  @Bean
  public EmbeddedSchemaRegistryServer embeddedSchemaRegistryServer(final EmbeddedKafkaBroker embeddedKafkaBroker) {
    final EmbeddedSchemaRegistryServer schemaRegistry = new EmbeddedSchemaRegistryServer(schemaRegistryPort, embeddedKafkaBroker.getZookeeperConnectionString());
    LOGGER.info("Listen Schema Registry on : http://localhost:{}", schemaRegistryPort);
    return schemaRegistry;
  }

}
