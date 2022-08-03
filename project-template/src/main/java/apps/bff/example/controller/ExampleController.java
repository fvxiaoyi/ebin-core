package apps.bff.example.controller;

import apps.bff.example.controller.request.CreateExampleRequest;
import apps.bff.example.controller.response.CreateExampleResponse;
import apps.example.application.CreateExampleAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ebin
 */
@RestController
@RequestMapping("/example")
public class ExampleController {
    @Autowired
    CreateExampleAppService createExampleAppService;

    @PostMapping("/create")
    public CreateExampleResponse create(CreateExampleRequest request) {
        return createExampleAppService.create(request);
    }

    @PostMapping("/create/v2")
    public void createV2(CreateExampleRequest request) {
        createExampleAppService.create(request);
    }
}
