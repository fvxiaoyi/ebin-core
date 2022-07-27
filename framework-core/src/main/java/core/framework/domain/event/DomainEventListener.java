package core.framework.domain.event;

import core.framework.domain.AggregateRoot;
import core.framework.domain.DomainEvent;

/**
 * @author ebin
 */
interface DomainEventListener<T extends DomainEvent<? extends AggregateRoot<?>>> {
    void onEvent(T event);
}
