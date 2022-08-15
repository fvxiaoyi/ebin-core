package apps.example.interfaces.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ebin
 */
public class CreateExampleRequest {
    @NotNull
    @NotBlank
    public String name;
}
