package core.framework.jpa.event;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;

/**
 * @author ebin
 */
public interface DomainPostEventListener<T extends DomainEvent<? extends AggregateRoot<?>>> extends DomainEventListener<T> {
    default boolean async() {
        return true;
    }
}
