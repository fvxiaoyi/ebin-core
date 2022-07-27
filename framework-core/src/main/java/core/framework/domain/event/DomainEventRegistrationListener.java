package core.framework.domain.event;

import core.framework.configuration.HibernateJPAConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManager;
import java.util.Map;

/**
 * @author ebin
 */
public class DomainEventRegistrationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        initializeDomainEventDispatcher(applicationContext);
        registerEventListener(applicationContext);
    }

    private void initializeDomainEventDispatcher(ApplicationContext applicationContext) {
        ThreadPoolTaskExecutor taskExecutor = applicationContext.getBean(HibernateJPAConfiguration.DOMAIN_EVENT_TASK_EXECUTOR_NAME, ThreadPoolTaskExecutor.class);
        DomainEventDispatcher.INSTANCE.setTaskExecutor(taskExecutor);
        //todo choose entityManager
        EntityManager entityManager = applicationContext.getBean(EntityManager.class);
        DomainEventDispatcher.INSTANCE.setEntityManager(entityManager);
    }

    @SuppressWarnings("rawtypes")
    private void registerEventListener(ApplicationContext applicationContext) {
        Map<String, DomainEventListener> listeners = applicationContext.getBeansOfType(DomainEventListener.class);
        listeners.forEach((k, v) -> DomainEventDispatcher.INSTANCE.registerEventListener(v));
    }
}
