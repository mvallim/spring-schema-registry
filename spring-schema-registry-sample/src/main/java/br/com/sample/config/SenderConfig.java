package br.com.sample.config;

import org.apache.avro.generic.GenericRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class SenderConfig {

	@Bean
	public ProducerFactory<?, ?> producerFactory(final KafkaProperties kafkaProperties) {
		return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
	}

	@Bean
	public KafkaTemplate<String, GenericRecord> kafkaTemplate(
			final ProducerFactory<String, GenericRecord> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}
}
