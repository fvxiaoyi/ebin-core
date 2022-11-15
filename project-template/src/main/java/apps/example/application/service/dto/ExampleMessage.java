package apps.example.application.service.dto;

import javax.validation.constraints.NotNull;

/**
 * @author ebin
 */
public class ExampleMessage {
    @NotNull
    public String id;
    @NotNull
    public String name;

    @Override
    public String toString() {
        return "ExampleMessage{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
