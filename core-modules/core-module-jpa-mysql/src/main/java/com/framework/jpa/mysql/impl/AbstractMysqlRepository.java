package com.framework.jpa.mysql.impl;

import core.framework.jpa.SpringJpaRepositorySupport;
import core.framework.jpa.impl.AbstractAggregateRoot;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.framework.jpa.mysql.configuration.HibernateMysqlConfiguration.MYSQL_PERSISTENCE_UNIT_INFO_NAME;

/**
 * @author ebin
 */
public abstract class AbstractMysqlRepository<T extends AbstractAggregateRoot<T>> implements SpringJpaRepositorySupport<T> {
    @PersistenceContext(unitName = MYSQL_PERSISTENCE_UNIT_INFO_NAME)
    private EntityManager entityManager;

    private final Class<T> entityClass;

    public AbstractMysqlRepository() {
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
