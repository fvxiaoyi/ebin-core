package apps.example.interfaces.controller;

import apps.example.interfaces.controller.request.CreateExampleRequest;
import apps.example.interfaces.controller.response.CreateExampleResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ebin
 */
@RequestMapping("/example")
public interface ExampleController {

    @PostMapping("/create")
    CreateExampleResponse create(CreateExampleRequest request);

    @PostMapping("/create/v2")
    void createV2(CreateExampleRequest request);
}
