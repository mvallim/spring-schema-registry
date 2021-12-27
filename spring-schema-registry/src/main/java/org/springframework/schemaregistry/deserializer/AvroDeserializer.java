package org.springframework.schemaregistry.deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.ExtendedDeserializer;

public class AvroDeserializer implements ExtendedDeserializer<SpecificRecord> {

  @Override
  public void configure(final Map<String, ?> configs, final boolean isKey) {
    // Auto-generated method stub
  }

  @Override
  public void close() {
    // Auto-generated method stub
  }

  @Override
  public SpecificRecord deserialize(final String topic, final byte[] data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public SpecificRecord deserialize(final String topic, final Headers headers, final byte[] data) {
    if (Objects.nonNull(data)) {
      try (final ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
        final Header header = headers.lastHeader("schema");
        final String stringSchema = new String(header.value());
        final Schema schema = new Schema.Parser().parse(stringSchema);
        final DatumReader<SpecificRecord> datumReader = new SpecificDatumReader<>(schema);
        final Decoder decoder = DecoderFactory.get().binaryDecoder(stream, null);
        return datumReader.read(null, decoder);
      } catch (final IOException ex) {
        throw new SerializationException("Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
      }
    }
    return null;
  }

}