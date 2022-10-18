package com.framework.jpa.mysql.configuration;

import core.framework.jpa.configuration.HibernateJPAProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.jpa.mysql")
public class HibernateMysqlProperties extends HibernateJPAProperties {

}
