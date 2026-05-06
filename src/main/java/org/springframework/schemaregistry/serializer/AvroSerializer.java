/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.schemaregistry.serializer;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Kafka serializer for Avro-specific records that includes schema information in message headers.
 * This serializer writes the Avro schema to the message headers under the "schema" key.
 */
public class AvroSerializer implements Serializer<SpecificRecord> {

  /**
   * Serializes Avro data without headers. This method is not supported.
   *
   * @param topic the topic to which the data will be sent
   * @param data the SpecificRecord to serialize
   * @return never returns as this method throws UnsupportedOperationException
   * @throws UnsupportedOperationException always thrown as headers are required
   */
  @Override
  public byte[] serialize(final String topic, final SpecificRecord data) {
    throw new UnsupportedOperationException("Empty header");
  }

  /**
   * Serializes Avro data and adds the schema to the message headers.
   *
   * @param topic the topic to which the data will be sent
   * @param headers the Kafka headers where the schema will be added
   * @param data the SpecificRecord to serialize
   * @return the serialized byte array, or empty array if data is null
   * @throws SerializationException if serialization fails
   */
  @Override
  public byte[] serialize(final String topic, final Headers headers, final SpecificRecord data) {
    if (Objects.nonNull(data)) {
      try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
        final BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(stream, null);
        final DatumWriter<SpecificRecord> datumWriter = new SpecificDatumWriter<>(data.getSchema());
        datumWriter.setSchema(data.getSchema());
        datumWriter.write(data, binaryEncoder);
        binaryEncoder.flush();
        headers.add("schema", data.getSchema().toString().getBytes(StandardCharsets.UTF_8));
        return stream.toByteArray();
      } catch (final Exception ex) {
        throw new SerializationException("Can't serialize data='" + data + "' for topic='" + topic + "'", ex);
      }
    }
    return new byte[0];
  }

}