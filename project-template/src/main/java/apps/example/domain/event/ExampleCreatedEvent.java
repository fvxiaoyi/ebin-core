package apps.example.domain.event;

import apps.example.domain.Example;
import core.framework.domain.impl.AbstractDomainEvent;

/**
 * @author ebin
 */
public class ExampleCreatedEvent extends AbstractDomainEvent<Example> {
    public ExampleCreatedEvent(Example source) {
        super(source);
    }
}
