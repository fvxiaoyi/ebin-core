package apps.example.application.service;

import apps.example.application.publisher.ExampleCreatedPublisher;
import apps.example.domain.event.ExampleCreatedEvent;
import core.framework.jpa.event.DomainPostEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class HandleExampleCreatedService implements DomainPostEventListener<ExampleCreatedEvent> {
    @Autowired
    private ExampleCreatedPublisher publishExampleCreatedEvent;

    @Override
    public void onEvent(ExampleCreatedEvent event) {
        publishExampleCreatedEvent.publish(event);
    }
}
