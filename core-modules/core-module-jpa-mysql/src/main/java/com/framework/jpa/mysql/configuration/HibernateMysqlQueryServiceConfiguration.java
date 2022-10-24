package com.framework.jpa.mysql.configuration;

import com.framework.jpa.mysql.query.HibernateMysqlQueryService;
import core.framework.query.QueryParser;
import core.framework.query.QueryService;
import core.framework.query.QueryType;
import core.framework.query.configuration.RouterQueryServiceCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.framework.jpa.mysql.configuration.HibernateMysqlConfiguration.MYSQL_PERSISTENCE_UNIT_INFO_NAME;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnBean(QueryParser.class)
public class HibernateMysqlQueryServiceConfiguration {
    public final static String HIBERNATE_MYSQL_QUERY_SERVICE = "hibernateMysqlQueryService";

    @PersistenceContext(unitName = MYSQL_PERSISTENCE_UNIT_INFO_NAME)
    private EntityManager entityManager;

    @Bean(name = HIBERNATE_MYSQL_QUERY_SERVICE)
    public QueryService hibernateMysqlQueryService(@Autowired QueryParser queryParser) {
        return new HibernateMysqlQueryService(entityManager, queryParser);
    }

    @Bean(name = "mysqlRouterQueryServiceCustomizer")
    public RouterQueryServiceCustomizer routerQueryServiceCustomizer(@Autowired @Qualifier(HIBERNATE_MYSQL_QUERY_SERVICE) QueryService hibernateMysqlQueryService) {
        return service -> service.addQueryService(QueryType.SQL, hibernateMysqlQueryService);
    }
}
