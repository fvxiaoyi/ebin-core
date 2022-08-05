package apps.example.infrastructure.service;

import apps.example.application.service.GetExampleAppService;
import apps.example.interfaces.service.ExampleService;
import apps.example.interfaces.service.response.GetExampleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ebin
 */
@RestController
public class ExampleRemoteService implements ExampleService {
    @Autowired
    GetExampleAppService getExampleAppService;

    @Override
    public GetExampleResponse getExample(String id) {
        return getExampleAppService.getExample(id);
    }
}
