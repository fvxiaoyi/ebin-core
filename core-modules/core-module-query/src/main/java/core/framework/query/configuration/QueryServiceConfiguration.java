package core.framework.query.configuration;

import core.framework.query.JPAQueryService;
import core.framework.query.MyBatisQueryParser;
import core.framework.query.QueryParser;
import core.framework.query.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author ebin
 */
@Configuration
public class QueryServiceConfiguration {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public QueryService jpaSqlQueryService(@Autowired QueryParser queryParser) {
        return new JPAQueryService(entityManager, queryParser);
    }

    @Bean
    public QueryParser queryParser() {
        return new MyBatisQueryParser();
    }
}
