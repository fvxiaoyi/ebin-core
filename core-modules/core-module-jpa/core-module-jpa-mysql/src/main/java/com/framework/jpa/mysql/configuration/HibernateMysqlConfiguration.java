package com.framework.jpa.mysql.configuration;

import com.framework.jpa.mysql.MySQLQueryInterceptor;
import com.framework.jpa.mysql.SpringHibernateJpaPersistenceProvider;
import com.framework.jpa.mysql.impl.MysqlDomainEventTracking;
import com.framework.jpa.mysql.impl.MysqlDomainEventTrackingAdaptor;
import com.mysql.cj.conf.PropertyKey;
import com.zaxxer.hikari.HikariDataSource;
import core.framework.jpa.configuration.HibernateJPAProperties;
import core.framework.jpa.event.DomainEventTrackingAdaptor;
import core.framework.jpa.support.ConfigurableEntityManagerFactoryBean;
import core.framework.jpa.support.ConfigurablePersistenceUnitInfo;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties({HibernateJPAProperties.class, HibernateMysqlProperties.class})
public class HibernateMysqlConfiguration {
    public static final int STATEMENT_FETCH_SIZE = 64;
    public static final String MYSQL_PERSISTENCE_UNIT_INFO_NAME = "mysql";
    public static final String MYSQL_PERSISTENCE_UNIT_INFO_BEAN_NAME = "mysqlPersistenceUnitInfo";
    public static final String MYSQL_ENTITY_MANAGER_FACTORY_BEAN_NAME = "mysqlEntityManagerFactory";
    public static final String MYSQL_TRANSACTION_MANAGER_BEAN_NAME = "mysqlTransactionManager";

    private final HibernateMysqlProperties jpaMysqlProperties;
    private final HibernateJPAProperties hibernateJPAProperties;

    public HibernateMysqlConfiguration(HibernateJPAProperties hibernateJPAProperties, HibernateMysqlProperties jpaMongodbProperties) {
        this.jpaMysqlProperties = jpaMongodbProperties;
        this.hibernateJPAProperties = hibernateJPAProperties;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        dataSource.setAutoCommit(false);
        dataSource.addDataSourceProperty(PropertyKey.queryInterceptors.getKeyName(), MySQLQueryInterceptor.class.getName());
        return dataSource;
    }

    @Bean(name = MYSQL_PERSISTENCE_UNIT_INFO_BEAN_NAME)
    public ConfigurablePersistenceUnitInfo mysqlPersistenceUnitInfo(DataSource dataSource, ObjectProvider<MysqlPersistenceUnitCustomizer> customizers) {
        Properties properties = new Properties();
        properties.put(AvailableSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true");
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.putIfAbsent(AvailableSettings.JPA_VALIDATION_MODE, ValidationMode.AUTO);
        properties.putIfAbsent(AvailableSettings.ISOLATION, Connection.TRANSACTION_READ_COMMITTED);
        properties.putIfAbsent(AvailableSettings.STATEMENT_FETCH_SIZE, STATEMENT_FETCH_SIZE);

        ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo = new ConfigurablePersistenceUnitInfo(MYSQL_PERSISTENCE_UNIT_INFO_NAME);
        configurablePersistenceUnitInfo.setBasePackagePath(hibernateJPAProperties.getBasePackagePath());
        configurablePersistenceUnitInfo.setPackagesToScan(jpaMysqlProperties.getPackagesToScan());
        configurablePersistenceUnitInfo.setPersistenceProviderClassName(SpringHibernateJpaPersistenceProvider.class.getName());
        configurablePersistenceUnitInfo.addManagedClassName(MysqlDomainEventTracking.class.getName());
        configurablePersistenceUnitInfo.setProperties(properties);
        configurablePersistenceUnitInfo.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        configurablePersistenceUnitInfo.setNonJtaDataSource(dataSource);

        customizers.orderedStream().forEach(customizer -> customizer.customize(configurablePersistenceUnitInfo));
        return configurablePersistenceUnitInfo;
    }

    @Bean(name = MYSQL_ENTITY_MANAGER_FACTORY_BEAN_NAME)
    public ConfigurableEntityManagerFactoryBean mysqlEntityManagerFactory(@Autowired @Qualifier(MYSQL_PERSISTENCE_UNIT_INFO_BEAN_NAME) ConfigurablePersistenceUnitInfo mysqlPersistenceUnitInfo) {
        return new ConfigurableEntityManagerFactoryBean(mysqlPersistenceUnitInfo);
    }

    @Bean(name = MYSQL_TRANSACTION_MANAGER_BEAN_NAME)
    public PlatformTransactionManager transactionManager(@Autowired @Qualifier(MYSQL_ENTITY_MANAGER_FACTORY_BEAN_NAME) EntityManagerFactory mysqlEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mysqlEntityManager);
        transactionManager.setDefaultTimeout(30);
        transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }

    @Bean
    public DomainEventTrackingAdaptor mysqlDomainEventTrackingAdaptor() {
        return new MysqlDomainEventTrackingAdaptor();
    }
}
