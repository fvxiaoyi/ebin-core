package core.framework.jpa.event;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;

/**
 * @author ebin
 */
interface DomainEventListener<T extends DomainEvent<? extends AggregateRoot<?>>> {
    void onEvent(T event);
}
