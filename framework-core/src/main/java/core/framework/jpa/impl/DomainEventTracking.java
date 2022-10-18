package core.framework.jpa.impl;

import core.framework.utils.json.JSON;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author ebin
 */
@Entity
@Table(name = "domain_event_tracking")
public class DomainEventTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "aggregate_root_class")
    private String aggregateRootClass;

    @NotNull
    @Column(name = "aggregate_root_id")
    private Long aggregateRootId;

    @Column(name = "payload")
    private String payload;

    @NotNull
    @Column(name = "created_time")
    private ZonedDateTime createdTime;

    private DomainEventTracking() {
    }

    public DomainEventTracking(AbstractDomainEvent<?> event) {
        this.aggregateRootClass = event.getSource().getClass().getTypeName();
        this.aggregateRootId = (Long) event.getSource().getId();
        this.createdTime = ZonedDateTime.now();
        if (Objects.nonNull(event.getPayload())) {
            this.payload = JSON.toJSON(event.getPayload());
        }
    }

    public Long getId() {
        return this.id;
    }

    public ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }

    public <T> T getPayload(Class<T> instanceClass) {
        return JSON.fromJSON(instanceClass, this.payload);
    }

    public String getAggregateRootClass() {
        return aggregateRootClass;
    }

    public Long getAggregateRootId() {
        return aggregateRootId;
    }

    public String getPayload() {
        return payload;
    }
}
