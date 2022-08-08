package apps.example.infrastructure.service.assembler;

import apps.example.application.service.dto.ExampleDTO;
import apps.example.interfaces.service.response.GetExampleResponse;

/**
 * @author ebin
 */
public final class GetExampleResponseAssembler {
    private GetExampleResponseAssembler() {
    }

    public static GetExampleResponse of(ExampleDTO example) {
        GetExampleResponse response = new GetExampleResponse();
        return response;
    }
}
