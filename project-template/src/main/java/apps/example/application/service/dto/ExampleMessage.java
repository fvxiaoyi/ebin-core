package apps.example.application.service.dto;

import javax.validation.constraints.NotNull;

/**
 * @author ebin
 */
public class ExampleMessage {
    @NotNull
    public Long id;
    @NotNull
    public String name;
}
