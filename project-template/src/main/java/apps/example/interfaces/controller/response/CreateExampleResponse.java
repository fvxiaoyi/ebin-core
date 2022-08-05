package apps.example.interfaces.controller.response;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author ebin
 */
public class CreateExampleResponse {
    @Valid
    @NotNull
    public Example example;

    public static class Example {
        @NotNull
        public Long id;

        @NotNull
        public String name;
    }
}
