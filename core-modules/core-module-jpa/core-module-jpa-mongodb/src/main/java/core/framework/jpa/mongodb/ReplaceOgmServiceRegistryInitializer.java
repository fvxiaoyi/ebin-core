package core.framework.jpa.mongodb;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.spi.ServiceContributor;

/**
 * @author ebin
 */
public class ReplaceOgmServiceRegistryInitializer implements ServiceContributor {
    @Override
    public void contribute(StandardServiceRegistryBuilder serviceRegistryBuilder) {
        Boolean ogmEnabled = (Boolean) serviceRegistryBuilder.getSettings().get("hibernate.ogm.enabled");
        if (ogmEnabled != null && ogmEnabled) {
            serviceRegistryBuilder.addInitiator(ReplaceOgmSessionFactoryServiceRegistryFactoryInitiator.INSTANCE);
        }
    }
}
