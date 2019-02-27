package hr.fer.ilj.hw02.hw02.repositories;

import hr.fer.ilj.hw02.hw02.entities.Course;
import hr.fer.ilj.hw02.hw02.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacher (Person teacher);
}
