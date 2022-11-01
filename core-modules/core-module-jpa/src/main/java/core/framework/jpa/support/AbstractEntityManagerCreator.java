package core.framework.jpa.support;

import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public abstract class AbstractEntityManagerCreator {
    private static final Map<Class<?>, Class<?>[]> CACHED_ENTITY_MANAGER_INTERFACES = new ConcurrentReferenceHashMap<>(4);

    public EntityManager createApplicationManagedEntityManager(EntityManager rawEntityManager,
                                                               EntityManagerFactoryInfo emfInfo,
                                                               boolean synchronizedWithTransaction) {
        Assert.notNull(emfInfo, "EntityManagerFactoryInfo must not be null");
        JpaDialect jpaDialect = emfInfo.getJpaDialect();
        PersistenceUnitInfo pui = emfInfo.getPersistenceUnitInfo();
        Boolean jta = pui != null ? pui.getTransactionType() == PersistenceUnitTransactionType.JTA : null;
        return createProxy(rawEntityManager, emfInfo.getEntityManagerInterface(),
                emfInfo.getBeanClassLoader(), jpaDialect, jta, synchronizedWithTransaction);
    }

    public EntityManager createProxy(EntityManager rawEm,
                                     @Nullable Class<? extends EntityManager> emIfc,
                                     @Nullable ClassLoader cl,
                                     @Nullable PersistenceExceptionTranslator exceptionTranslator,
                                     @Nullable Boolean jta,
                                     boolean synchronizedWithTransaction) {
        Assert.notNull(rawEm, "EntityManager must not be null");
        Class<?>[] interfaces;

        if (emIfc != null) {
            interfaces = CACHED_ENTITY_MANAGER_INTERFACES.computeIfAbsent(emIfc, key -> {
                if (EntityManagerProxy.class.equals(key)) {
                    return new Class<?>[]{key};
                }
                return new Class<?>[]{key, EntityManagerProxy.class};
            });
        } else {
            interfaces = CACHED_ENTITY_MANAGER_INTERFACES.computeIfAbsent(rawEm.getClass(), key -> {
                Set<Class<?>> ifcs = new LinkedHashSet<>(ClassUtils.getAllInterfacesForClassAsSet(key, cl));
                ifcs.add(EntityManagerProxy.class);
                return ClassUtils.toClassArray(ifcs);
            });
        }

        interfaces = customizeInterfaces(interfaces);

        return (EntityManager) Proxy.newProxyInstance(
                cl != null ? cl : AbstractEntityManagerCreator.class.getClassLoader(),
                interfaces,
                new ExtendedEntityManagerInvocationHandler(
                        rawEm, exceptionTranslator, jta, false, synchronizedWithTransaction));
    }

    protected abstract Class<?>[] customizeInterfaces(Class<?>[] interfaces);
}
