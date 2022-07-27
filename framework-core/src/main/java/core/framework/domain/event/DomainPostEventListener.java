package core.framework.domain.event;

import core.framework.domain.AggregateRoot;
import core.framework.domain.DomainEvent;

/**
 * @author ebin
 */
public interface DomainPostEventListener<T extends DomainEvent<? extends AggregateRoot<?>>> extends DomainEventListener<T> {
    default boolean async() {
        return true;
    }
}
