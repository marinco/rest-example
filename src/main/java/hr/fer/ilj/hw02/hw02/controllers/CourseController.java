package hr.fer.ilj.hw02.hw02.controllers;

import hr.fer.ilj.hw02.hw02.entities.Course;
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

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, PersonRepository personRepository){
        this.courseRepository = courseRepository;
        this.personRepository = personRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Course> getCourses(){
        return courseRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addCourse(@RequestBody Course course){
        Optional<Course> oldCourse = course.getId() == null ?
                Optional.empty() :
                courseRepository.findById(course.getId());

        if ( oldCourse.isPresent() ){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        course = courseRepository.save(course);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(course.getId())
                .toUri();
        return ResponseEntity.created(location).body(course);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{courseId}")
    ResponseEntity<?> getCourse(@PathVariable Long courseId){
        Optional<Course> course = courseRepository.findById(courseId);
        return course.isPresent() ? ResponseEntity.ok(course.get()) : ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{courseId}")
    ResponseEntity<?> deleteCourse(@PathVariable Long courseId){
        Optional<Course> course = courseRepository.findById(courseId);
        if ( !course.isPresent() ){
            return ResponseEntity.notFound().build();
        }
        courseRepository.deleteById(courseId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{courseId}")
    ResponseEntity<?> updateCourse(@PathVariable Long courseId, @RequestBody Course updatedCourse){
        Optional<Course> oldCourse = courseRepository.findById(courseId);
        if ( !oldCourse.isPresent() ){
            return addCourse(updatedCourse);
        }
        updatedCourse.setId(oldCourse.get().getId()); //can't edit id
        updatedCourse.setTeacher(oldCourse.get().getTeacher()); //teacher edits done through PUT at /courses/id/teacher
        courseRepository.save(updatedCourse);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{courseId}/teacher")
    ResponseEntity<?> getCourseTeacher(@PathVariable Long courseId){
        Optional<Course> course = courseRepository.findById(courseId);
        return course.isPresent() ?
                ResponseEntity.ok(course.get().getTeacher()) :
                ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{courseId}/teacher")
    ResponseEntity<?> deleteCourseTeacher(@PathVariable Long courseId){
        Optional<Course> course = courseRepository.findById(courseId);
        if ( !course.isPresent() ){
            return ResponseEntity.notFound().build();
        }
        Course updatedCourse = course.get();
        updatedCourse.setTeacher(null);
        courseRepository.save(updatedCourse);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{courseId}/teacher")
    ResponseEntity<?> updateCourseTeacher(@PathVariable Long courseId, @RequestBody String teacherLink){
        Optional<Course> course = courseRepository.findById(courseId);
        Optional<Person> teacher = personRepository.findById(Long.parseLong(teacherLink.substring(teacherLink.lastIndexOf('/') + 1)));
        if ( !course.isPresent() || !teacher.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Course updatedCourse = course.get();
        updatedCourse.setTeacher(teacher.get());
        courseRepository.save(updatedCourse);

        return ResponseEntity.noContent().build();
    }

}
