package com.framework.jpa.mysql.impl;

import core.framework.jpa.AggregateRoot;
import core.framework.jpa.impl.AbstractAggregateRoot;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author ebin
 */
@MappedSuperclass
public class MysqlAggregateRoot<A extends AggregateRoot<A>> extends AbstractAggregateRoot<A> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Override
    public Long getId() {
        return id;
    }
}
