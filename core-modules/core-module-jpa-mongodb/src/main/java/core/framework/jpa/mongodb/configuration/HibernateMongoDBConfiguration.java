package core.framework.jpa.mongodb.configuration;

import core.framework.jpa.event.DomainEventTrackingAdaptor;
import core.framework.jpa.mongodb.HibernateMongoDBDatastoreProvider;
import core.framework.jpa.mongodb.HibernateMongoDBDialect;
import core.framework.jpa.mongodb.HibernateOGMEntityManagerCreator;
import core.framework.jpa.mongodb.HibernateOGMPersistenceProvider;
import core.framework.jpa.mongodb.impl.MongoDBDomainEventTracking;
import core.framework.jpa.mongodb.impl.MongoDBDomainEventTrackingAdaptor;
import core.framework.jpa.support.ConfigurableEntityManagerFactoryBean;
import core.framework.jpa.support.ConfigurablePersistenceUnitInfo;
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.ogm.datastore.mongodb.MongoDBProperties;
import org.hibernate.ogm.datastore.mongodb.options.ReadPreferenceType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;
import java.util.Properties;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.jpa.mongodb", name = "host")
@EnableConfigurationProperties({HibernateMongoDBProperties.class})
public class HibernateMongoDBConfiguration {
    public final static String MONGODB_PERSISTENCE_UNIT_INFO_NAME = "mongodb";
    public final static String MONGODB_PERSISTENCE_UNIT_INFO_BEAN_NAME = "mongodbPersistenceUnitInfo";
    public final static String MONGODB_ENTITY_MANAGER_FACTORY_BEAN_NAME = "mongodbEntityManagerFactory";
    public final static String MONGODB_TRANSACTION_MANAGER_BEAN_NAME = "mongodbTransactionManager";
    private final HibernateMongoDBProperties jpaMongodbProperties;

    public HibernateMongoDBConfiguration(HibernateMongoDBProperties jpaMongodbProperties) {
        this.jpaMongodbProperties = jpaMongodbProperties;
    }

    @Bean(name = MONGODB_PERSISTENCE_UNIT_INFO_BEAN_NAME)
    public ConfigurablePersistenceUnitInfo mongodbPersistenceUnitInfo(ObjectProvider<MongoDBPersistenceUnitCustomizer> customizers) {
        Properties properties = new Properties();
        properties.put(MongoDBProperties.DATASTORE_PROVIDER, HibernateMongoDBDatastoreProvider.class.getName());
        properties.put(MongoDBProperties.HOST, jpaMongodbProperties.getHost());
        properties.put(MongoDBProperties.DATABASE, jpaMongodbProperties.getDatabase());
        properties.put(MongoDBProperties.CREATE_DATABASE, jpaMongodbProperties.getCreateDatabase());

        if (StringUtils.hasText(jpaMongodbProperties.getAuthenticationDatabase())) {
            properties.put(MongoDBProperties.USERNAME, jpaMongodbProperties.getUsername());
            properties.put(MongoDBProperties.PASSWORD, jpaMongodbProperties.getPassword());
            properties.put(MongoDBProperties.AUTHENTICATION_DATABASE, jpaMongodbProperties.getAuthenticationDatabase());
        }

        properties.put(MongoDBProperties.READ_PREFERENCE, ReadPreferenceType.SECONDARY_PREFERRED);
        properties.put(MongoDBProperties.GRID_DIALECT, HibernateMongoDBDialect.class.getName());
        properties.put(AvailableSettings.TC_CLASSLOADER, TcclLookupPrecedence.BEFORE.toString());

        ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo = new ConfigurablePersistenceUnitInfo(MONGODB_PERSISTENCE_UNIT_INFO_NAME);
        configurablePersistenceUnitInfo.setBasePackagePath(jpaMongodbProperties.getBasePackagePath());
        configurablePersistenceUnitInfo.setPackagesToScan(jpaMongodbProperties.getPackagesToScan());
        configurablePersistenceUnitInfo.setPersistenceProviderClassName(HibernateOGMPersistenceProvider.class.getName());
        configurablePersistenceUnitInfo.addManagedClassName(MongoDBDomainEventTracking.class.getName());
        configurablePersistenceUnitInfo.setProperties(properties);
        configurablePersistenceUnitInfo.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);

        customizers.orderedStream().forEach(customizer -> customizer.customize(configurablePersistenceUnitInfo));
        return configurablePersistenceUnitInfo;
    }

    @Bean(name = MONGODB_ENTITY_MANAGER_FACTORY_BEAN_NAME)
    public ConfigurableEntityManagerFactoryBean mongodbEntityManagerFactory(@Autowired @Qualifier(MONGODB_PERSISTENCE_UNIT_INFO_BEAN_NAME) ConfigurablePersistenceUnitInfo mongodbPersistenceUnitInfo) {
        ConfigurableEntityManagerFactoryBean configurableEntityManagerFactoryBean = new ConfigurableEntityManagerFactoryBean(mongodbPersistenceUnitInfo);
        configurableEntityManagerFactoryBean.setEntityManagerCreator(new HibernateOGMEntityManagerCreator());
        return configurableEntityManagerFactoryBean;
    }

    @Bean(name = MONGODB_TRANSACTION_MANAGER_BEAN_NAME)
    public PlatformTransactionManager transactionManager(@Autowired @Qualifier(MONGODB_ENTITY_MANAGER_FACTORY_BEAN_NAME) EntityManagerFactory mongodbEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mongodbEntityManager);
        return transactionManager;
    }

    @Bean
    public DomainEventTrackingAdaptor mongoDBDomainEventTrackingAdaptor() {
        return new MongoDBDomainEventTrackingAdaptor();
    }
}
