package br.com.embedded;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.com.embedded.config.ApplicationConfig;

public class ApplicationTest {

	@Test
	public void testUpSuccess() throws Exception {
		final ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class);
		
		final Application application = applicationContext.getBean(Application.class);
		final ApplicationConfig applicationConfig = applicationContext.getBean(ApplicationConfig.class);
		
		assertThat(application, notNullValue());
		assertThat(applicationConfig, notNullValue());
		
		applicationConfig.destroy();
	}
	
	@Test(expected = BeanInitializationException.class)
	public void testUpFail() {
		SpringApplication.run(Application.class);
		SpringApplication.run(Application.class);
	}

}
