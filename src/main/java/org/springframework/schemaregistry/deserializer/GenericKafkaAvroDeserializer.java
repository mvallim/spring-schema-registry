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

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.common.errors.SerializationException;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.SneakyThrows;

public class GenericKafkaAvroDeserializer extends SpecificKafkaAvroDeserializer {

  private final ClassLoader classLoader = SpecificKafkaAvroDeserializer.class.getClassLoader();

  private final DecoderFactory decoderFactory = DecoderFactory.get();

  private final Map<String, Boolean> discoveredClasses = new ConcurrentHashMap<>();

  public GenericKafkaAvroDeserializer() {
    super();
  }

  public GenericKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistryClient) {
    super(schemaRegistryClient);
  }

  public GenericKafkaAvroDeserializer(final SchemaRegistryClient schemaRegistryClient, final Map<String, ?> configs) {
    super(schemaRegistryClient, configs);
  }

  @Override
  @SneakyThrows
  protected Object deserialize(final boolean includeSchemaAndVersion, final String topic, final Boolean isKey, final byte[] payload, final Schema readerSchema) throws SerializationException {

    final ByteBuffer buffer = ByteBuffer.wrap(payload);

    if (buffer.get() != MAGIC_BYTE) {
      throw new SerializationException("Unknown magic byte!");
    }

    final Schema schema = schemaRegistry.getById(buffer.getInt());

    final String fullName = schema.getFullName();

    if (constraintClass(fullName)) {
      return super.deserialize(includeSchemaAndVersion, topic, isKey, payload, readerSchema);
    } else {
      final int length = buffer.limit() - 1 - idSize;
      final int start = buffer.position() + buffer.arrayOffset();
      final DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
      return datumReader.read(null, decoderFactory.binaryDecoder(buffer.array(), start, length, null));
    }

  }

  private boolean constraintClass(final String fullName) {
    if (discoveredClasses.containsKey(fullName)) {
      return discoveredClasses.get(fullName);
    }

    try {
      Class.forName(fullName, false, classLoader);
      discoveredClasses.put(fullName, true);
    } catch (final ClassNotFoundException ex) {
      discoveredClasses.put(fullName, false);
    }

    return discoveredClasses.get(fullName);
  }

}