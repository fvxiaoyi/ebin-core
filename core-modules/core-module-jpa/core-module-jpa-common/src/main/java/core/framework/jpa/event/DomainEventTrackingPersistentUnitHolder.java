package core.framework.jpa.event;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public final class DomainEventTrackingPersistentUnitHolder {
    public static final DomainEventTrackingPersistentUnitHolder INSTANCE = new DomainEventTrackingPersistentUnitHolder();

    private Map<String, EntityManager> entityManagers;
    private Map<Class<?>, String> aggregateRootPersistenceType;
    private List<DomainEventTrackingAdaptor> domainEventTrackingAdaptors;

    private DomainEventTrackingPersistentUnitHolder() {
    }

    void setDomainEventTrackingAdaptors(Collection<DomainEventTrackingAdaptor> domainEventTrackingAdaptors) {
        this.domainEventTrackingAdaptors = new ArrayList<>(domainEventTrackingAdaptors);
    }

    public void setManagerFactories(Collection<EntityManagerFactory> entityManagerFactories) {
        this.entityManagers = new HashMap<>(entityManagerFactories.size());
        this.aggregateRootPersistenceType = new HashMap<>();

        entityManagerFactories.forEach(entityManagerFactory -> {
            EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
            String persistenceUnitName = (String) entityManagerFactory.getProperties().get("hibernate.ejb.persistenceUnitName");
            this.entityManagers.put(persistenceUnitName, entityManager);

            entityManager.getMetamodel().getEntities().forEach(entityType -> {
                Class<?> javaType = entityType.getJavaType();
                if (AggregateRoot.class.isAssignableFrom(javaType)) {
                    this.aggregateRootPersistenceType.put(javaType, persistenceUnitName);
                }
            });
        });
    }

    public void persist(AggregateRoot<?> aggregateRoot) {
        List<? extends DomainEvent<?>> domainEvents = aggregateRoot.getDomainEvents();
        if (domainEvents.isEmpty()) {
            return;
        }
        String persistenceUnitName = aggregateRootPersistenceType.get(aggregateRoot.getClass());
        if (persistenceUnitName != null) {
            EntityManager entityManager = entityManagers.get(persistenceUnitName);
            domainEventTrackingAdaptors.stream().filter(f -> f.support(persistenceUnitName)).findFirst().ifPresent(adaptor -> {
                adaptor.persist(aggregateRoot, entityManager);
            });
        }
    }

}
