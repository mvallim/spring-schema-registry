# Spring Schema Registry

The purpose of this module is to solve the problem of multiple keystores using Spring Boot + Kafka + Schema Registry + SSL

## Problem description

1. **When**
    * We have a Spring Boot application exposing SSL end-points with a first distinct certificate;
    * We have communication with Kafka via SSL with a second distinct certificate;
    * We have the communication with Schema Registry with the same certificate used for communication with Kafka or a separate third party certificate;

2. **Scenarios**

    | Protocol | Spring Boot | Kafka | Schema Registry | Result |
    |:--------:|:-----------:|:-----:|:---------------:|:------:|
    | SSL      | Yes         | Not   | Not             | **Ok** |
    | SSL      | Yes         | Yes   | Not             | **Ok** |
    | SSL      | Yes         | Yes   | Yes             | Fail   |
    | SSL      | Not         | Yes   | Yes             | **Ok** |
    | SSL      | Not         | Not   | Yes             | **Ok** |
    | SSL      | Not         | Not   | Not             | **Ok** |

The failure happens in a scenario where we would expect it to be fully functional, where the application uses one certificate to securely expose endpoints, and uses other certificates to communicate with Schema Registry and Kafka.

```text
+-------------------+            +-----------------------+
|                   |<---json--->| Schema Registry + SSL |
|                   |            +-----------------------+
| Spring Boot + SSL |
|                   |            +-----------------------+
|                   |<--binary-->|      Kafka + SSL      |  
+-------------------+            +-----------------------+
```

The problem happens because the `kafka-avro-serializer` component uses the JVM variables `javax.net.ssl.trustStore`, `javax.net.ssl.keyStore`, `javax.net.ssl.trustStorePassword` and `javax.net.ssl.keyStorePassword`, and these variables apply to the whole application. As a consequence, if we use a certificate to publish the application API, it will be used by the `kafka-avro-serializer` component.

It is intended that the application uses a certificate to expose its API and use a second certificate to communicate with the **Schema Registry**.

## 1. Quick Start

This chapter will show you how to use Kafka + Schema Registry over SSL.

### 1.1 pom.xml

```xml
<dependency>
    <groupId>com.github.mvallim</groupId>
    <artifactId>spring-schema-registry</artifactId>
    <version>0.0.3</version>
</dependency>
```

### 1.2 Configure application

***Attention**: You must use YAML **or** PROPERTIES.*

#### 1.2.1 application.yaml

```yaml
spring:
  kafka:
    bootstrap-servers:
    - kafka-node01:9093
    - kafka-node02:9093
    - kafka-node03:9093
    properties:
      security.protocol: SSL
      auto.register.schemas: false
      value.subject.name.strategy: io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
      schema.registry.url: https://schema-registry-node01:8082, https://schema-registry-node02:8082, https://schema-registry-node03:8082
    ssl:
      protocol: SSL
      key-password: changeit
      key-store-location: classpath:application.client.keystore.jks
      key-store-password: changeit
      key-store-type: JKS
      trust-store-location: classpath:application.client.truststore.jks
      trust-store-password: changeit
      trust-store-type: JKS
    consumer:
      properties:
        max.poll.interval.ms: 3000
        specific.avro.reader: true
      group-id: people
      auto-offset-reset: earliest
      enable-auto-commit: true
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.schemaregistry.serializer.WrapperKafkaAvroDeserializer
    producer:
      acks: all
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.schemaregistry.serializer.WrapperKafkaAvroSerializer

server:
  port: 8443
  ssl:
    key-store: classpath:keystore.jks
    key-store-password: changeit
    keyStoreType: JKS
    trust-store: classpath:truststore.jks
    trust-store-password: changeit
    trustStoreType: JKS
```

#### 1.2.2 application.properties

```properties
spring.kafka.bootstrap-servers=kafka-node01:9093,kafka-node02:9093,kafka-node03:9093

spring.kafka.properties.auto.register.schemas=false
spring.kafka.properties.value.subject.name.strategy=io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
spring.kafka.properties.schema.registry.url=https://schema-registry-node01:8082,https://schema-registry-node02:8082,https://schema-registry-node03:8082
spring.kafka.properties.security.protocol=SSL
spring.kafka.properties.auto.register.schemas=false
spring.kafka.properties.value.subject.name.strategy=io.confluent.kafka.serializers.subject.TopicRecordNameStrategy

spring.kafka.ssl.protocol=SSL
spring.kafka.ssl.key-password=changeit
spring.kafka.ssl.key-store-location=classpath:kafka.client.keystore.jks
spring.kafka.ssl.key-store-password=changeit
spring.kafka.ssl.key-store-type=JKS
spring.kafka.ssl.trust-store-location=classpath:kafka.client.truststore.jks
spring.kafka.ssl.trust-store-password=changeit
spring.kafka.ssl.trust-store-type=JKS

spring.kafka.consumer.properties.max.poll.interval.ms=3000
spring.kafka.consumer.properties.specific.avro.reader=true
spring.kafka.consumer.group-id=people
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.enable-auto-commit=true
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.schemaregistry.serializer.WrapperKafkaAvroDeserializer

spring.kafka.producer.acks=all
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.schemaregistry.serializer.WrapperKafkaAvroSerializer

server.port=8443
server.ssl.key-store=classpath:keystore.jks
server.ssl.key-store-password=changeit
server.ssl.keyStoreType=JKS
server.ssl.trust-store=classpath:truststore.jks
server.ssl.trust-store-password=changeit
server.ssl.trustStoreType=JKS
```

### 1.3 Configure beans

#### 1.3.1 ProducerConfig

```java
@Configuration
public class ProducerConfig {

    @Bean
    public ProducerFactory<?, ?> producerFactory(final KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, GenericRecord> kafkaTemplate(final ProducerFactory<String, GenericRecord> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
```

#### 1.3.2 ConsumerConfig

```java
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

        return containerFactory;
    }
}
```
