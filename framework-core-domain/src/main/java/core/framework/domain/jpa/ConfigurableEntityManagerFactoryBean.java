package core.framework.domain.jpa;

import org.springframework.beans.BeanUtils;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

/**
 * @author ebin
 */
public class ConfigurableEntityManagerFactoryBean extends AbstractEntityManagerFactoryBean {
    private PersistenceUnitInfo persistenceUnitInfo;

    public ConfigurableEntityManagerFactoryBean(PersistenceUnitInfo persistenceUnitInfo) {
        this.persistenceUnitInfo = persistenceUnitInfo;
        this.setPersistenceUnitName(persistenceUnitInfo.getPersistenceUnitName());
    }

    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        Assert.state(this.persistenceUnitInfo != null, "PersistenceUnitInfo not initialized");

        PersistenceProvider provider = getPersistenceProvider();
        if (provider == null) {
            String providerClassName = this.persistenceUnitInfo.getPersistenceProviderClassName();
            if (providerClassName == null) {
                throw new IllegalArgumentException(
                        "No PersistenceProvider specified in EntityManagerFactory configuration, " +
                                "and chosen PersistenceUnitInfo does not specify a provider class name either");
            }
            Class<?> providerClass = ClassUtils.resolveClassName(providerClassName, getBeanClassLoader());
            provider = (PersistenceProvider) BeanUtils.instantiateClass(providerClass);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Building JPA container EntityManagerFactory for persistence unit '" +
                    this.persistenceUnitInfo.getPersistenceUnitName() + "'");
        }

        return provider.createContainerEntityManagerFactory(this.persistenceUnitInfo, getJpaPropertyMap());
    }

}
