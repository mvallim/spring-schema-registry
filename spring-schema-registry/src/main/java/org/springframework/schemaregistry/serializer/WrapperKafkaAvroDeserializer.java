package org.springframework.schemaregistry.serializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLSocketFactory;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.schemaregistry.core.SchemaRegistrySSLSocketFactory;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.GenericContainerWithVersion;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.NonRecordContainer;
import io.confluent.kafka.serializers.WrapperAvroSchemaUtils;

public class WrapperKafkaAvroDeserializer extends KafkaAvroDeserializer implements Deserializer<Object> {

  private final DecoderFactory decoderFactory = DecoderFactory.get();

  private final Map<String, Schema> readerSchemaCache = new ConcurrentHashMap<String, Schema>();

  public WrapperKafkaAvroDeserializer() {
    super();
  }

  public WrapperKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistryClient) {
    super(schemaRegistryClient);
  }

  public WrapperKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistryClient, final Map<String, ?> configs) {
    super(schemaRegistryClient, configs);
  }

  @Override
  public void configure(final Map<String, ?> configs, final boolean isKey) {

    if (Objects.isNull(schemaRegistry)) {

      final AbstractKafkaAvroSerDeConfig deserializerConfig = new KafkaAvroDeserializerConfig(configs);

      final RestService restService = new RestService(deserializerConfig.getSchemaRegistryUrls());

      final SSLSocketFactory sslSocketFactory = SchemaRegistrySSLSocketFactory.createSslSocketFactory(configs);

      if (Objects.nonNull(sslSocketFactory)) {
        restService.setSslSocketFactory(sslSocketFactory);
      }

      final int maxSchemaObject = deserializerConfig.getMaxSchemasPerSubject();
      final Map<String, Object> originals = deserializerConfig.originalsWithPrefix("");

      schemaRegistry = new CachedSchemaRegistryClient(restService, maxSchemaObject, originals);
    }

    super.configure(configs, isKey);
  }

  @Override
  protected Object deserialize(final boolean includeSchemaAndVersion, final String topic, final Boolean isKey, final byte[] payload, final Schema readerSchema)
      throws SerializationException {

    if (Objects.isNull(payload)) {
      return null;
    }

    int id = -1;

    try {
      final ByteBuffer buffer = getByteBuffer(payload);
      id = buffer.getInt();
      Schema schema = schemaRegistry.getById(id);
      String subject = null;

      if (includeSchemaAndVersion) {
        subject = subjectName(topic, isKey, schema);
        schema = schemaForDeserialize(id, schema, subject, isKey);
      }

      final int length = buffer.limit() - 1 - idSize;
      final Object result;

      if (schema.getType().equals(Schema.Type.BYTES)) {
        final byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);
        result = bytes;
      } else {
        final int start = buffer.position() + buffer.arrayOffset();
        final DatumReader<?> reader = getDatumReader(schema, readerSchema);
        Object object = reader.read(null, decoderFactory.binaryDecoder(buffer.array(), start, length, null));

        if (schema.getType().equals(Schema.Type.STRING)) {
          object = object.toString(); // Utf8 -> String
        }
        result = object;
      }

      if (includeSchemaAndVersion) {
        final Integer version = schemaVersion(topic, isKey, id, subject, schema, result);

        if (schema.getType().equals(Schema.Type.RECORD)) {
          return new GenericContainerWithVersion((GenericContainer) result, version);
        } else {
          return new GenericContainerWithVersion(new NonRecordContainer(schema, result), version);
        }
      } else {
        return result;
      }
    } catch (IOException | RuntimeException e) {
      throw new SerializationException("Error deserializing Avro message for id " + id, e);
    } catch (final RestClientException e) {
      throw new SerializationException("Error retrieving Avro schema for id " + id, e);
    }
  }

  private ByteBuffer getByteBuffer(final byte[] payload) {
    final ByteBuffer buffer = ByteBuffer.wrap(payload);
    if (buffer.get() != MAGIC_BYTE) {
      throw new SerializationException("Unknown magic byte!");
    }
    return buffer;
  }

  private String subjectName(final String topic, final Boolean isKey, final Schema schemaFromRegistry) {
    return isDeprecatedSubjectNameStrategy(isKey) ? null : getSubjectName(topic, isKey, null, schemaFromRegistry);
  }

  private Schema schemaForDeserialize(final int id, final Schema schemaFromRegistry, final String subject, final Boolean isKey) throws IOException, RestClientException {
    return isDeprecatedSubjectNameStrategy(isKey) ? WrapperAvroSchemaUtils.copyOf(schemaFromRegistry) : schemaRegistry.getBySubjectAndId(subject, id);
  }

  private Integer schemaVersion(final String topic, final Boolean isKey, final int id, String subject, final Schema schema, final Object result)
      throws IOException, RestClientException {
    if (isDeprecatedSubjectNameStrategy(isKey)) {
      subject = getSubjectName(topic, isKey, result, schema);
      final Schema subjectSchema = schemaRegistry.getBySubjectAndId(subject, id);
      return schemaRegistry.getVersion(subject, subjectSchema);
    } else {
      return schemaRegistry.getVersion(subject, schema);
    }
  }

  private DatumReader<?> getDatumReader(final Schema writerSchema, final Schema readerSchema) {

    final boolean writerSchemaIsPrimitive = WrapperAvroSchemaUtils.getPrimitiveSchemas().values().contains(writerSchema);

    if (useSpecificAvroReader && !writerSchemaIsPrimitive) {
      if (Objects.isNull(readerSchema)) {
        final Schema schema = getReaderSchema(writerSchema);
        if (Objects.isNull(schema)) {
          return new GenericDatumReader<>(writerSchema);
        }
        return new SpecificDatumReader<>(writerSchema, schema);
      }
      return new SpecificDatumReader<>(writerSchema, readerSchema);
    }

    if (Objects.isNull(readerSchema)) {
      return new GenericDatumReader<>(writerSchema);
    }

    return new GenericDatumReader<>(writerSchema, readerSchema);
  }

  @SuppressWarnings("unchecked")
  private Schema getReaderSchema(final Schema writerSchema) {

    if (Objects.isNull(readerSchemaCache.get(writerSchema.getFullName()))) {
      Optional.<Class<SpecificRecord>>ofNullable(SpecificData.get().getClass(writerSchema)).ifPresent(readerClass -> {
        try {
          final Schema readerSchema = readerClass.newInstance().getSchema();
          readerSchemaCache.put(writerSchema.getFullName(), readerSchema);
        } catch (final InstantiationException e) {
          throw new SerializationException(writerSchema.getFullName() + " specified by the " + "writers schema could not be instantiated to " + "find the readers schema.");
        } catch (final IllegalAccessException e) {
          throw new SerializationException(writerSchema.getFullName() + " specified by the " + "writers schema is not allowed to be instantiated " + "to find the readers schema.");
        }
      });
    }

    return readerSchemaCache.get(writerSchema.getFullName());
  }

}