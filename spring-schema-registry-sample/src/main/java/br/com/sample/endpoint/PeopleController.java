package br.com.sample.endpoint;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sample.application.PeopleApplication;
import br.com.sample.model.People;
import br.com.sample.repository.PeopleRepository;

@RestController
@RequestMapping(path = "/api")
public class PeopleController {

	@Autowired
	PeopleRepository repo;

	@Autowired
	PeopleApplication peopleApplication;

	@GetMapping("/people")
	public List<People> findAll() {
		return this.peopleApplication.findAll();
	}

	@GetMapping("/people/{id}")
	public People findById(@PathVariable final String id) {
		return this.peopleApplication.findById(id);
	}

	@PostMapping("/people")
	public People save(@RequestBody final People people) {
		return this.peopleApplication.save(people);
	}

	@PutMapping(value = "/people/{id}")
	public People update(@PathVariable final String id, @RequestBody final People people) {
		return this.peopleApplication.update(id, people);
	}

	@DeleteMapping("/people/{id}")
	public void delete(@PathVariable final String id) {
		this.peopleApplication.delete(id);
	}
}
