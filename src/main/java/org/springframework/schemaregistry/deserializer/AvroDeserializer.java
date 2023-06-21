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

package org.springframework.schemaregistry.deserializer;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

public class AvroDeserializer implements Deserializer<SpecificRecord> {

  @Override
  public SpecificRecord deserialize(final String topic, final byte[] data) {
    throw new UnsupportedOperationException("Empty header");
  }

  @Override
  public SpecificRecord deserialize(final String topic, final Headers headers, final byte[] data) {
    if (ArrayUtils.isNotEmpty(data)) {
      try (final ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
        final Header header = headers.lastHeader("schema");
        final String stringSchema = new String(header.value());
        final Schema schema = new Schema.Parser().parse(stringSchema);
        final DatumReader<SpecificRecord> datumReader = new SpecificDatumReader<>(schema);
        final Decoder decoder = DecoderFactory.get().binaryDecoder(stream, null);
        return datumReader.read(null, decoder);
      } catch (final Exception ex) {
        throw new SerializationException("Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
      }
    }
    return null;
  }

}