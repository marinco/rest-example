package hr.fer.ilj.hw02.hw02.controllers;

import hr.fer.ilj.hw02.hw02.entities.Person;
import hr.fer.ilj.hw02.hw02.repositories.CourseRepository;
import hr.fer.ilj.hw02.hw02.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;

    @Autowired
    public PersonController(CourseRepository courseRepository, PersonRepository personRepository){
        this.courseRepository = courseRepository;
        this.personRepository = personRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Person> getPersons (){
        return personRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addPerson(@RequestBody Person person){
        Optional<Person> oldPerson = person.getId() == null ?
                Optional.empty() :
                personRepository.findById(person.getId());
        if ( oldPerson.isPresent() ){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        person = personRepository.save(person);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(person.getId())
                .toUri();
        return ResponseEntity.created(location).body(person);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{personId}")
    ResponseEntity<?> getPerson(@PathVariable Long personId){
        Optional<Person> person = personRepository.findById(personId);
        return person.isPresent() ? ResponseEntity.ok(person) : ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{personId}")
    ResponseEntity<?> deletePerson(@PathVariable Long personId){
        Optional<Person> person = personRepository.findById(personId);
        if ( !person.isPresent() ){
            return ResponseEntity.notFound().build();
        }
        //remove links from courses to teacher, unable to delete teacher otherwise
        courseRepository.saveAll(
            courseRepository.findByTeacher(person.get()).stream()
                .peek(c -> c.setTeacher(null))
                .collect(Collectors.toList())
        );
        personRepository.deleteById(personId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{personId}")
    ResponseEntity<?> updatePerson(@PathVariable Long personId, @RequestBody Person newPerson){
        Optional<Person> oldPerson = personRepository.findById(personId);
        if ( !oldPerson.isPresent() ){
            return addPerson(newPerson);
        }
        newPerson.setId(oldPerson.get().getId()); // can't change id
        newPerson.setTeaching(oldPerson.get().getTeaching()); // teaching changes are done through PUT at courses/courseId/teacher
        newPerson = personRepository.save(newPerson);
        return ResponseEntity.noContent().build();
    }
}
