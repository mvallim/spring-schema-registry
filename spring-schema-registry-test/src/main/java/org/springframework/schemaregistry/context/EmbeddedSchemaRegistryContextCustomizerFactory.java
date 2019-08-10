package org.springframework.schemaregistry.context;

import java.util.List;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

class EmbeddedSchemaRegistryContextCustomizerFactory implements ContextCustomizerFactory {

	@Override
	public ContextCustomizer createContextCustomizer(Class<?> testClass,
			List<ContextConfigurationAttributes> configAttributes) {
		final EmbeddedSchemaRegistry embeddedSchemaRegistry = AnnotatedElementUtils.findMergedAnnotation(testClass,
				EmbeddedSchemaRegistry.class);
		return embeddedSchemaRegistry != null ? new EmbeddedSchemaRegistryContextCustomizer(embeddedSchemaRegistry)
				: null;
	}
}
