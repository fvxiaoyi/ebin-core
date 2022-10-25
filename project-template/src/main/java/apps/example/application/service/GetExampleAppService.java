package apps.example.application.service;

import apps.example.application.service.dto.ExampleDTO;
import apps.example.application.service.exception.ExampleNotFoundException;
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

    public ExampleDTO getExample(String id) {
        QueryCommand<ExampleDTO> command = new DefaultQueryCommand<>("example.get", ExampleDTO.class);
        command.addQueryParam("id", id);
        return queryService.get(command).orElseThrow(() -> new ExampleNotFoundException(id));
    }
}
