package core.framework.jpa.mongodb;

import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;

import java.util.Map;

/**
 * @author ebin
 */
public class ReplaceOgmSessionFactoryServiceRegistryFactoryInitiator implements StandardServiceInitiator<SessionFactoryServiceRegistryFactory> {

    public static final ReplaceOgmSessionFactoryServiceRegistryFactoryInitiator INSTANCE = new ReplaceOgmSessionFactoryServiceRegistryFactoryInitiator();

    @Override
    public Class<SessionFactoryServiceRegistryFactory> getServiceInitiated() {
        return SessionFactoryServiceRegistryFactory.class;
    }

    @Override
    public SessionFactoryServiceRegistryFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        return new ExtendOgmSessionFactoryServiceRegistryFactoryImpl(registry);
    }
}

