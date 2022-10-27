package apps.example.interfaces.controller;

import apps.example.interfaces.controller.request.CreateExampleRequest;
import apps.example.interfaces.controller.response.CreateExampleResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ebin
 */
@RequestMapping("/example")
public interface ExampleController {

    @PostMapping("/create")
    CreateExampleResponse create(@RequestBody CreateExampleRequest request);
}
