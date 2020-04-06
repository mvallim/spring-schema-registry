package io.confluent.kafka.serializers;

import java.util.Map;

import org.apache.avro.Schema;

public final class WrapperAvroSchemaUtils {

  private WrapperAvroSchemaUtils() {
    super();
  }

  public static Schema copyOf(final Schema schema) {
    return AvroSchemaUtils.copyOf(schema);
  }

  public static Map<String, Schema> getPrimitiveSchemas() {
    return AvroSchemaUtils.getPrimitiveSchemas();
  }

  public static Schema getSchema(final Object object) {
    return AvroSchemaUtils.getSchema(object);
  }

}
