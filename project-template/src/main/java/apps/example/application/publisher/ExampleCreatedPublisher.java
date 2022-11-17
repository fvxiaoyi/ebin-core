package apps.example.application.publisher;

import apps.example.interfaces.message.ExampleCreatedMessage;

/**
 * @author ebin
 */
public interface ExampleCreatedPublisher {
    void publish(ExampleCreatedMessage event);
}
