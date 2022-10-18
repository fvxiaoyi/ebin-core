package core.framework.jpa.event;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class DomainEventTrackingPersistentUnitHolder {
    public static final DomainEventTrackingPersistentUnitHolder INSTANCE = new DomainEventTrackingPersistentUnitHolder();

    private Map<Class<?>, EntityManager> entityManagerHolders;
    private List<DomainEventTrackingAdaptor> domainEventTrackingAdaptors;

    private DomainEventTrackingPersistentUnitHolder() {
    }

    protected void setDomainEventTrackingAdaptors(Collection<DomainEventTrackingAdaptor> domainEventTrackingAdaptors) {
        this.domainEventTrackingAdaptors = new ArrayList<>(domainEventTrackingAdaptors);
    }

    protected void setEntityManagers(Collection<EntityManager> entityManagers) {
        Map<Class<?>, EntityManager> map = new HashMap<>(entityManagers.size());
        entityManagers.forEach(entityManager -> {
            entityManager.getMetamodel().getEntities().forEach(entityType -> {
                Class<?> javaType = entityType.getJavaType();
                if (AggregateRoot.class.isAssignableFrom(javaType)) {
                    map.put(javaType, entityManager);
                }
            });
        });
        this.entityManagerHolders = Collections.unmodifiableMap(map);
    }

    public void persist(AggregateRoot<?> aggregateRoot) {
        List<? extends DomainEvent<?>> domainEvents = aggregateRoot.getDomainEvents();
        if (domainEvents.isEmpty()) {
            return;
        }
        EntityManager entityManager = entityManagerHolders.get(aggregateRoot.getClass());
        if (entityManager != null) {
            domainEventTrackingAdaptors.stream().filter(f -> f.support(aggregateRoot)).findFirst().ifPresent(adaptor -> {
                adaptor.persist(aggregateRoot, entityManager);
            });
        }
    }

}
