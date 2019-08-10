package br.com.sample.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.sample.model.People;

@Repository
public class PeopleRepository {

	private final Map<String, People> repo = new HashMap<>();

	public People findById(final String id) {
		return this.repo.get(id);
	}

	public List<People> findAll() {
		return new ArrayList<>(this.repo.values());
	}

	public People save(final People people) {
		final String id = UUID.randomUUID().toString();
		final Date currentDate = new Date();

		people.setId(id);
		people.setCreated(currentDate);
		people.setEdited(currentDate);
		this.repo.put(id, people);

		return people;
	}

	public People update(final String id, final People people) {
		people.setId(id);
		people.setCreated(this.repo.get(id).getCreated());
		people.setEdited(new Date());
		this.repo.put(id, people);

		return people;
	}

	public void delete(final String id) {
		this.repo.remove(id);
	}
}
