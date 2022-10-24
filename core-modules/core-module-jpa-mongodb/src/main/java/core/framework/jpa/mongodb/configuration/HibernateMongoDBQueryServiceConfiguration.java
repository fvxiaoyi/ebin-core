package core.framework.jpa.mongodb.configuration;

import core.framework.jpa.mongodb.query.HibernateMongoDBQueryService;
import core.framework.query.QueryParser;
import core.framework.query.QueryService;
import core.framework.query.QueryType;
import core.framework.query.configuration.RouterQueryServiceCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static core.framework.jpa.mongodb.configuration.HibernateMongoDBConfiguration.MONGODB_PERSISTENCE_UNIT_INFO_NAME;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnBean(QueryParser.class)
public class HibernateMongoDBQueryServiceConfiguration {
    public final static String HIBERNATE_MONGODB_QUERY_SERVICE = "hibernateMongoDBQueryService";

    @PersistenceContext(unitName = MONGODB_PERSISTENCE_UNIT_INFO_NAME)
    private EntityManager entityManager;

    @Bean(name = HIBERNATE_MONGODB_QUERY_SERVICE)
    public QueryService hibernateMongoDBQueryService(@Autowired QueryParser queryParser) {
        return new HibernateMongoDBQueryService(entityManager, queryParser);
    }

    @Bean(name = "mongodbRouterQueryServiceCustomizer")
    public RouterQueryServiceCustomizer routerQueryServiceCustomizer(@Autowired @Qualifier(HIBERNATE_MONGODB_QUERY_SERVICE) QueryService hibernateMongoDBQueryService) {
        return service -> service.addQueryService(QueryType.NOSQL, hibernateMongoDBQueryService);
    }
}
