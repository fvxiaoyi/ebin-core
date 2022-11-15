package core.framework.jpa.mongodb;

import org.hibernate.EntityMode;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;

import java.util.Map;

/**
 * @author ebin
 */
public class HibernateOGMEntityManagerFactoryBuilderImpl extends EntityManagerFactoryBuilderImpl {
    public HibernateOGMEntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings) {
        super(persistenceUnit, integrationSettings);
    }

    public HibernateOGMEntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings, ClassLoader providedClassLoader) {
        super(persistenceUnit, integrationSettings, providedClassLoader);
    }

    public HibernateOGMEntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings, ClassLoaderService providedClassLoaderService) {
        super(persistenceUnit, integrationSettings, providedClassLoaderService);
    }

    @Override
    protected void populate(SessionFactoryBuilder sfBuilder, StandardServiceRegistry ssr) {
        super.populate(sfBuilder, ssr);
        sfBuilder.applyEntityTuplizer(EntityMode.POJO, HibernateOGMEntityTuplizer.class);
    }
}
