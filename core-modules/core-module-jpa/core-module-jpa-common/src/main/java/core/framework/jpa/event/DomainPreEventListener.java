package core.framework.jpa.event;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;

/**
 * @author ebin
 */
public interface DomainPreEventListener<T extends DomainEvent<? extends AggregateRoot<?>>> extends DomainEventListener<T> {
}
