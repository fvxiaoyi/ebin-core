package core.framework.jpa.mongodb.impl;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;
import core.framework.jpa.event.DomainEventTrackingAdaptor;
import core.framework.jpa.impl.AbstractDomainEvent;

import javax.persistence.EntityManager;
import java.util.List;

import static core.framework.jpa.mongodb.configuration.HibernateMongoDBConfiguration.MONGODB_PERSISTENCE_UNIT_INFO_NAME;

/**
 * @author ebin
 */
public class MongoDBDomainEventTrackingAdaptor implements DomainEventTrackingAdaptor {
    @Override
    public void persist(AggregateRoot<?> aggregateRoot, EntityManager entityManager) {
        List<? extends DomainEvent<?>> domainEvents = aggregateRoot.getDomainEvents();
        for (DomainEvent<?> domainEvent : domainEvents) {
            if (domainEvent instanceof AbstractDomainEvent) {
                MongoDBDomainEventTracking mongoDBDomainEventTracking = new MongoDBDomainEventTracking((AbstractDomainEvent<?>) domainEvent);
                entityManager.persist(mongoDBDomainEventTracking);
            }
        }
    }

    @Override
    public boolean support(String persistenceUnitName) {
        return MONGODB_PERSISTENCE_UNIT_INFO_NAME.equals(persistenceUnitName);
    }
}
