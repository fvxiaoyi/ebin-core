package core.framework.jpa.mongodb.impl;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.impl.AbstractAggregateRoot;
import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author ebin
 */
@MappedSuperclass
public class MongoDBAggregateRoot<A extends AggregateRoot<A>> extends AbstractAggregateRoot<A> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "objectid")
    public String id;

    @Override
    public String getId() {
        return id;
    }
}
