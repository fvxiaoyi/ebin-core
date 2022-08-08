package apps.example.infrastructure.controller;

import apps.example.application.service.CreateExampleAppService;
import apps.example.interfaces.controller.ExampleController;
import apps.example.infrastructure.controller.assembler.CreateExampleRequestAssembler;
import apps.example.infrastructure.controller.assembler.CreateExampleResponseAssembler;
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
    private CreateExampleAppService createExampleAppService;

    @Override
    public CreateExampleResponse create(CreateExampleRequest request) {
        return CreateExampleResponseAssembler.of(
                createExampleAppService.create(
                        CreateExampleRequestAssembler.of(request)
                )
        );
    }

    @Override
    public void createV2(CreateExampleRequest request) {
        createExampleAppService.create(CreateExampleRequestAssembler.of(request));
    }
}
