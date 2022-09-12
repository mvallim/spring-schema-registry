package org.springframework.schemaregistry.serializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.io.IOException;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.schemaregistry.deserializer.AvroDeserializer;

import example.avro.User;

public class AvroSerializerTest {

  private final AvroSerializer avroSerializer = new AvroSerializer();

  private final AvroDeserializer avroDeserializer = new AvroDeserializer();

  @Test
  public void testSuccessSerializer() throws IOException {
    final RecordHeaders headers = new RecordHeaders();
    final byte[] bs = avroSerializer.serialize("bogus", headers, createAvroRecord());
    final SpecificRecord specificRecord = avroDeserializer.deserialize("bogus", headers, bs);
    assertThat(specificRecord).isInstanceOf(User.class);
  }

  @Test
  public void testSuccessSerializerNullData() throws IOException {
    final RecordHeaders headers = new RecordHeaders();
    final byte[] bs = avroSerializer.serialize("bogus", headers, null);
    final SpecificRecord specificRecord = avroDeserializer.deserialize("bogus", headers, bs);
    assertThat(specificRecord).isNull();
  }

  @Test
  public void testSuccessSerializerRiseSerializationException() throws IOException {
    assertThatThrownBy(() ->  avroSerializer.serialize("bogus", null, createAvroRecord()))
        .isInstanceOf(SerializationException.class);
  }

  @Test
  public void testSuccessSerializerRiseUnsupportedOperationException() throws IOException {
    assertThatThrownBy(() ->  avroSerializer.serialize("bogus", createAvroRecord()))
            .isInstanceOf(UnsupportedOperationException.class);
  }

  private SpecificRecord createAvroRecord() throws IOException {
    final User avroRecord = new User();
    avroRecord.setName("Test user");
    return avroRecord;
  }

}
