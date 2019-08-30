# Kafka + Schema Registry Embedded

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
       -Dversion=0.0.4 \
       -Dtransitive=false
   ```

### 1.2 Command Line Parameters

1. Help

   ```shell
   java -jar ~/.m2/repository/com/github/mvallim/spring-schema-registry-embedded/0.0.4/spring-schema-registry-embedded-0.0.4.jar --help
   ```

   ```text
   usage: spring-schema-registry-embedded
       --broker-port <arg>        the number of port broker
       --brokers <arg>            the number of brokers
       --help                     print this help
       --topic-partitions <arg>   the number of partitions per topic
       --topics <arg>             the topics to create
   ```

2. Without parameters

   ```shell
   java -jar ~/.m2/repository/com/github/mvallim/spring-schema-registry-embedded/0.0.4/spring-schema-registry-embedded-0.0.4.jar
   ```

3. With parameters

   ```shell
   java -jar ~/.m2/repository/com/github/mvallim/spring-schema-registry-embedded/0.0.4/spring-schema-registry-embedded-0.0.4.jar --brokers=10 --topic-partitions=20 --broker-port=9092 --topics=test1,test2,test3
   ```

### 1.3 Run

1. Run embedded Kafka + Schema Registry

   ```shell
   java -jar ~/.m2/repository/com/github/mvallim/spring-schema-registry-embedded/0.0.4/spring-schema-registry-embedded-0.0.4.jar
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

### 1.4 Check ports

1. Check the application ports

   1. **Linux**

      Run this command

      ```shell
      netstat -plnt
      ```

      Output should be

      ```text
      (Not all processes could be identified, non-owned process info
       will not be shown, you would have to be root to see it all.)
      Active Internet connections (only servers)
      Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
      tcp        0      0 127.0.0.1:9092          0.0.0.0:*               LISTEN      18949/java
      tcp        0      0 127.0.0.1:38021         0.0.0.0:*               LISTEN      3518/java
      tcp        0      0 0.0.0.0:8081            0.0.0.0:*               LISTEN      18949/java
      tcp        0      0 0.0.0.0:38417           0.0.0.0:*               LISTEN      3518/java
      tcp        0      0 127.0.0.1:53            0.0.0.0:*               LISTEN      -
      tcp        0      0 127.0.0.1:41623         0.0.0.0:*               LISTEN      15189/code
      tcp        0      0 127.0.0.1:631           0.0.0.0:*               LISTEN      -
      tcp        0      0 127.0.0.1:43097         0.0.0.0:*               LISTEN      18949/java
      ```

   1. **Windows**

      Run this command

      ```shell
      netstat -ant -p tcp | findstr LISTENING
      ```

      Output should be

      ```text
      TCP    127.0.0.1:8081        0.0.0.0:0       LISTENING   InHost
      TCP    127.0.0.1:9092        0.0.0.0:0       LISTENING   InHost
      ```

   You can see ports **9092** and **8081**. (Kafka and Schema Registry respectivaly)
