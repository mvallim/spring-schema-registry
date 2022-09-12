package org.springframework.schemaregistry.deserializer;

import example.avro.User;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.schemaregistry.serializer.AvroSerializer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AvroDeserializerTest {

  private final AvroSerializer avroSerializer = new AvroSerializer();

  private final AvroDeserializer avroDeserializer = new AvroDeserializer();

  @Test
  public void testSuccessDeserializer() throws IOException {
    final RecordHeaders headers = new RecordHeaders();
    final byte[] bs = avroSerializer.serialize("bogus", headers, createAvroRecord());
    final SpecificRecord specificRecord = avroDeserializer.deserialize("bogus", headers, bs);
    assertThat(specificRecord).isInstanceOf(User.class);
  }

  @Test
  public void testSuccessDeserializerNullData() throws IOException {
    final RecordHeaders headers = new RecordHeaders();
    final byte[] bs = avroSerializer.serialize("bogus", headers, null);
    final SpecificRecord specificRecord = avroDeserializer.deserialize("bogus", headers, bs);
    assertThat(specificRecord).isNull();
  }

  @Test
  public void testSuccessDeserializerRiseSerializationException() throws IOException {
    assertThatThrownBy(() -> avroDeserializer.deserialize("bogus", null, new byte[] { 0 }))
                       .isInstanceOf(SerializationException.class);

  }

  @Test
  public void testSuccessDeserializerRiseUnsupportedOperationException() throws IOException {
    assertThatThrownBy(() ->  avroDeserializer.deserialize("bogus", new byte[] { 0 }))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  private SpecificRecord createAvroRecord() throws IOException {
    final User avroRecord = new User();
    avroRecord.setName("Test user");
    return avroRecord;
  }

}
