package apps.example.domain;

import core.framework.jpa.impl.AbstractAggregateRoot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author ebin
 */
@Entity
@Table(name = "example")
public class Example extends AbstractAggregateRoot<Example> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    private String name;

    private Example() {
    }

    public Example(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Long getId() {
        return id;
    }
}