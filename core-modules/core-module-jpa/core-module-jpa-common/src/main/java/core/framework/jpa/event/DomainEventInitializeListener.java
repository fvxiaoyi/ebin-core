package core.framework.jpa.event;

import core.framework.jpa.configuration.DomainEventConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * @author ebin
 */
public class DomainEventInitializeListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        initializeDomainEventTrackingPersistentUnitHolder(applicationContext);
        initializeDomainEventDispatcher(applicationContext);
        registerEventListener(applicationContext);
    }

    private void initializeDomainEventDispatcher(ApplicationContext applicationContext) {
        ThreadPoolTaskExecutor taskExecutor = applicationContext.getBean(DomainEventConfiguration.DOMAIN_EVENT_TASK_EXECUTOR_NAME, ThreadPoolTaskExecutor.class);
        DomainEventDispatcher.INSTANCE.setTaskExecutor(taskExecutor);
    }

    private void initializeDomainEventTrackingPersistentUnitHolder(ApplicationContext applicationContext) {
        Map<String, EntityManagerFactory> entityManagerFactories = applicationContext.getBeansOfType(EntityManagerFactory.class);
        Map<String, DomainEventTrackingAdaptor> domainEventTrackingAdaptorMap = applicationContext.getBeansOfType(DomainEventTrackingAdaptor.class);
        DomainEventTrackingPersistentUnitHolder.INSTANCE.setManagerFactories(entityManagerFactories.values());
        DomainEventTrackingPersistentUnitHolder.INSTANCE.setDomainEventTrackingAdaptors(domainEventTrackingAdaptorMap.values());
    }

    @SuppressWarnings("rawtypes")
    private void registerEventListener(ApplicationContext applicationContext) {
        Map<String, DomainEventListener> listeners = applicationContext.getBeansOfType(DomainEventListener.class);
        listeners.forEach((k, v) -> DomainEventDispatcher.INSTANCE.registerEventListener(v));
    }
}
