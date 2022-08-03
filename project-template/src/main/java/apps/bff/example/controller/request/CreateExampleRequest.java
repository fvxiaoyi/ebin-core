package apps.bff.example.controller.request;

import javax.validation.constraints.NotBlank;

/**
 * @author ebin
 */
public class CreateExampleRequest {
    @NotBlank
    public String name;
}
