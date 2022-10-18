package core.framework.jpa.impl;

import core.framework.jpa.SpringJpaRepositorySupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author ebin
 */
public abstract class AbstractRepository<T extends AbstractAggregateRoot<T>> implements SpringJpaRepositorySupport<T> {
    @PersistenceContext
    private EntityManager entityManager;

    private final Class<T> entityClass;

    public AbstractRepository() {
        Type actualTypeArgument = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityClass = (Class<T>) actualTypeArgument;
    }

    @Override
    public Class<T> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
}
