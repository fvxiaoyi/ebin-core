package core.framework.jpa.event;

import core.framework.jpa.AggregateRoot;

import javax.persistence.EntityManager;

/**
 * @author ebin
 */
public interface DomainEventTrackingAdaptor {
    void persist(AggregateRoot<?> aggregateRoot, EntityManager entityManager);

    boolean support(AggregateRoot<?> aggregateRoot);
}
