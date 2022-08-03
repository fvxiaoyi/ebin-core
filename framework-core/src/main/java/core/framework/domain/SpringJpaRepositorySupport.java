package core.framework.domain;

import core.framework.domain.impl.AbstractAggregateRoot;

import javax.persistence.EntityManager;

/**
 * @author ebin
 */
public interface SpringJpaRepositorySupport<T extends AbstractAggregateRoot<T>> {
    EntityManager getEntityManager();

    Class<T> getEntityClass();
}
