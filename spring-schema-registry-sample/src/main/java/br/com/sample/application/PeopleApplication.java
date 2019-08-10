package br.com.sample.application;

import java.util.List;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.sample.mapper.PeopleMapper;
import br.com.sample.model.People;
import br.com.sample.repository.PeopleRepository;

@Service
public class PeopleApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeopleApplication.class);

	@Autowired
	KafkaTemplate<String, GenericRecord> kafkaTemplate;

	@Autowired
	PeopleRepository peopleRepository;

	@KafkaListener(topics = "people")
	public void receive(final GenericRecord payload) {
		LOGGER.info("received payload='{}'", payload);
	}

	@Transactional
	public List<People> findAll() {
		return this.peopleRepository.findAll();
	}

	@Transactional
	public People findById(String id) {
		return this.peopleRepository.findById(id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public People save(final People people) {
		LOGGER.info("sending payload='{}'", people);

		final People result = this.peopleRepository.save(people);

		final ProducerRecord<String, GenericRecord> producerRecord = new ProducerRecord<>("people", people.getId(),
				PeopleMapper.MAPPER.from(people));

		this.kafkaTemplate.send(producerRecord);

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public People update(final String id, final People people) {
		return this.peopleRepository.update(id, people);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(final String id) {
		this.peopleRepository.delete(id);
	}
}