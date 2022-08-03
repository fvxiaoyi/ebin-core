package apps.example.application;

import apps.bff.example.controller.request.CreateExampleRequest;
import apps.bff.example.controller.response.CreateExampleResponse;
import apps.example.domain.Example;
import apps.example.domain.service.CreateExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ebin
 */
@Service
public class CreateExampleAppService {
    @Autowired
    CreateExampleService createExampleService;

    @Transactional
    public CreateExampleResponse create(CreateExampleRequest request) {
        Example example = createExampleService.create(request.name);
        CreateExampleResponse response = new CreateExampleResponse();
        CreateExampleResponse.Example target = new CreateExampleResponse.Example();
        target.id = example.getId();
        target.name = example.getName();
        response.example = target;
        return response;
    }
}
