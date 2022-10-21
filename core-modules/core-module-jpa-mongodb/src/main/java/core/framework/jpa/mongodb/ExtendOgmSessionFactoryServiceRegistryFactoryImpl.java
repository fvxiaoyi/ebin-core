package core.framework.jpa.mongodb;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.ogm.service.impl.OgmSessionFactoryServiceInitiators;
import org.hibernate.service.internal.SessionFactoryServiceRegistryBuilderImpl;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceContributor;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;

/**
 * @author ebin
 */
public class ExtendOgmSessionFactoryServiceRegistryFactoryImpl implements SessionFactoryServiceRegistryFactory {

    private final ServiceRegistryImplementor theBasicServiceRegistry;

    public ExtendOgmSessionFactoryServiceRegistryFactoryImpl(ServiceRegistryImplementor theBasicServiceRegistry) {
        this.theBasicServiceRegistry = theBasicServiceRegistry;
    }

    @Override
    public SessionFactoryServiceRegistry buildServiceRegistry(SessionFactoryImplementor sessionFactory, BootstrapContext bootstrapContext, SessionFactoryOptions options) {
        final ClassLoaderService cls = options.getServiceRegistry().getService(ClassLoaderService.class);
        final SessionFactoryServiceRegistryBuilderImpl builder = new SessionFactoryServiceRegistryBuilderImpl(theBasicServiceRegistry);

        // Add the OGM services to the builder
        for (SessionFactoryServiceInitiator<?> initiator : OgmSessionFactoryServiceInitiators.LIST) {
            builder.addInitiator(initiator);
        }

        // replace NativeNoSqlQueryInterpreterInitiator
        builder.addInitiator(ReplaceNativeNoSqlQueryInterpreterInitiator.INSTANCE);

        for (SessionFactoryServiceContributor contributor : cls.loadJavaServices(SessionFactoryServiceContributor.class)) {
            contributor.contribute(builder);
        }

        return builder.buildSessionFactoryServiceRegistry(sessionFactory, bootstrapContext, options);
    }
}

