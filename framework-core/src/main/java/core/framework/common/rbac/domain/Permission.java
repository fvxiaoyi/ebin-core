package core.framework.common.rbac.domain;

import core.framework.domain.impl.AbstractAggregateRoot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author ebin
 */
@Entity
@Table(name = "permissions")
public class Permission extends AbstractAggregateRoot<Permission> {
    @NotNull
    @Column(name = "page_code")
    public String pageCode;

    @NotNull
    @Column(name = "page_name")
    public String pageName;

    @NotNull
    @Column(name = "action_code")
    public String actionCode;

    @NotNull
    @Column(name = "action_name")
    public String actionName;

    @Column(name = "note")
    private String note;

    private Permission() {
    }

    public Permission(String pageCode, String pageName,
                      String actionCode, String actionName,
                      String note) {
        this.pageCode = pageCode;
        this.pageName = pageName;
        this.actionCode = actionCode;
        this.actionName = actionName;
        this.note = note;
    }

    public String getPageCode() {
        return pageCode;
    }

    public String getPageName() {
        return pageName;
    }

    public String getActionCode() {
        return actionCode;
    }

    public String getActionName() {
        return actionName;
    }

    public String getNote() {
        return note;
    }
}
