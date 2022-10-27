package apps.example.infrastructure.controller.assembler;

import apps.example.application.service.dto.CreatedExampleResultDTO;
import apps.example.interfaces.controller.response.CreateExampleResponse;

/**
 * @author ebin
 */
public final class CreateExampleResponseAssembler {
    private CreateExampleResponseAssembler() {
    }

    public static CreateExampleResponse of(CreatedExampleResultDTO example) {
        CreateExampleResponse response = new CreateExampleResponse();
        return response;
    }
}
