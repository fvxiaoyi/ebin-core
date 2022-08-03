package apps.example.domain.service;

import apps.example.domain.Example;
import apps.example.domain.event.ExampleCreatedEvent;
import apps.example.domain.exception.ExampleNameDupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ebin
 */
@Service
public class CreateExampleService {
    @Autowired
    ExampleRepo exampleRepo;

    public Example create(String name) {
        List<Example> examples = exampleRepo.selectByName(name);
        if (!examples.isEmpty()) {
            throw new ExampleNameDupException();
        }
        Example example = new Example(name);
        example.registerEvent(new ExampleCreatedEvent(example));
        exampleRepo.persist(example);
        return example;
    }
}
