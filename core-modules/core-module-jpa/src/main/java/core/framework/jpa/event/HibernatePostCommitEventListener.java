package core.framework.jpa.event;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;
import org.hibernate.event.spi.PostCommitDeleteEventListener;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostCommitUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;

import java.util.List;

/**
 * @author ebin
 */
public class HibernatePostCommitEventListener implements PostCommitInsertEventListener, PostCommitUpdateEventListener, PostCommitDeleteEventListener {
    @Override
    public void onPostDeleteCommitFailed(PostDeleteEvent event) {
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostInsertCommitFailed(PostInsertEvent event) {
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostUpdateCommitFailed(PostUpdateEvent event) {
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        riseDomainEvent(event.getEntity());
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        riseDomainEvent(event.getEntity());
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        riseDomainEvent(event.getEntity());
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }

    private void riseDomainEvent(Object entity) {
        if (entity instanceof AggregateRoot) {
            AggregateRoot<?> aggregateRoot = (AggregateRoot<?>) entity;
            List<? extends DomainEvent<?>> domainEvents = aggregateRoot.getDomainEvents();
            for (DomainEvent<?> domainEvent : domainEvents) {
                DomainEventDispatcher.INSTANCE.publishPostCommitEvent(domainEvent);
            }
        }
    }

    private void cleanDomainEvent(Object entity) {
        if (entity instanceof AggregateRoot) {
            AggregateRoot<?> aggregateRoot = (AggregateRoot<?>) entity;
            aggregateRoot.clearDomainEvents();
        }
    }
}
