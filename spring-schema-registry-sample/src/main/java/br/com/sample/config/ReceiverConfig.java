package br.com.sample.config;

import org.apache.avro.generic.GenericRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.LoggingErrorHandler;

@EnableKafka
@Configuration
public class ReceiverConfig {

  @Bean
  public ConsumerFactory<?, ?> consumerFactory(final KafkaProperties kafkaProperties) {
    return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
  }

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, GenericRecord>> containerFactory(final ConsumerFactory<String, GenericRecord> consumerFactory) {
    final ConcurrentKafkaListenerContainerFactory<String, GenericRecord> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();

    containerFactory.setConsumerFactory(consumerFactory);
    containerFactory.setConcurrency(20);
    containerFactory.setErrorHandler(new LoggingErrorHandler());

    return containerFactory;
  }
}
