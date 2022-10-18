package apps.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.StringUtils;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author ebin
 */
//@Configuration
public class JPAConfig implements BeanFactoryAware {

    @Autowired
    private DataSource dataSource;

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Bean
    public ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo() {
        ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo = new ConfigurablePersistenceUnitInfo("mysql");
        configurablePersistenceUnitInfo.setNonJtaDataSource(dataSource);
        configurablePersistenceUnitInfo.setPackagesToScan(new String[]{"apps.*"});
        return configurablePersistenceUnitInfo;
    }

    @Bean
    public PersistenceUnitCustomizer persistenceUnitCustomizer1() {
        return new PersistenceUnitCustomizer() {
            @Override
            public String persistenceUnitName() {
                return "mysql";
            }

            @Override
            public String[] packagesToScan() {
                return new String[]{"apps.*"};
            }
        };
    }

    @Bean
    public ConfigurableEntityManagerFactoryBean configurableEntityManagerFactoryBean(ConfigurablePersistenceUnitInfo configurablePersistenceUnitInfo) {
        ConfigurableEntityManagerFactoryBean configurableEntityManagerFactoryBean = new ConfigurableEntityManagerFactoryBean(configurablePersistenceUnitInfo);
        configurableEntityManagerFactoryBean.setJpaVendorAdapter(createJpaVendorAdapter());
        configurableEntityManagerFactoryBean.setPersistenceUnitName("mysql");
        return configurableEntityManagerFactoryBean;
    }

    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }


//    @Bean
//    @Primary
//    @ConditionalOnMissingBean({LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class})
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder) {
//        return factoryBuilder.dataSource(this.dataSource).persistenceUnit("mysql").packages(getPackagesToScan()).build();
//    }

    protected String[] getPackagesToScan() {
        List<String> packages = EntityScanPackages.get(this.beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(this.beanFactory)) {
            packages = AutoConfigurationPackages.get(this.beanFactory);
        }
        return StringUtils.toStringArray(packages);
    }
}
