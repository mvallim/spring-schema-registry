package br.com.sample.test.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.com.sample.test.model.People;

@Mapper(uses = ChaSequenceTransform.class)
public interface PeopleMapper {

	PeopleMapper MAPPER = Mappers.getMapper(PeopleMapper.class);

	@Mapping(target = "created", expression = "java( new java.util.Date( people.getCreated() ) )")
	@Mapping(target = "edited", expression = "java( new java.util.Date( people.getEdited() ) )")
	People to(br.com.sample.test.avro.People people);

	@Mapping(target = "created", expression = "java( people.getCreated().getTime() )")
	@Mapping(target = "edited", expression = "java( people.getEdited().getTime() )")
	br.com.sample.test.avro.People from(People people);
}
