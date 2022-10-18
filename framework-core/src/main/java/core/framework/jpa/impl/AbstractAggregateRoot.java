package core.framework.jpa.impl;

import core.framework.jpa.AbstractDomainEventAggregateRoot;
import core.framework.jpa.AggregateRoot;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * @author ebin
 */
@MappedSuperclass
public abstract class AbstractAggregateRoot<A extends AggregateRoot<A>> extends AbstractDomainEventAggregateRoot<A> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_time")
    private final ZonedDateTime createdTime = ZonedDateTime.now();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public ZonedDateTime getCreatedTime() {
        return createdTime;
    }
}
