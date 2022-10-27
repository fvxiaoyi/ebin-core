package apps.example.interfaces.controller.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author ebin
 */
@Valid
public class CreateExampleRequest {
    @NotBlank
    public String name;
}
