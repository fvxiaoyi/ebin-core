package apps.example.application.service;

import apps.example.domain.Example;
import apps.example.domain.service.CreateExampleService;
import apps.example.interfaces.controller.request.CreateExampleRequest;
import apps.example.interfaces.controller.response.CreateExampleResponse;
import com.framework.jpa.mysql.Mysql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ebin
 */
@Service
public class CreateExampleAppService {
    @Autowired
    private CreateExampleService createExampleService;

    @Transactional(Mysql.TRANSACTION_MANAGER_NAME)
    public CreateExampleResponse create(CreateExampleRequest request) {
        Example example = createExampleService.create(request.name);
        CreateExampleResponse response = new CreateExampleResponse();
        response.example = new CreateExampleResponse.Example();
        response.example.id = example.getId();
        response.example.name = example.getName();
        return response;
    }
}
