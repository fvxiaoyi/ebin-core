package core.framework.jpa.impl;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author ebin
 */
public abstract class AbstractDomainEventAggregateRoot<A extends AggregateRoot<A>> implements AggregateRoot<A> {
    private final transient List<DomainEvent<A>> domainEvents = new ArrayList<>();

    @Override
    public DomainEvent<A> registerEvent(DomainEvent<A> event) {
        if (Objects.nonNull(event)) {
            this.domainEvents.add(event);
            return event;
        }
        return null;
    }

    @Override
    public List<DomainEvent<A>> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    @Override
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
