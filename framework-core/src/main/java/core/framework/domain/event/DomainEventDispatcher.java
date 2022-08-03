package core.framework.domain.event;

import core.framework.domain.AggregateRoot;
import core.framework.domain.DomainEvent;
import core.framework.domain.impl.AbstractDomainEvent;
import core.framework.domain.impl.DomainEventTracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public final class DomainEventDispatcher {
    public static final DomainEventDispatcher INSTANCE = new DomainEventDispatcher();
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventDispatcher.class);
    private static final Map<String, Set<DomainPreEventListener<?>>> PRE_EVENT_LISTENERS = new ConcurrentHashMap<>();
    private static final Map<String, Set<DomainPostEventListener<?>>> POST_EVENT_LISTENERS = new ConcurrentHashMap<>();

    private EntityManager entityManager;
    private ThreadPoolTaskExecutor taskExecutor;

    private DomainEventDispatcher() {
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    protected void registerEventListener(DomainEventListener<?> listener) {
        String getGenericEventName = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
        if (listener instanceof DomainPreEventListener) {
            PRE_EVENT_LISTENERS.computeIfAbsent(
                    getGenericEventName,
                    k -> new LinkedHashSet<>()
            ).add((DomainPreEventListener<?>) listener);
        } else if (listener instanceof DomainPostEventListener) {
            POST_EVENT_LISTENERS.computeIfAbsent(
                    getGenericEventName,
                    k -> new LinkedHashSet<>()
            ).add((DomainPostEventListener<?>) listener);
        } else {
            throw new UnsupportedOperationException("Unsupported event type!");
        }
    }

    public <T extends AggregateRoot<T>> void publishPreCommitEvent(DomainEvent<T> event) {
        if (event instanceof AbstractDomainEvent) {
            entityManager.persist(new DomainEventTracking((AbstractDomainEvent) event));
        }
        Set<DomainPreEventListener<?>> domainEventListeners = PRE_EVENT_LISTENERS.get(event.getClass().getTypeName());
        if (domainEventListeners != null) {
            domainEventListeners.forEach(listener -> ((DomainPreEventListener<DomainEvent<T>>) listener).onEvent(event));
        }
    }

    public <T extends AggregateRoot<T>> void publishPostCommitEvent(DomainEvent<T> event) {
        Set<DomainPostEventListener<?>> domainEventListeners = POST_EVENT_LISTENERS.get(event.getClass().getTypeName());
        if (domainEventListeners != null) {
            domainEventListeners.forEach(listener -> {
                if (Objects.nonNull(taskExecutor) && listener.async()) {
                    taskExecutor.execute(() -> {
                        ((DomainPostEventListener<DomainEvent<T>>) listener).onEvent(event);
                    });
                } else {
                    try {
                        ((DomainPostEventListener<DomainEvent<T>>) listener).onEvent(event);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            });
        }
    }
}
