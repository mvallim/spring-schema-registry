# Sample Application Demo

## 1. Quick Start

This chapter will show you how to get started run Kafka + Schema Registry embedded.

### 1.1 Prerequisite

Before run `spring-schema-registry-embedded`, you must do download the last version of `spring-schema-registry-embedded` using maven cli.

1. Download artifact

   ```shell
   mvn dependency:get \
       -DremoteRepositories=http://repo1.maven.org/maven2/ \
       -DgroupId=com.github.mvallim \
       -DartifactId=spring-schema-registry-embedded \
       -Dversion=0.0.2 \
       -Dtransitive=false
   ```

### 1.2 Run

1. Run embedded Kafka + Schema Registry

   ```shell
   java -jar ~/.m2/repository/com/github/mvallim/spring-schema-registry-embedded/0.0.2/spring-schema-registry-embedded-0.0.2.jar
   ```

   Output should be

   ```text
     .   ____          _            __ _ _
    /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
   ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
    \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
     '  |____| .__|_| |_|_| |_\__, | / / / /
    =========|_|==============|___/=/_/_/_/
    :: Spring Boot ::        (v2.1.2.RELEASE)

    2019-08-18 19:19:23,098  INFO [main] ApplicationConfig.embeddedKafkaBroker(21) | Listen Kafka Server on : 127.0.0.1:9092
    2019-08-18 19:19:23,729  INFO [main] ApplicationConfig.embeddedSchemaRegistryServer(28) | Listen Schema Registry on : http://localhost:8081
    [2019-08-18 19:19:24.510] - 25942 WARNING [main] --- org.glassfish.jersey.internal.inject.Providers: A provider io.confluent.kafka.schemaregistry.rest.resources.ConfigResource registered in SERVER runtime does not implement any provider interfaces applicable in the SERVER runtime. Due to constraint configuration problems the provider io.confluent.kafka.schemaregistry.rest.resources.ConfigResource will be ignored. 
    [2019-08-18 19:19:24.511] - 25942 WARNING [main] --- org.glassfish.jersey.internal.inject.Providers: A provider io.confluent.kafka.schemaregistry.rest.resources.ModeResource registered in SERVER runtime does not implement any provider interfaces applicable in the SERVER runtime. Due to constraint configuration problems the provider io.confluent.kafka.schemaregistry.rest.resources.ModeResource will be ignored. 
    [2019-08-18 19:19:24.511] - 25942 WARNING [main] --- org.glassfish.jersey.internal.inject.Providers: A provider io.confluent.kafka.schemaregistry.rest.resources.SubjectsResource registered in SERVER runtime does not implement any provider interfaces applicable in the SERVER runtime. Due to constraint configuration problems the provider io.confluent.kafka.schemaregistry.rest.resources.SubjectsResource will be ignored. 
    [2019-08-18 19:19:24.512] - 25942 WARNING [main] --- org.glassfish.jersey.internal.inject.Providers: A provider io.confluent.kafka.schemaregistry.rest.resources.SubjectVersionsResource registered in SERVER runtime does not implement any provider interfaces applicable in the SERVER runtime. Due to constraint configuration problems the provider io.confluent.kafka.schemaregistry.rest.resources.SubjectVersionsResource will be ignored. 
    [2019-08-18 19:19:24.512] - 25942 WARNING [main] --- org.glassfish.jersey.internal.inject.Providers: A provider io.confluent.kafka.schemaregistry.rest.resources.CompatibilityResource registered in SERVER runtime does not implement any provider interfaces applicable in the SERVER runtime. Due to constraint configuration problems the provider io.confluent.kafka.schemaregistry.rest.resources.CompatibilityResource will be ignored. 
    [2019-08-18 19:19:24.512] - 25942 WARNING [main] --- org.glassfish.jersey.internal.inject.Providers: A provider io.confluent.kafka.schemaregistry.rest.resources.SchemasResource registered in SERVER runtime does not implement any provider interfaces applicable in the SERVER runtime. Due to constraint configuration problems the provider io.confluent.kafka.schemaregistry.rest.resources.SchemasResource will be ignored. 
   ```
## 2. Start Sample Application

### 2.1 Run

```shell
mvn spring-boot:run
```

### 2.2 Consume Sample Application API

```shell
curl -s -k \
  -d "@people.json" \
  -H "Content-Type: application/json" \
  -X POST \
  https://localhost:8443/api/people | jq
```

The expected output looks like this:

```json
{
  "id": "6ffbf523-61c8-4d23-a9b9-c0620f0d8350",
  "name": "Marcos Vallim",
  "height": 186,
  "mass": 80,
  "hair_color": "white",
  "skin_color": "white",
  "eye_color": "brown",
  "birth_year": "1980",
  "gender": "male",
  "created": "2019-07-28T00:00:00.000+0000",
  "edited": "2019-07-28T00:00:00.000+0000"
}
```

The expected output application looks like this:

```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.2.RELEASE)

19:19:31.086 [main] INFO  b.c.s.Application - Starting Application on cooler with PID 26097 (/home/mvallim/Projects/workspace/spring-schema-registry-parent/spring-schema-registry-sample/target/classes started by mvallim in /home/mvallim/Projects/workspace/spring-schema-registry-parent/spring-schema-registry-sample)
19:19:31.088 [main] INFO  b.c.s.Application - No active profile set, falling back to default profiles: default
19:19:31.508 [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'org.springframework.kafka.annotation.KafkaBootstrapConfiguration' of type [org.springframework.kafka.annotation.KafkaBootstrapConfiguration$$EnhancerBySpringCGLIB$$5b4f0a06] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
19:19:31.749 [main] INFO  o.s.b.w.e.t.TomcatWebServer - Tomcat initialized with port(s): 8443 (https)
19:19:31.757 [main] INFO  o.a.c.h.Http11NioProtocol - Initializing ProtocolHandler ["https-jsse-nio-8443"]
19:19:31.764 [main] INFO  o.a.c.c.StandardService - Starting service [Tomcat]
19:19:31.765 [main] INFO  o.a.c.c.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.14]
19:19:31.769 [main] INFO  o.a.c.c.AprLifecycleListener - The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: [/usr/java/packages/lib/amd64:/usr/lib/x86_64-linux-gnu/jni:/lib/x86_64-linux-gnu:/usr/lib/x86_64-linux-gnu:/usr/lib/jni:/lib:/usr/lib]
19:19:31.829 [main] INFO  o.a.c.c.C.[.[.[/] - Initializing Spring embedded WebApplicationContext
19:19:31.830 [main] INFO  o.s.w.c.ContextLoader - Root WebApplicationContext: initialization completed in 722 ms
19:19:32.006 [main] INFO  o.s.s.c.ThreadPoolTaskExecutor - Initializing ExecutorService 'applicationTaskExecutor'
19:19:32.411 [main] INFO  o.a.k.c.u.AppInfoParser - Kafka version : 2.0.1
19:19:32.411 [main] INFO  o.a.k.c.u.AppInfoParser - Kafka commitId : fa14705e51bd2ce5
19:19:32.412 [main] INFO  o.s.s.c.ThreadPoolTaskScheduler - Initializing ExecutorService
19:19:32.420 [main] INFO  o.a.c.h.Http11NioProtocol - Starting ProtocolHandler ["https-jsse-nio-8443"]
19:19:32.422 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.a.k.c.Metadata - Cluster ID: QBmbxShaQ0uCWYdpb3C1vw
19:19:32.528 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-2,     groupId=people] Discovered group coordinator localhost:9092 (id: 2147483647 rack: null)
19:19:32.531 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-2, groupId=people] Revoking previously assigned partitions []
19:19:32.531 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.s.k.l.KafkaMessageListenerContainer - partitions revoked: []
19:19:32.531 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-2, groupId=people] (Re-)joining group
19:19:32.573 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-2, groupId=people] Successfully joined group with generation 1
19:19:32.575 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-2, groupId=people] Setting newly assigned partitions [people-0]
19:19:32.585 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.a.k.c.c.i.Fetcher - [Consumer clientId=consumer-2, groupId=people] Resetting offset for partition people-0 to offset 0.
19:19:32.585 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  o.s.k.l.KafkaMessageListenerContainer - partitions assigned: [people-0]
19:19:32.815 [main] INFO  o.s.b.w.e.t.TomcatWebServer - Tomcat started on port(s): 8443 (https) with context path ''
19:19:32.817 [main] INFO  b.c.s.Application - Started Application in 1.957 seconds (JVM running for 2.27)
19:19:55.350 [kafka-producer-network-thread | producer-1] INFO  o.a.k.c.Metadata - Cluster ID: QBmbxShaQ0uCWYdpb3C1vw
19:19:55.663 [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] INFO  b.c.s.a.PeopleApplication - received payload='{"id": "cca01a66-3fd7-4606-995b-f6d1986ca930", "name": "Marcos Vallim", "height": 186, "mass": 80, "hair_color": "white", "skin_color": "white", "eye_color": "brow", "birth_year": "1980", "gender": "male", "created": 1566166795263, "edited": 1566166795263}'

```

## Useful information

* [Registering Schemas](documentation/register-schemas.md)
* [Consuming API](documentation/consume-api.md)
* [Generating Certificates](documentation/generate-certificates.md)
