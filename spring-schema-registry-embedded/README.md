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
       -Dversion=0.0.2 \
       -Dtransitive=false
   ```

### 1.2 Run

1. Run embedded Kafka + Schema Registry

   ```shell
   java -jar ~/.m2/repository/com/github/mvallim/spring-schema-registry-embedded/0.0.2/spring-schema-registry-embedded-0.0.2.jar
   ```

### 1.3 Check ports

1. Check the application ports

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

   You can see ports **9092** and **8081**. (Kafka and Schema Registry respectivaly)
