package com.framework.jpa.mysql.impl;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;
import core.framework.jpa.event.DomainEventTrackingAdaptor;
import core.framework.jpa.impl.AbstractDomainEvent;

import javax.persistence.EntityManager;
import java.util.List;

import static com.framework.jpa.mysql.configuration.HibernateMysqlConfiguration.MYSQL_PERSISTENCE_UNIT_INFO_NAME;

/**
 * @author ebin
 */
public class MysqlDomainEventTrackingAdaptor implements DomainEventTrackingAdaptor {
    @Override
    public void persist(AggregateRoot<?> aggregateRoot, EntityManager entityManager) {
        List<? extends DomainEvent<?>> domainEvents = aggregateRoot.getDomainEvents();
        for (DomainEvent<?> domainEvent : domainEvents) {
            if (domainEvent instanceof AbstractDomainEvent) {
                MysqlDomainEventTracking mysqlDomainEventTracking = new MysqlDomainEventTracking((AbstractDomainEvent<?>) domainEvent);
                entityManager.persist(mysqlDomainEventTracking);
            }
        }
    }

    @Override
    public boolean support(String persistenceUnitName) {
        return MYSQL_PERSISTENCE_UNIT_INFO_NAME.equals(persistenceUnitName);
    }
}
