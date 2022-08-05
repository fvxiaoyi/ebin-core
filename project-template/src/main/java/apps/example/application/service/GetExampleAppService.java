package apps.example.application.service;

import apps.example.application.service.exception.ExampleNotFoundException;
import apps.example.interfaces.service.response.GetExampleResponse;
import core.framework.db.query.JPAQueryCommand;
import core.framework.db.query.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class GetExampleAppService {
    @Autowired
    QueryService queryService;

    public GetExampleResponse getExample(String id) {
        JPAQueryCommand<GetExampleResponse.Example> command = new JPAQueryCommand<>("example.get", GetExampleResponse.Example.class);
        command.addQueryParam("id", id);
        GetExampleResponse.Example example = queryService.get(command).orElseThrow(() -> new ExampleNotFoundException(id));
        GetExampleResponse response = new GetExampleResponse();
        response.example = example;
        return response;
    }
}
