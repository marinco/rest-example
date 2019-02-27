package hr.fer.ilj.hw02.hw02.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import hr.fer.ilj.hw02.hw02.entities.Person;

@Projection(name = "short", types = {Person.class})
public interface PersonListProjection {
    @Value("#{target.firstName} #{target.lastName()}")
    String getName();
    Long getId();
}
