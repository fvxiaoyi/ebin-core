package apps.example.application.service;

import apps.example.application.service.dto.CreateExampleParam;
import apps.example.application.service.dto.ExampleDTO;
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
    private CreateExampleService createExampleService;

    @Transactional
    public ExampleDTO create(CreateExampleParam param) {
        Example example = createExampleService.create(param.name);
        ExampleDTO exampleDTO = new ExampleDTO();
        exampleDTO.id = example.getId();
        exampleDTO.name = example.getName();
        return exampleDTO;
    }
}
