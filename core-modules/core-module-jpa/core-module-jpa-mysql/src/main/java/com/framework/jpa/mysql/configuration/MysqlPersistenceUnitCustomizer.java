package com.framework.jpa.mysql.configuration;


import core.framework.jpa.support.ConfigurablePersistenceUnitInfo;

/**
 * @author ebin
 */
@FunctionalInterface
public interface MysqlPersistenceUnitCustomizer {
    void customize(ConfigurablePersistenceUnitInfo persistenceUnitInfo);
}
