package core.framework.jpa.impl;

import core.framework.jpa.AggregateRoot;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * @author ebin
 */
@MappedSuperclass
public abstract class AbstractAggregateRoot<A extends AggregateRoot<A>> extends AbstractDomainEventAggregateRoot<A> {
    @NotNull
    @Column(name = "created_time")
    private ZonedDateTime createdTime = ZonedDateTime.now();

    @Override
    public ZonedDateTime getCreatedTime() {
        return createdTime;
    }
}
