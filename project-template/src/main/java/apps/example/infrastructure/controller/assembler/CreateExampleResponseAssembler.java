package apps.example.infrastructure.controller.assembler;

import apps.example.application.service.dto.ExampleDTO;
import apps.example.interfaces.controller.response.CreateExampleResponse;

/**
 * @author ebin
 */
public final class CreateExampleResponseAssembler {
    private CreateExampleResponseAssembler() {
    }

    public static CreateExampleResponse of(ExampleDTO example) {
        CreateExampleResponse response = new CreateExampleResponse();
        return response;
    }
}
