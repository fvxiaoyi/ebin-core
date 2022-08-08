package apps.example.application.service;

import apps.example.application.service.dto.ExampleDTO;
import apps.example.application.service.exception.ExampleNotFoundException;
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
    private QueryService queryService;

    public ExampleDTO getExample(String id) {
        JPAQueryCommand<ExampleDTO> command = new JPAQueryCommand<>("example.get", ExampleDTO.class);
        command.addQueryParam("id", id);
        return queryService.get(command).orElseThrow(() -> new ExampleNotFoundException(id));
    }
}
