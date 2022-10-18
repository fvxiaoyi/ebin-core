package core.framework.jpa.support;

import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class ConfigurableEntityManagerFactoryBean extends AbstractEntityManagerFactoryBean {
    private PersistenceUnitInfo persistenceUnitInfo;
    private EntityManagerCreator entityManagerCreator = new DefaultEntityManagerCreator();

    public ConfigurableEntityManagerFactoryBean(PersistenceUnitInfo persistenceUnitInfo) {
        this.persistenceUnitInfo = persistenceUnitInfo;
        this.setPersistenceUnitName(persistenceUnitInfo.getPersistenceUnitName());
    }

    public void setEntityManagerCreator(EntityManagerCreator entityManagerCreator) {
        this.entityManagerCreator = entityManagerCreator;
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

    @Override
    protected EntityManagerFactory createEntityManagerFactoryProxy(EntityManagerFactory emf) {
        Set<Class<?>> ifcs = new LinkedHashSet<>();
        if (emf != null) {
            ifcs.addAll(ClassUtils.getAllInterfacesForClassAsSet(emf.getClass(), getBeanClassLoader()));
        } else {
            ifcs.add(EntityManagerFactory.class);
        }
        ifcs.add(EntityManagerFactoryInfo.class);

        try {
            return (EntityManagerFactory) Proxy.newProxyInstance(getBeanClassLoader(),
                    ClassUtils.toClassArray(ifcs), new ManagedEntityManagerFactoryInvocationHandler(this));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Conflicting EntityManagerFactory interfaces - " +
                    "consider specifying the 'jpaVendorAdapter' or 'entityManagerFactoryInterface' property " +
                    "to select a specific EntityManagerFactory interface to proceed with", ex);
        }
    }

    protected Object invokeProxyMethod(Method method, @Nullable Object[] args) throws Throwable {
        if (method.getDeclaringClass().isAssignableFrom(EntityManagerFactoryInfo.class)) {
            return method.invoke(this, args);
        } else if (method.getName().equals("createEntityManager") && args != null && args.length > 0 &&
                args[0] == SynchronizationType.SYNCHRONIZED) {
            // JPA 2.1's createEntityManager(SynchronizationType, Map)
            // Redirect to plain createEntityManager and add synchronization semantics through Spring proxy
            EntityManager rawEntityManager = (args.length > 1 ?
                    getNativeEntityManagerFactory().createEntityManager((Map<?, ?>) args[1]) :
                    getNativeEntityManagerFactory().createEntityManager());
            postProcessEntityManager(rawEntityManager);
            return entityManagerCreator.createApplicationManagedEntityManager(rawEntityManager, this, true);
        }

        // Look for Query arguments, primarily JPA 2.1's addNamedQuery(String, Query)
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Query && Proxy.isProxyClass(arg.getClass())) {
                    // Assumably a Spring-generated proxy from SharedEntityManagerCreator:
                    // since we're passing it back to the native EntityManagerFactory,
                    // let's unwrap it to the original Query object from the provider.
                    try {
                        args[i] = ((Query) arg).unwrap(null);
                    } catch (RuntimeException ex) {
                        // Ignore - simply proceed with given Query object then
                    }
                }
            }
        }

        // Standard delegation to the native factory, just post-processing EntityManager return values
        Object retVal = method.invoke(getNativeEntityManagerFactory(), args);
        if (retVal instanceof EntityManager) {
            // Any other createEntityManager variant - expecting non-synchronized semantics
            EntityManager rawEntityManager = (EntityManager) retVal;
            postProcessEntityManager(rawEntityManager);
            retVal = entityManagerCreator.createApplicationManagedEntityManager(rawEntityManager, this, false);
        }
        return retVal;
    }

    private static class ManagedEntityManagerFactoryInvocationHandler implements InvocationHandler, Serializable {

        private final ConfigurableEntityManagerFactoryBean entityManagerFactoryBean;

        public ManagedEntityManagerFactoryInvocationHandler(ConfigurableEntityManagerFactoryBean emfb) {
            this.entityManagerFactoryBean = emfb;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals":
                    // Only consider equal when proxies are identical.
                    return (proxy == args[0]);
                case "hashCode":
                    // Use hashCode of EntityManagerFactory proxy.
                    return System.identityHashCode(proxy);
                case "unwrap":
                    // Handle JPA 2.1 unwrap method - could be a proxy match.
                    Class<?> targetClass = (Class<?>) args[0];
                    if (targetClass == null) {
                        return this.entityManagerFactoryBean.getNativeEntityManagerFactory();
                    } else if (targetClass.isInstance(proxy)) {
                        return proxy;
                    }
                    break;
            }

            try {
                return this.entityManagerFactoryBean.invokeProxyMethod(method, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}
