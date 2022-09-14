package core.framework.common.rbac.domain;

import core.framework.domain.impl.AbstractAggregateRoot;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author ebin
 */
public class User extends AbstractAggregateRoot<User> {
    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "slat")
    private String slat;

    @NotNull
    @Column(name = "roles")
    private List<UserRole> roles;

    @Column(name = "updated_time")
    private ZonedDateTime updatedTime;
}
