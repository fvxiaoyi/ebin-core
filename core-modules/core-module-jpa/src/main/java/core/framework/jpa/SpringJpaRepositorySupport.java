package core.framework.jpa;

import core.framework.jpa.impl.AbstractAggregateRoot;

import javax.persistence.EntityManager;

/**
 * @author ebin
 */
public interface SpringJpaRepositorySupport<T extends AbstractAggregateRoot<T>> {
    EntityManager getEntityManager();

    Class<T> getEntityClass();
}
