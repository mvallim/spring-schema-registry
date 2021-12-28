#!/bin/bash

echo "=> ROOT and CA"

echo " => Generate the private keys (for root and ca)"
keytool \
	-genkeypair \
	-alias root \
	-dname "CN=root, O=Home, OU=Home, L=Campinas, C=BR" \
	-validity 3650 \
	-keyalg RSA \
	-keysize 4096 \
	-ext bc:c \
	-keystore root.jks \
	-storetype jks \
	-keypass changeit \
	-storepass changeit > /dev/null 2>&1

keytool \
	-genkeypair \
	-alias ca \
	-dname "CN=ca, O=Home, OU=Home, L=Campinas, C=BR" \
	-validity 3650 \
	-keyalg RSA \
	-keysize 4096 \
	-ext bc:c \
	-keystore ca.jks \
	-storetype jks \
	-keypass changeit \
	-storepass changeit > /dev/null 2>&1

echo " => Generate the root certificate"
keytool \
	-exportcert \
	-rfc \
	-keystore root.jks \
	-storetype jks \
	-alias root \
	-storepass changeit \
	-file root.pem > /dev/null 2>&1

echo " => Generate certificate for ca signed by root (root -> ca)"
keytool \
	-keystore ca.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-certreq \
	-alias ca \
	-file ca.csr > /dev/null 2>&1
keytool \
	-keystore root.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-gencert \
	-alias root \
	-ext bc=0 \
	-ext san=dns:ca \
	-rfc \
	-infile ca.csr \
	-outfile ca.pem > /dev/null 2>&1

echo " => Import ca cert chain into ca.jks"
keytool \
	-keystore ca.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-trustcacerts \
	-noprompt \
	-alias root \
	-file root.pem > /dev/null 2>&1
keytool \
	-keystore ca.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias ca \
	-file ca.pem > /dev/null 2>&1

echo "=> Kafka Server"

echo " => Generate the private keys (for the server)"
keytool \
	-genkeypair \
	-alias kafka-server \
	-dname "CN=kafka-server, O=Home, OU=Home, L=Campinas, C=BR" \
	-validity 3650 \
	-keyalg RSA \
	-keysize 4096 \
	-keystore kafka.server.keystore.jks \
	-storetype jks \
	-keypass changeit \
	-storepass changeit > /dev/null 2>&1

echo " => Generate certificate for the server signed by ca (root -> ca -> kafka-server)"
keytool \
	-keystore kafka.server.keystore.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-certreq \
	-alias kafka-server \
	-file kafka-server.csr > /dev/null 2>&1
keytool \
	-keystore ca.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-gencert \
	-alias ca \
	-ext ku:c=dig,keyEnc \
	-ext "san=dns:kafka,dns:broker,dns:localhost" \
	-ext eku=sa,ca \
	-rfc \
	-infile kafka-server.csr \
	-outfile kafka-server.pem > /dev/null 2>&1

echo " => Import the server cert chain into kafka.server.keystore.jks"
keytool \
	-keystore kafka.server.keystore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias kafka-server \
	-noprompt \
	-file kafka-server.pem > /dev/null 2>&1

echo " => Import the server cert chain into kafka.server.truststore.jks"
keytool \
	-keystore kafka.server.truststore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-trustcacerts \
	-noprompt \
	-alias root \
	-file root.pem > /dev/null 2>&1
keytool \
	-keystore kafka.server.truststore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias ca \
	-file ca.pem > /dev/null 2>&1
echo "changeit" > credentials

echo "=> Schema Registry Server"

echo " => Generate the private keys (for schema-registry-server)"
keytool \
	-genkeypair \
	-alias schema-registry-server \
	-dname "CN=schema-registry-server, O=Home, OU=Home, L=Campinas, C=BR" \
	-validity 3650 \
	-keyalg RSA \
	-keysize 4096 \
	-keystore schema-registry.server.keystore.jks \
	-storetype jks \
	-keypass changeit \
	-storepass changeit > /dev/null 2>&1

echo " => Generate certificate for the server signed by ca (root -> ca -> schema-registry-server)"
keytool \
	-keystore schema-registry.server.keystore.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-certreq \
	-alias schema-registry-server \
	-file schema-registry-server.csr > /dev/null 2>&1
keytool \
	-keystore ca.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-gencert \
	-alias ca \
	-ext ku:c=dig,keyEnc \
	-ext "san=dns:schema-registry,dns:schema,dns:localhost" \
	-ext eku=sa,ca \
	-rfc \
	-infile schema-registry-server.csr \
	-outfile schema-registry-server.pem > /dev/null 2>&1

echo " => Import the server cert chain into schema-registry.server.keystore.jks"
keytool \
	-keystore schema-registry.server.keystore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias schema-registry-server \
	-noprompt \
	-file schema-registry-server.pem > /dev/null 2>&1

echo " => Import the server cert chain into schema-registry.server.truststore.jks"
keytool \
	-keystore schema-registry.server.truststore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-trustcacerts \
	-noprompt \
	-alias root \
	-file root.pem > /dev/null 2>&1
keytool \
	-keystore schema-registry.server.truststore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias ca \
	-file ca.pem > /dev/null 2>&1

echo "=> Control Center Server"

echo " => Generate the private keys (for control-center-server)"
keytool \
	-genkeypair \
	-alias control-center-server \
	-dname "CN=control-center-server, O=Home, OU=Home, L=Campinas, C=BR" \
	-validity 3650 \
	-keyalg RSA \
	-keysize 4096 \
	-keystore control-center.server.keystore.jks \
	-storetype jks \
	-keypass changeit \
	-storepass changeit > /dev/null 2>&1

echo " => Generate certificate for the server signed by ca (root -> ca -> control-center-server)"
keytool \
	-keystore control-center.server.keystore.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-certreq \
	-alias control-center-server \
	-file control-center-server.csr > /dev/null 2>&1
keytool \
	-keystore ca.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-gencert \
	-alias ca \
	-ext ku:c=dig,keyEnc \
	-ext "san=dns:control-center,dns:control,dns:localhost" \
	-ext eku=sa,ca \
	-rfc \
	-infile control-center-server.csr \
	-outfile control-center-server.pem > /dev/null 2>&1

echo " => Import the server cert chain into control-center.server.keystore.jks"
keytool \
	-keystore control-center.server.keystore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias control-center-server \
	-noprompt \
	-file control-center-server.pem > /dev/null 2>&1

echo " => Import the server cert chain into control-center.server.truststore.jks"
keytool \
	-keystore control-center.server.truststore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-trustcacerts \
	-noprompt \
	-alias root \
	-file root.pem > /dev/null 2>&1
keytool \
	-keystore control-center.server.truststore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias ca \
	-file ca.pem > /dev/null 2>&1

echo "=> Appplication Client"

echo " => Generate the private keys (for application-client)"
keytool \
	-genkeypair \
	-alias application-client \
	-dname "CN=application-client, O=Home, OU=Home, L=Campinas, C=BR" \
	-validity 3650 \
	-keyalg RSA \
	-keysize 4096 \
	-keystore application.client.keystore.jks \
	-storetype jks \
	-keypass changeit \
	-storepass changeit > /dev/null 2>&1

echo " => Generate certificate for the client signed by ca (root -> ca -> application-client)"
keytool \
	-keystore application.client.keystore.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-certreq \
	-alias application-client \
	-file application-client.csr > /dev/null 2>&1
keytool \
	-keystore ca.jks \
	-storetype jks \
	-validity 3650 \
	-storepass changeit \
	-gencert \
	-alias ca \
	-ext ku:c=dig,keyEnc \
	-ext eku=sa,ca \
	-rfc \
	-infile application-client.csr \
	-outfile application-client.pem > /dev/null 2>&1

echo " => Import the client cert chain into application.client.keystore.jks"
keytool \
	-keystore application.client.keystore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias application-client \
	-noprompt \
	-file application-client.pem > /dev/null 2>&1

echo " => Import the client cert chain into application.client.truststore.jks"
keytool \
	-keystore application.client.truststore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-trustcacerts \
	-noprompt \
	-alias root \
	-file root.pem > /dev/null 2>&1
keytool \
	-keystore application.client.truststore.jks \
	-storetype jks \
	-storepass changeit \
	-importcert \
	-alias ca \
	-file ca.pem > /dev/null 2>&1

echo "=> Clean up"

echo " => Move files"

mkdir -p certificates/{root-ca,application,control-center,kafka,schema-registry}

mv -f root.* certificates/root-ca/.
mv -f ca.* certificates/root-ca/.

mv -f application*.* certificates/application/.

mv -f control-center*.* certificates/control-center/.

mv -f kafka*.* certificates/kafka/.
mv -f credentials certificates/kafka/.

mv -f schema-registry*.* certificates/schema-registry/.
