package org.springframework.schemaregistry.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EmbeddedSchemaRegistry {

	/**
	 * @return port from schema registry
	 */
	int port() default 8081;

	/**
	 * @return kafka zookeeper url
	 */
	String kafkastoreConnectionUrl() default "localhost:2181";

}
