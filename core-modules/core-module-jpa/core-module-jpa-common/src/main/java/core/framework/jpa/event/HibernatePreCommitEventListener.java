package core.framework.jpa.event;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.util.List;

/**
 * @author ebin
 */
public class HibernatePreCommitEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        handleEntity(event.getEntity());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        handleEntity(event.getEntity());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        handleEntity(event.getEntity());
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return PostInsertEventListener.super.requiresPostCommitHandling(persister);
    }

    private void handleEntity(Object entity) {
        if (entity instanceof AggregateRoot) {
            AggregateRoot<?> aggregateRoot = (AggregateRoot<?>) entity;
            DomainEventTrackingPersistentUnitHolder.INSTANCE.persist(aggregateRoot);
            riseDomainEvent(aggregateRoot);
        }
    }

    private void riseDomainEvent(AggregateRoot<?> aggregateRoot) {
        List<? extends DomainEvent<?>> domainEvents = aggregateRoot.getDomainEvents();
        for (DomainEvent<?> domainEvent : domainEvents) {
            DomainEventDispatcher.INSTANCE.publishPreCommitEvent(domainEvent);
        }
    }
}
