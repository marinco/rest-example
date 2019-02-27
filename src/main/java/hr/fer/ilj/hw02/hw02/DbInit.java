package hr.fer.ilj.hw02.hw02;

import hr.fer.ilj.hw02.hw02.entities.Course;
import hr.fer.ilj.hw02.hw02.entities.Person;
import hr.fer.ilj.hw02.hw02.repositories.CourseRepository;
import hr.fer.ilj.hw02.hw02.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DbInit implements CommandLineRunner {
    private PersonRepository personRepository;
    private CourseRepository courseRepository;

    @Autowired
    public DbInit(PersonRepository personRepository, CourseRepository courseRepository){
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception{
        System.out.println("DbInit.run");
        Person person1 = new Person("Bruno", "Blašković", "274", "C06-18");
        personRepository.save(person1);

        Person person2 = new Person("Mario", "Kušek", "301", "C08-12");
        personRepository.save(person2);

        Course courseIlj = new Course("ILJ", "Informacija, logika i jezici", person1);
        courseRepository.save(courseIlj);
        person1.getTeaching().add(courseIlj);
        personRepository.save(person1);

        Person person3 = new Person("Dragan", "Jevtić", "319", "C08-07");
        personRepository.save(person3);

        Course courseKM = new Course("KM", "Komunikacijske mreže", person3 );
        courseRepository.save(courseKM);
        person3.getTeaching().add(courseKM);
        personRepository.save(person3);
    }
}
