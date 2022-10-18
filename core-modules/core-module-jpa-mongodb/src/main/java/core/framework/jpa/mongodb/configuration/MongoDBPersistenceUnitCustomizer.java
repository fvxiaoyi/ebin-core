package core.framework.jpa.mongodb.configuration;


import core.framework.jpa.support.ConfigurablePersistenceUnitInfo;

/**
 * @author ebin
 */
@FunctionalInterface
public interface MongoDBPersistenceUnitCustomizer {
    void customize(ConfigurablePersistenceUnitInfo persistenceUnitInfo);
}
