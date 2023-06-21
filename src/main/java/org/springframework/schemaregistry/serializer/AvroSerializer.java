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

public class AvroSerializer implements Serializer<SpecificRecord> {

  @Override
  public byte[] serialize(final String topic, final SpecificRecord data) {
    throw new UnsupportedOperationException("Empty header");
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
        headers.add("schema", data.getSchema().toString().getBytes(StandardCharsets.UTF_8));
        return stream.toByteArray();
      } catch (final Exception ex) {
        throw new SerializationException("Can't serialize data='" + data + "' for topic='" + topic + "'", ex);
      }
    }
    return new byte[0];
  }

}