package org.springframework.schemaregistry.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.ExtendedSerializer;

public class AvroSerializer implements ExtendedSerializer<SpecificRecord> {

  @Override
  public void configure(final Map<String, ?> configs, final boolean isKey) {
    // Auto-generated method stub
  }

  @Override
  public void close() {
    // Auto-generated method stub
  }

  @Override
  public byte[] serialize(final String topic, final SpecificRecord data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte[] serialize(final String topic, final Headers headers, final SpecificRecord data) {
    if (Objects.nonNull(data)) {
      try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
        final BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(stream, null);
        final DatumWriter<SpecificRecord> datumWriter = new SpecificDatumWriter<>(data.getSchema());
        datumWriter.setSchema(data.getSchema());
        datumWriter.write(data, binaryEncoder);
        binaryEncoder.flush();
        headers.add("schema", data.getSchema().toString().getBytes());
        return stream.toByteArray();
      } catch (final IOException ex) {
        throw new SerializationException("Can't serialize data='" + data + "' for topic='" + topic + "'", ex);
      }
    }
    return new byte[0];
  }

}