package hr.fer.ilj.hw02.hw02.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import hr.fer.ilj.hw02.hw02.entities.Person;

@RepositoryRestResource(excerptProjection = PersonListProjection.class)
public interface PersonRepository extends JpaRepository<Person, Long> {

}
