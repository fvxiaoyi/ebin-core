package apps.example.domain;

import core.framework.domain.impl.AbstractAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author ebin
 */
@Entity
@Table(name = "example")
public class Example extends AbstractAggregateRoot<Example> {

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
}