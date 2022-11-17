package com.framework.jpa.mysql;

import static com.framework.jpa.mysql.configuration.HibernateMysqlConfiguration.MYSQL_TRANSACTION_MANAGER_BEAN_NAME;

/**
 * @author ebin
 */
public final class Mysql {
    public static final String TRANSACTION_MANAGER_NAME = MYSQL_TRANSACTION_MANAGER_BEAN_NAME;

    private Mysql() {
    }
}
