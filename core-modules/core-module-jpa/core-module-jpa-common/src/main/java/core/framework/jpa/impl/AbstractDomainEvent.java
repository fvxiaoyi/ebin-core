package core.framework.jpa.impl;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.DomainEvent;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public abstract class AbstractDomainEvent<T extends AggregateRoot<T>> implements DomainEvent<T> {
    private final T source;
    private final ZonedDateTime createdTime;
    private Object payload;

    public AbstractDomainEvent(T source) {
        this.source = source;
        this.createdTime = ZonedDateTime.now();
    }

    @Override
    public T getSource() {
        return this.source;
    }

    @Override
    public ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
