package apps.example.application.service.dto;

import javax.validation.constraints.NotBlank;

/**
 * @author ebin
 */
public class CreateExampleParam {
    @NotBlank
    public String name;
}
