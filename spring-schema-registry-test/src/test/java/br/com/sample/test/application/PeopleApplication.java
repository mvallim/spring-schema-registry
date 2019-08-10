package br.com.sample.test.application;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.sample.test.model.People;

@Service
public class PeopleApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeopleApplication.class);

	@Autowired
	private KafkaTemplate<String, GenericRecord> kafkaTemplate;

	public People save(final People people) {
		LOGGER.info("sending payload='{}'", people);

		final br.com.sample.test.avro.People peopleEvent = this.toEvent(people);

		final ProducerRecord<String, GenericRecord> producerRecord = new ProducerRecord<>("people", people.getId(),
				peopleEvent);

		this.kafkaTemplate.send(producerRecord);

		return people;
	}

	private br.com.sample.test.avro.People toEvent(People people) {
		return br.com.sample.test.avro.People.newBuilder()
				.setId(people.getId())
				.setName(people.getName())
				.setHeight(people.getHeight())
				.setMass(people.getMass())
				.setHairColor(people.getHairColor())
				.setSkinColor(people.getSkinColor())
				.setEyeColor(people.getEyeColor())
				.setBirthYear(people.getBirthYear())
				.setGender(people.getGender())
				.setEdited(people.getEdited().getTime())
				.setCreated(people.getCreated().getTime())
				.build();
	}

}