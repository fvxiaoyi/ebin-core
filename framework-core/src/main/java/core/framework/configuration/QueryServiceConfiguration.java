package core.framework.configuration;

import core.framework.db.query.JPAQueryService;
import core.framework.db.query.MyBatisQueryParser;
import core.framework.db.query.QueryParser;
import core.framework.db.query.QueryService;
import core.framework.db.query.SqlQueryServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Validator;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties({SqlQueryServiceProperties.class})
public class QueryServiceConfiguration {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public QueryService jpaSqlQueryService(@Autowired QueryParser queryParser, @Autowired Validator validator) {
        return new JPAQueryService(entityManager, queryParser, validator);
    }

    @Bean
    public QueryParser queryParser(@Autowired SqlQueryServiceProperties sqlQueryServiceProperties) {
        return new MyBatisQueryParser(sqlQueryServiceProperties);
    }
}
