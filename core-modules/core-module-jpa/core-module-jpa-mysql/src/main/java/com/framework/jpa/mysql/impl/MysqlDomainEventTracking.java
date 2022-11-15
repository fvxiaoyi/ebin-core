package com.framework.jpa.mysql.impl;

import core.framework.jpa.impl.AbstractDomainEvent;
import core.framework.jpa.impl.AbstractDomainEventTracking;

import javax.persistence.Column;
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
public class MysqlDomainEventTracking extends AbstractDomainEventTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    public MysqlDomainEventTracking(AbstractDomainEvent<?> event) {
        super(event);
    }

    @Override
    public Long getId() {
        return id;
    }
}
