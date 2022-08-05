package apps.example.interfaces.service;

import apps.example.interfaces.service.response.GetExampleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ebin
 */
public interface ExampleService {

    @GetMapping("/example/get/{id}")
    GetExampleResponse getExample(@PathVariable String id);
}
