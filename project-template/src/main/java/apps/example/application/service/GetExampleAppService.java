package apps.example.application.service;

import apps.example.application.service.dto.CreatedExampleResultDTO;
import apps.example.application.service.exception.ExampleNotFoundException;
import apps.example.interfaces.service.response.GetExampleResponse;
import core.framework.query.QueryCommand;
import core.framework.query.QueryService;
import core.framework.query.impl.command.DefaultQueryCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class GetExampleAppService {
    @Autowired
    private QueryService queryService;

    public GetExampleResponse getExample(String id) {
        QueryCommand<CreatedExampleResultDTO> command = new DefaultQueryCommand<>("example.get", CreatedExampleResultDTO.class);
        command.addQueryParam("id", id);
        CreatedExampleResultDTO createdExampleResultDTO = queryService.get(command).orElseThrow(() -> new ExampleNotFoundException(id));
        GetExampleResponse getExampleResponse = new GetExampleResponse();
        getExampleResponse.example = new GetExampleResponse.Example();
        getExampleResponse.example.id = createdExampleResultDTO.id;
        getExampleResponse.example.name = createdExampleResultDTO.name;
        return getExampleResponse;
    }
}
