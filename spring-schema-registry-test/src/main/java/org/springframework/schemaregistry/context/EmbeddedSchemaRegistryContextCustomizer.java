package org.springframework.schemaregistry.context;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.schemaregistry.EmbeddedSchemaRegistryServer;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.util.Assert;

class EmbeddedSchemaRegistryContextCustomizer implements ContextCustomizer {

	private final EmbeddedSchemaRegistry embeddedSchemaRegistry;

	public EmbeddedSchemaRegistryContextCustomizer(EmbeddedSchemaRegistry embeddedSchemaRegistry) {
		this.embeddedSchemaRegistry = embeddedSchemaRegistry;
	}

	@Override
	public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedContext) {
		final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
		Assert.isInstanceOf(DefaultSingletonBeanRegistry.class, beanFactory);

		final EmbeddedSchemaRegistryServer embeddedSchemaRegistryServer = new EmbeddedSchemaRegistryServer(
				this.embeddedSchemaRegistry.port(), this.embeddedSchemaRegistry.kafkastoreConnectionUrl());

		beanFactory.initializeBean(embeddedSchemaRegistryServer, EmbeddedSchemaRegistryServer.BEAN_NAME);
		beanFactory.registerSingleton(EmbeddedSchemaRegistryServer.BEAN_NAME, embeddedSchemaRegistryServer);
		((DefaultSingletonBeanRegistry) beanFactory).registerDisposableBean(EmbeddedSchemaRegistryServer.BEAN_NAME,
				embeddedSchemaRegistryServer);
	}
}
