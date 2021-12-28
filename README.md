# Spring Schema Registry
![Java CI with Maven](https://github.com/mvallim/spring-schema-registry/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=spring-schema-registry&metric=alert_status)](https://sonarcloud.io/dashboard?id=spring-schema-registry)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=spring-schema-registry&metric=coverage)](https://sonarcloud.io/dashboard?id=spring-schema-registry)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mvallim/spring-schema-registry/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mvallim/spring-schema-registry)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

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

## Notice

  * Compatible with **JDK 8, 11, 15, 16 and 17**
  * Compatible with **schema-registry version 5.3.1 or later**
  * Compatible with **avro version 1.9.1 or later**

## 1. Quick Start

This chapter will show you how to use Kafka + Schema Registry over SSL.

## 1.1 Create certificates

Run `generate-certificates.sh` 

[generate-certificates.sh](./generate-certificates.sh)

Expected output:

```text
=> ROOT and CA
 => Generate the private keys (for root and ca)
 => Generate the root certificate
 => Generate certificate for ca signed by root (root -> ca)
 => Import ca cert chain into ca.jks
=> Kafka Server
 => Generate the private keys (for the server)
 => Generate certificate for the server signed by ca (root -> ca -> kafka-server)
 => Import the server cert chain into kafka.server.keystore.jks
 => Import the server cert chain into kafka.server.truststore.jks
=> Schema Registry Server
 => Generate the private keys (for schema-registry-server)
 => Generate certificate for the server signed by ca (root -> ca -> schema-registry-server)
 => Import the server cert chain into schema-registry.server.keystore.jks
 => Import the server cert chain into schema-registry.server.truststore.jks
=> Control Center Server
 => Generate the private keys (for control-center-server)
 => Generate certificate for the server signed by ca (root -> ca -> control-center-server)
 => Import the server cert chain into control-center.server.keystore.jks
 => Import the server cert chain into control-center.server.truststore.jks
=> Appplication Client
 => Generate the private keys (for application-client)
 => Generate certificate for the client signed by ca (root -> ca -> application-client)
 => Import the client cert chain into application.client.keystore.jks
 => Import the client cert chain into application.client.truststore.jks
=> Clean up
 => Move files
```

## 1.2 Running stack

Run `docker-compose up -d`

[docker-compose-yml](./docker-compose.yml)

Expected output:

```text
Creating network "schema" with the default driver
Creating zookeeper ... done
Creating kafka     ... done
Creating schema-registry ... done
Creating control-center  ... done
```

### 1.3 Dependencies

You can pull it from the central Maven repositories:

```xml
<dependency>
  <groupId>com.github.mvallim</groupId>
  <artifactId>spring-schema-registry</artifactId>
  <version>2.0.0</version>
</dependency>
```

If you want to try a snapshot version, add the following repository:

```xml
<repository>
  <id>sonatype-snapshots</id>
  <name>Sonatype Snapshots</name>
  <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository>
```

#### Gradle

```groovy
implementation 'com.github.mvallim:spring-schema-registry:2.0.0'
```

If you want to try a snapshot version, add the following repository:

```groovy
repositories {
  maven {
    url "https://oss.sonatype.org/content/repositories/snapshots"
  }
}
```

### 1.4 Configure application

***Attention**: You must use YAML **or** PROPERTIES.*

#### 1.4.1 application.yaml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    properties:
      security.protocol: SSL
      auto.register.schemas: false
      value.subject.name.strategy: io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
      schema.registry.url: https://localhost:8082
    ssl:
      protocol: SSL
      key-password: changeit
      key-store-location: file:certificates/application/application.client.keystore.jks
      key-store-password: changeit
      key-store-type: JKS
      trust-store-location: file:certificates/application/application.client.truststore.jks
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
      value-deserializer: org.springframework.schemaregistry.deserializer.WrapperKafkaAvroDeserializer
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

#### 1.4.2 application.properties

```properties
spring.kafka.bootstrap-servers=localhost:9092

spring.kafka.properties.auto.register.schemas=false
spring.kafka.properties.value.subject.name.strategy=io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
spring.kafka.properties.schema.registry.url=https://localhost:8082
spring.kafka.properties.security.protocol=SSL
spring.kafka.properties.auto.register.schemas=false
spring.kafka.properties.value.subject.name.strategy=io.confluent.kafka.serializers.subject.TopicRecordNameStrategy

spring.kafka.ssl.protocol=SSL
spring.kafka.ssl.key-password=changeit
spring.kafka.ssl.key-store-location=file:certificates/application/application.client.keystore.jks
spring.kafka.ssl.key-store-password=changeit
spring.kafka.ssl.key-store-type=JKS
spring.kafka.ssl.trust-store-location=file:certificates/application/application.client.truststore.jks
spring.kafka.ssl.trust-store-password=changeit
spring.kafka.ssl.trust-store-type=JKS

spring.kafka.consumer.properties.max.poll.interval.ms=3000
spring.kafka.consumer.properties.specific.avro.reader=true
spring.kafka.consumer.group-id=people
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.enable-auto-commit=true
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.schemaregistry.deserializer.WrapperKafkaAvroDeserializer

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

### 1.5 Configure beans

#### 1.5.1 ProducerConfig

```java
@Configuration
public class ProducerConfig {

  @Bean
  public ProducerFactory<String, GenericRecord> producerFactory(final KafkaProperties kafkaProperties) {
    return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
  }

  @Bean
  public KafkaTemplate<String, GenericRecord> kafkaTemplate(final ProducerFactory<String, GenericRecord> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }
}
```

#### 1.5.2 ConsumerConfig

```java
@EnableKafka
@Configuration
public class ConsumerConfig {

  @Bean
  public ConsumerFactory<String, GenericRecord> consumerFactory(final KafkaProperties kafkaProperties) {
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

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [GitHub](https://github.com/mvallim/spring-schema-registry) for versioning. For the versions available, see the [tags on this repository](https://github.com/mvallim/spring-schema-registry/tags).

## Authors

* **Marcos Vallim** - *Initial work, Development, Test, Documentation* - [mvallim](https://github.com/mvallim)
* **Carlos Batistão** - *Initial work, Development, Test, Documentation* - [cezbatistao](https://github.com/cezbatistao)

See also the list of [contributors](CONTRIBUTORS.txt) who participated in this project.

## License

This project is licensed under the Apache License - see the [LICENSE](LICENSE) file for details
