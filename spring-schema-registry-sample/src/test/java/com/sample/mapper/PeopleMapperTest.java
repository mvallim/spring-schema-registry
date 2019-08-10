package com.sample.mapper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import br.com.sample.avro.People;
import br.com.sample.mapper.PeopleMapper;

public class PeopleMapperTest {

	@Test
	public void testMapAvroToModel() {

		final long time = new Date().getTime();

		final People peopleAvro = new People();

		peopleAvro.setId(UUID.randomUUID().toString());
		peopleAvro.setName("Marcos Vallim");
		peopleAvro.setMass(80);
		peopleAvro.setSkinColor("white");
		peopleAvro.setBirthYear("1980");
		peopleAvro.setCreated(time);
		peopleAvro.setEdited(time);
		peopleAvro.setEyeColor("brow");
		peopleAvro.setGender("male");
		peopleAvro.setHairColor("gray");
		peopleAvro.setHeight(186);

		final br.com.sample.model.People peopleModel = PeopleMapper.MAPPER.to(peopleAvro);

		assertThat(peopleModel.getId(), equalTo(peopleAvro.getId()));
		assertThat(peopleModel.getName(), equalTo(peopleAvro.getName()));
		assertThat(peopleModel.getMass(), equalTo(peopleAvro.getMass()));
		assertThat(peopleModel.getSkinColor(), equalTo(peopleAvro.getSkinColor()));
		assertThat(peopleModel.getBirthYear(), equalTo(peopleAvro.getBirthYear()));
		assertThat(peopleModel.getEyeColor(), equalTo(peopleAvro.getEyeColor()));
		assertThat(peopleModel.getGender(), equalTo(peopleAvro.getGender()));
		assertThat(peopleModel.getHairColor(), equalTo(peopleAvro.getHairColor()));
		assertThat(peopleModel.getHeight(), equalTo(peopleAvro.getHeight()));
		assertThat(peopleModel.getCreated().getTime(), equalTo(peopleAvro.getCreated()));
		assertThat(peopleModel.getEdited().getTime(), equalTo(peopleAvro.getEdited()));
	}

	@Test
	public void testModelToAvro() {

		final br.com.sample.model.People peopleModel = new br.com.sample.model.People();

		peopleModel.setId(UUID.randomUUID().toString());
		peopleModel.setName("Marcos Vallim");
		peopleModel.setMass(80);
		peopleModel.setSkinColor("white");
		peopleModel.setBirthYear("1980");
		peopleModel.setCreated(new Date());
		peopleModel.setEdited(new Date());
		peopleModel.setEyeColor("brow");
		peopleModel.setGender("male");
		peopleModel.setHairColor("gray");
		peopleModel.setHeight(186);

		final People peopleAvro = PeopleMapper.MAPPER.from(peopleModel);

		assertThat(peopleAvro.getId(), equalTo(peopleModel.getId()));
		assertThat(peopleAvro.getName(), equalTo(peopleModel.getName()));
		assertThat(peopleAvro.getMass(), equalTo(peopleModel.getMass()));
		assertThat(peopleAvro.getSkinColor(), equalTo(peopleModel.getSkinColor()));
		assertThat(peopleAvro.getBirthYear(), equalTo(peopleModel.getBirthYear()));
		assertThat(peopleAvro.getEyeColor(), equalTo(peopleModel.getEyeColor()));
		assertThat(peopleAvro.getGender(), equalTo(peopleModel.getGender()));
		assertThat(peopleAvro.getHairColor(), equalTo(peopleModel.getHairColor()));
		assertThat(peopleAvro.getHeight(), equalTo(peopleModel.getHeight()));
		assertThat(peopleAvro.getCreated(), equalTo(peopleModel.getCreated().getTime()));
		assertThat(peopleAvro.getEdited(), equalTo(peopleModel.getEdited().getTime()));
	}

}
