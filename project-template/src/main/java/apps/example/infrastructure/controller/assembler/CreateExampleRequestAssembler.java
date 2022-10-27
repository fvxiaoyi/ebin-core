package apps.example.infrastructure.controller.assembler;

import apps.example.application.service.dto.CreateExampleDTO;
import apps.example.interfaces.controller.request.CreateExampleRequest;

/**
 * @author ebin
 */
public final class CreateExampleRequestAssembler {
    private CreateExampleRequestAssembler() {
    }

    public static CreateExampleDTO of(CreateExampleRequest request) {
        CreateExampleDTO createExampleParam = new CreateExampleDTO();
        createExampleParam.name = request.name;
        return createExampleParam;
    }
}
