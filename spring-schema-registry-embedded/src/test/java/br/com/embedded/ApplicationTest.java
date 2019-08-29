package br.com.embedded;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.schemaregistry.EmbeddedSchemaRegistryServer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
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
	
	@Test
	public void testUpFailInvalidParameter() {
		final ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, new String[] { "--ahdsgjasgdahdk" });
		applicationContext.close();
	}
	
	@Test
	public void testUpSuccessValidParameter() {
		final ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, new String[] { "-b 1" });
		applicationContext.close();
	}

	@Test(expected = BeanCreationException.class)
	public void testUpFail() {
		SpringApplication.run(Application.class, new String[] { "--brokers=1" });
		SpringApplication.run(Application.class, new String[] { "--brokers=1" });
	}

	@Test
	public void testOptions() {
		assertTrue(Application.accept(new String[] { "--brokers=1" }));
		assertTrue(Application.accept(new String[] { "-b 1" }));
		assertTrue(Application.accept(new String[] { }));
		assertTrue(Application.accept(null));
		
		assertFalse(Application.accept(new String[] { "--dajskdhaksdjh" }));
		assertFalse(Application.accept(new String[] { "-h" }));
		assertFalse(Application.accept(new String[] { "--help" }));
		assertFalse(Application.accept(new String[] { "-h", "--help" }));
		assertFalse(Application.accept(new String[] { "--help", "-h", "-b 1" }));
	}

}
