package apps.example.infrastructure.controller;

import apps.example.application.service.CreateExampleAppService;
import apps.example.interfaces.controller.ExampleController;
import apps.example.interfaces.controller.request.CreateExampleRequest;
import apps.example.interfaces.controller.response.CreateExampleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ebin
 */
@RestController
public class ExampleControllerImpl implements ExampleController {
    @Autowired
    CreateExampleAppService createExampleAppService;

    @Override
    public CreateExampleResponse create(CreateExampleRequest request) {
        return createExampleAppService.create(request);
    }

    @Override
    public void createV2(CreateExampleRequest request) {
        createExampleAppService.create(request);
    }
}
