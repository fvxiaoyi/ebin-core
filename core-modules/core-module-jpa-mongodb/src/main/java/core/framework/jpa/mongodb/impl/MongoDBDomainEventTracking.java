package core.framework.jpa.mongodb.impl;

import core.framework.jpa.impl.AbstractDomainEvent;
import core.framework.jpa.impl.AbstractDomainEventTracking;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author ebin
 */
@Entity
@Table(name = "domain_event_tracking")
public class MongoDBDomainEventTracking extends AbstractDomainEventTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "objectid")
    public String id;

    public MongoDBDomainEventTracking(AbstractDomainEvent<?> event) {
        super(event);
    }

    @Override
    public String getId() {
        return id;
    }
}
