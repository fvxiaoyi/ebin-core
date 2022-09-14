package core.framework.common.rbac.domain;

import core.framework.domain.impl.AbstractAggregateRoot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
@Entity
@Table(name = "roles")
public class Role extends AbstractAggregateRoot<Role> {
    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "permissions")
    private List<RolePermission> permissions = new ArrayList<>();

    @Column(name = "updated_time")
    private ZonedDateTime updatedTime;
}
