package org.springframework.schemaregistry;

import br.com.sample.test.application.PeopleApplication;
import br.com.sample.test.config.SenderConfig;
import br.com.sample.test.model.People;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.schemaregistry.deserializer.WrapperKafkaAvroDeserializer;
import org.springframework.schemaregistry.rule.EmbeddedSchemaRegistryRule;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SenderConfig.class })
@ComponentScan("br.com.sample")
@ActiveProfiles("test")
@ContextConfiguration(classes = { KafkaProperties.class }, initializers = ConfigFileApplicationContextInitializer.class)
@EnableConfigurationProperties
public class PeopleApplicationTest {

	@Autowired
	private PeopleApplication peopleApplication;

	@Value("${spring.kafka.properties.schema.registry.url}")
	private String schemaRegistryUrl;

	private BlockingQueue<ConsumerRecord<String, GenericRecord>> records;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, 1, "people");

	private static EmbeddedSchemaRegistryServer embeddedSchemaRegistryServer;

//	@ClassRule
//	public static EmbeddedSchemaRegistryRule embeddedSchemaRegistryRule = new EmbeddedSchemaRegistryRule(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString());

//	@Rule
//	public TestRule chain = RuleChain.outerRule());

	@BeforeClass
	public static void setupClass() throws Exception {
		final String kafkaBootstrapServers = embeddedKafka.getEmbeddedKafka().getBrokersAsString();
		System.setProperty("spring.embedded.kafka.brokers", kafkaBootstrapServers);

		embeddedSchemaRegistryServer = new EmbeddedSchemaRegistryServer(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString());
		embeddedSchemaRegistryServer.afterPropertiesSet();

		System.setProperty("spring.embedded.schema.registry", format("http://localhost:%s", embeddedSchemaRegistryServer.getPort()));
	}

	@Before
	public final void setupKafka() {
		// set up the Kafka consumer properties
		final Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("sender", "false",
				embeddedKafka.getEmbeddedKafka());
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, WrapperKafkaAvroDeserializer.class);
		consumerProperties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
		consumerProperties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, this.schemaRegistryUrl);

		// create a Kafka consumer factory
		final DefaultKafkaConsumerFactory<String, GenericRecord> consumerFactory = new DefaultKafkaConsumerFactory<>(
				consumerProperties);

		// set the topic that needs to be consumed
		final ContainerProperties containerProperties = new ContainerProperties("people");

		// create a Kafka MessageListenerContainer
		final KafkaMessageListenerContainer<String, GenericRecord> container = new KafkaMessageListenerContainer<>(
				consumerFactory, containerProperties);

		// create a thread safe queue to store the received message
		this.records = new LinkedBlockingQueue<>();

		// setup a Kafka message listener
		container.setupMessageListener((MessageListener<String, GenericRecord>) record -> {
			this.records.add(record);
		});

		// start the container and underlying message listener
		container.start();

		// wait until the container has the required number of assigned partitions
		ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
	}

	@Test
	public void test() {
		final Date time = new Date();

		final People people = new People();
		people.setId(UUID.randomUUID().toString());
		people.setName("Marcos Vallim");
		people.setMass(80);
		people.setSkinColor("white");
		people.setBirthYear("1980");
		people.setCreated(time);
		people.setEdited(time);
		people.setEyeColor("brow");
		people.setGender("male");
		people.setHairColor("gray");
		people.setHeight(186);

		this.peopleApplication.save(people);

		final br.com.sample.test.avro.People peopleResult = this.getMessageWithType(br.com.sample.test.avro.People.class);

		assertThat(peopleResult).isNotNull();
		assertThat(peopleResult.getId().toString()).isEqualTo(people.getId());
		assertThat(peopleResult.getName().toString()).isEqualTo(people.getName());
		assertThat(peopleResult.getMass()).isEqualTo(people.getMass());
		assertThat(peopleResult.getSkinColor().toString()).isEqualTo(people.getSkinColor());
		assertThat(peopleResult.getBirthYear().toString()).isEqualTo(people.getBirthYear());
		assertThat(peopleResult.getCreated()).isEqualTo(people.getCreated().getTime());
		assertThat(peopleResult.getEdited()).isEqualTo(people.getEdited().getTime());
		assertThat(peopleResult.getEyeColor().toString()).isEqualTo(people.getEyeColor());
		assertThat(peopleResult.getGender().toString()).isEqualTo(people.getGender());
		assertThat(peopleResult.getHairColor().toString()).isEqualTo(people.getHairColor());
		assertThat(peopleResult.getHeight()).isEqualTo(people.getHeight());
	}

	private <T> T getMessageWithType(Class<T> clazz) {
		ConsumerRecord<String, GenericRecord> received = null;
		try {
			received = this.records.poll(10, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			throw new RuntimeException("Error get record from Embedded Kafka", e);
		}

		return clazz.cast(received.value());
	}

	@AfterClass
	public static void afterClass() {
		embeddedSchemaRegistryServer.destroy();
	}
}
