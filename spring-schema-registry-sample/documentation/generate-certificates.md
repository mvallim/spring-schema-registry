# Generate certificates

## Spring Boot

### Generate the private key

```shell
keytool \
    -genkeypair \
    -alias spring-boot \
    -dname "cn=Spring Boot" \
    -validity 18250 \
    -keyalg RSA \
    -keysize 4096 \
    -ext bc:c \
    -keystore keystore.jks \
    -keypass changeit \
    -storepass changeit
```

### Generate the spring-boot certificate

```shell
keytool \
    -exportcert \
    -rfc \
    -keystore keystore.jks \
    -alias spring-boot \
    -storepass changeit \
    -file spring-boot.pem
```

### Import spring-boot cert chain into truststore.jks

```shell
keytool \
    -keystore truststore.jks \
    -storepass changeit \
    -importcert \
    -trustcacerts \
    -noprompt \
    -alias spring-boot \
    -file spring-boot.pem
```

## ROOT and CA

### Generate the private keys (for root and ca)

```shell
keytool \
    -genkeypair \
    -alias root \
    -dname "cn=Local Network - Development" \
    -validity 18250 \
    -keyalg RSA \
    -keysize 4096 \
    -ext bc:c \
    -keystore root.jks \
    -keypass changeit \
    -storepass changeit

keytool \
    -genkeypair \
    -alias ca \
    -dname "cn=Local Network - Development" \
    -validity 18250 \
    -keyalg RSA \
    -keysize 4096 \
    -ext bc:c \
    -keystore ca.jks \
    -keypass changeit \
    -storepass changeit
```

### Generate the root certificate

```shell
keytool \
    -exportcert \
    -rfc \
    -keystore root.jks \
    -alias root \
    -storepass changeit \
    -file root.pem
```

### Generate a certificate for ca signed by root (root -> ca)

```shell
keytool \
    -keystore ca.jks \
    -storepass changeit \
    -certreq \
    -alias ca \
    -file ca.csr

keytool \
    -keystore root.jks \
    -storepass changeit \
    -gencert \
    -alias root \
    -ext bc=0 \
    -ext san=dns:ca \
    -rfc \
    -infile ca.csr \
    -outfile ca.pem
```

### Import ca cert chain into ca.jks

```shell
keytool \
    -keystore ca.jks \
    -storepass changeit \
    -importcert \
    -trustcacerts \
    -noprompt \
    -alias root \
    -file root.pem

keytool \
    -keystore ca.jks \
    -storepass changeit \
    -importcert \
    -alias ca \
    -file ca.pem
```

## Kafka Server

### Generate the private keys (for the server)

```shell
keytool \
    -genkeypair \
    -alias kafka-server \
    -dname cn=kafka-server \
    -validity 18250 \
    -keyalg RSA \
    -keysize 4096 \
    -keystore kafka.server.keystore.jks \
    -keypass changeit \
    -storepass changeit
```

### Generate a certificate for the server signed by ca (root -> ca -> kafka-server)

```shell
keytool \
    -keystore kafka.server.keystore.jks \
    -storepass changeit \
    -certreq \
    -alias kafka-server \
    -file kafka-server.csr

keytool \
    -keystore ca.jks \
    -storepass changeit \
    -gencert \
    -alias ca \
    -ext ku:c=dig,keyEnc \
    -ext "san=dns:kafka-node01.,dns:kafka-node02.,dns:kafka-node03." \
    -ext eku=sa,ca \
    -rfc \
    -infile kafka-server.csr \
    -outfile kafka-server.pem
```

### Import the server cert chain into kafka.server.keystore.jks

```shell
keytool \
    -keystore kafka.server.keystore.jks \
    -storepass changeit \
    -importcert \
    -alias kafka-server \
    -noprompt \
    -file kafka-server.pem
```

### Import the server cert chain into kafka.server.truststore.jks

```shell
keytool \
    -keystore kafka.server.truststore.jks \
    -storepass changeit \
    -importcert \
    -trustcacerts \
    -noprompt \
    -alias root \
    -file root.pem

keytool \
    -keystore kafka.server.truststore.jks \
    -storepass changeit \
    -importcert \
    -alias ca \
    -file ca.pem
```

## Schema Registry Server

### Generate the private keys (for schema-registry-server)

```shell
keytool \
    -genkeypair \
    -alias schema-registry-server \
    -dname cn=schema-registry-server \
    -validity 18250 \
    -keyalg RSA \
    -keysize 4096 \
    -keystore schema-registry.server.keystore.jks \
    -keypass changeit \
    -storepass changeit
```

### Generate a certificate for the server signed by ca (root -> ca -> schema-registry-server)

```shell
keytool \
    -keystore schema-registry.server.keystore.jks \
    -storepass changeit \
    -certreq \
    -alias schema-registry-server \
    -file schema-registry-server.csr

keytool \
    -keystore ca.jks \
    -storepass changeit \
    -gencert \
    -alias ca \
    -ext ku:c=dig,keyEnc \
    -ext "san=dns:schema-registry-node01.,dns:schema-registry-node02.,dns:schema-registry-node03." \
    -ext eku=sa,ca \
    -rfc \
    -infile schema-registry-server.csr \
    -outfile schema-registry-server.pem
```

### Import the server cert chain into schema-registry.server.keystore.jks

```shell
keytool \
    -keystore schema-registry.server.keystore.jks \
    -storepass changeit \
    -importcert \
    -alias schema-registry-server \
    -noprompt \
    -file schema-registry-server.pem
```

### Import the server cert chain into schema-registry.server.truststore.jks

```shell
keytool \
    -keystore schema-registry.server.truststore.jks \
    -storepass changeit \
    -importcert \
    -trustcacerts \
    -noprompt \
    -alias root \
    -file root.pem

keytool \
    -keystore schema-registry.server.truststore.jks \
    -storepass changeit \
    -importcert \
    -alias ca \
    -file ca.pem
```

## Schema Registry Client

### Generate the private keys (for schema-registry-client)

```shell
keytool \
    -genkeypair \
    -alias schema-registry-client \
    -dname cn=schema-registry-client \
    -validity 18250 \
    -keyalg RSA \
    -keysize 4096 \
    -keystore schema-registry.client.keystore.jks \
    -keypass changeit \
    -storepass changeit
```

### Generate a certificate for the client signed by ca (root -> ca -> schema-registry-client)

```shell
keytool \
    -keystore schema-registry.client.keystore.jks \
    -storepass changeit \
    -certreq \
    -alias schema-registry-client \
    -file schema-registry-client.csr

keytool \
    -keystore ca.jks \
    -storepass changeit \
    -gencert \
    -alias ca \
    -ext ku:c=dig,keyEnc \
    -ext eku=sa,ca \
    -rfc \
    -infile schema-registry-client.csr \
    -outfile schema-registry-client.pem
```

### Import the server cert chain into schema-registry.client.keystore.jks

```shell
keytool \
    -keystore schema-registry.client.keystore.jks \
    -storepass changeit \
    -importcert \
    -alias schema-registry-client \
    -noprompt \
    -file schema-registry-client.pem
```

### Import the server cert chain into schema-registry.client.truststore.jks

```shell
keytool \
    -keystore schema-registry.client.truststore.jks \
    -storepass changeit \
    -importcert \
    -trustcacerts \
    -noprompt \
    -alias root \
    -file root.pem

keytool \
    -keystore schema-registry.client.truststore.jks \
    -storepass changeit \
    -importcert \
    -alias ca \
    -file ca.pem
```

## Appplication Client

### Generate the private keys (for application-client)

```shell
keytool \
    -genkeypair \
    -alias application-client \
    -dname cn=application-client \
    -validity 18250 \
    -keyalg RSA \
    -keysize 4096 \
    -keystore application.client.keystore.jks \
    -keypass changeit \
    -storepass changeit
```

### Generate a certificate for the client signed by ca (root -> ca -> application-client)

```shell
keytool \
    -keystore application.client.keystore.jks \
    -storepass changeit \
    -certreq \
    -alias application-client \
    -file application-client.csr

keytool \
    -keystore ca.jks \
    -storepass changeit \
    -gencert \
    -alias ca \
    -ext ku:c=dig,keyEnc \
    -ext eku=sa,ca \
    -rfc \
    -infile application-client.csr \
    -outfile application-client.pem
```

### Import the server cert chain into kafka.client.keystore.jks

```shell
keytool \
    -keystore application.client.keystore.jks \
    -storepass changeit \
    -importcert \
    -alias application-client \
    -noprompt \
    -file application-client.pem
```

### Import the server cert chain into kafka.client.truststore.jks

```shell
keytool \
    -keystore application.client.truststore.jks \
    -storepass changeit \
    -importcert \
    -trustcacerts \
    -noprompt \
    -alias root \
    -file root.pem

keytool \
    -keystore application.client.truststore.jks \
    -storepass changeit \
    -importcert \
    -alias ca \
    -file ca.pem
```
