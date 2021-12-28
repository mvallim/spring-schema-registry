package org.springframework.schemaregistry.deserializer;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.Test;
import org.springframework.schemaregistry.serializer.AvroSerializer;

import example.avro.User;

public class AvroDeserializerTest {

  private final AvroSerializer avroSerializer = new AvroSerializer();

  private final AvroDeserializer avroDeserializer = new AvroDeserializer();

  @Test
  public void testSuccessDeserializer() throws IOException {
    final RecordHeaders headers = new RecordHeaders();
    final byte[] bs = avroSerializer.serialize("bogus", headers, createAvroRecord());
    final SpecificRecord specificRecord = avroDeserializer.deserialize("bogus", headers, bs);
    assertThat(specificRecord, instanceOf(User.class));
  }

  @Test
  public void testSuccessDeserializerNullData() throws IOException {
    final RecordHeaders headers = new RecordHeaders();
    final byte[] bs = avroSerializer.serialize("bogus", headers, null);
    final SpecificRecord specificRecord = avroDeserializer.deserialize("bogus", headers, bs);
    assertThat(specificRecord, nullValue());
  }

  @Test
  public void testSuccessDeserializerRiseSerializationException() throws IOException {
    final Throwable throwable = catchThrowable(() -> avroDeserializer.deserialize("bogus", null, new byte[] { 0 }));
    assertThat(throwable, instanceOf(SerializationException.class));
  }

  @Test
  public void testSuccessDeserializerRiseUnsupportedOperationException() throws IOException {
    final Throwable throwable = catchThrowable(() -> avroDeserializer.deserialize("bogus", new byte[] { 0 }));
    assertThat(throwable, instanceOf(UnsupportedOperationException.class));
  }

  private SpecificRecord createAvroRecord() throws IOException {
    final User avroRecord = new User();
    avroRecord.setName("Test user");
    return avroRecord;
  }

}
