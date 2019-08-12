# Spring Schema Registry

[![Build Status](https://travis-ci.org/mvallim/spring-schema-registry.svg?branch=master)](https://travis-ci.org/mvallim/spring-schema-registry)
[![Coverage Status](https://coveralls.io/repos/github/mvallim/spring-schema-registry/badge.svg?branch=master)](https://coveralls.io/github/mvallim/spring-schema-registry?branch=master)
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

* [Registering Schemas](spring-schema-registry-sample/documentation/register-schemas.md)
* [Consuming API](spring-schema-registry-sample/documentation/consume-api.md)
* [Generating Certificates](spring-schema-registry-sample/documentation/generate-certificates.md)
