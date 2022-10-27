package apps.example.infrastructure.service.assembler;

import apps.example.application.service.dto.CreatedExampleResultDTO;
import apps.example.interfaces.service.response.GetExampleResponse;

/**
 * @author ebin
 */
public final class GetExampleResponseAssembler {
    private GetExampleResponseAssembler() {
    }

    public static GetExampleResponse of(CreatedExampleResultDTO example) {
        GetExampleResponse response = new GetExampleResponse();
        return response;
    }
}
