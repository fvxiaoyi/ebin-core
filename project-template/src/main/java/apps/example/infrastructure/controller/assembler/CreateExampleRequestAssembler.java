package apps.example.infrastructure.controller.assembler;

import apps.example.application.service.dto.CreateExampleParam;
import apps.example.interfaces.controller.request.CreateExampleRequest;

/**
 * @author ebin
 */
public final class CreateExampleRequestAssembler {
    private CreateExampleRequestAssembler() {
    }

    public static CreateExampleParam of(CreateExampleRequest request) {
        CreateExampleParam createExampleParam = new CreateExampleParam();
        createExampleParam.name = request.name;
        return createExampleParam;
    }
}
