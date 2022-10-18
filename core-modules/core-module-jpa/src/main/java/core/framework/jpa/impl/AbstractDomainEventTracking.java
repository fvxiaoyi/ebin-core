package core.framework.jpa.impl;

import core.framework.json.JSON;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author ebin
 */
@MappedSuperclass
public abstract class AbstractDomainEventTracking {
    @NotNull
    @Column(name = "event_name")
    private String eventName;

    @NotNull
    @Column(name = "aggregate_root_class")
    private String aggregateRootClass;

    @NotNull
    @Column(name = "aggregate_root_id")
    private String aggregateRootId;

    @Column(name = "aggregate_root_snapshot")
    private String aggregateRootSnapshot;

    @Column(name = "payload")
    private String payload;

    @NotNull
    @Column(name = "created_time")
    private ZonedDateTime createdTime;

    private AbstractDomainEventTracking() {
    }

    public AbstractDomainEventTracking(AbstractDomainEvent<?> event) {
        this.aggregateRootClass = event.getSource().getClass().getTypeName();
        this.aggregateRootId = String.valueOf(event.getSource().getId());
        this.createdTime = ZonedDateTime.now();
        if (Objects.nonNull(event.getPayload())) {
            this.payload = JSON.toJSON(event.getPayload());
        }
        if (Objects.nonNull(event.getSource())) {
            this.aggregateRootSnapshot = JSON.toJSON(event.getSource());
        }
    }

    public abstract Object getId();

    public ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }

    public <T> T getPayload(Class<T> instanceClass) {
        return JSON.fromJSON(instanceClass, this.payload);
    }

    public String getAggregateRootClass() {
        return aggregateRootClass;
    }

}
