package apps.example.application.listener;

import apps.example.application.publisher.ExampleCreatedPublisher;
import apps.example.domain.event.ExampleCreatedEvent;
import core.framework.jpa.event.DomainPostEventListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ebin
 */
public class ExampleCreatedEventListener implements DomainPostEventListener<ExampleCreatedEvent> {
    @Autowired
    private ExampleCreatedPublisher publishExampleCreatedEvent;

    @Override
    public void onEvent(ExampleCreatedEvent event) {
        publishExampleCreatedEvent.publish(event);
    }
}
