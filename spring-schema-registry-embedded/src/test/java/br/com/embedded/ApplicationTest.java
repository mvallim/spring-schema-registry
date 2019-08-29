package br.com.embedded;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.schemaregistry.EmbeddedSchemaRegistryServer;

public class ApplicationTest {

	@Test
	public void testUpSuccess() throws Exception {
		final ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, new String[] { "--brokers=1" });

		final Application application = applicationContext.getBean(Application.class);
		final EmbeddedKafkaBroker broker = applicationContext.getBean(EmbeddedKafkaBroker.class);
		final EmbeddedSchemaRegistryServer schemaRegistry = applicationContext.getBean(EmbeddedSchemaRegistryServer.class);

		assertThat(application, notNullValue());
		assertThat(broker, notNullValue());
		assertThat(schemaRegistry, notNullValue());

		schemaRegistry.destroy();
		broker.destroy();
	}

	@Test(expected = BeanCreationException.class)
	public void testUpFail() {
		SpringApplication.run(Application.class, new String[] { "--brokers=1" });
		SpringApplication.run(Application.class, new String[] { "--brokers=1" });
	}

}
