package core.framework.query.configuration;

import core.framework.query.QueryParser;
import core.framework.query.QueryService;
import core.framework.query.impl.nosql.JPANoSqlQueryService;
import core.framework.query.impl.parser.MyBatisQueryParser;
import core.framework.query.impl.sql.JPASqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.framework.jpa.mysql.configuration.HibernateMysqlConfiguration.MYSQL_PERSISTENCE_UNIT_INFO_NAME;
import static core.framework.jpa.mongodb.configuration.HibernateMongoDBConfiguration.MONGODB_PERSISTENCE_UNIT_INFO_NAME;

/**
 * @author ebin
 */
@Configuration
public class QueryServiceConfiguration {
    @PersistenceContext(unitName = MONGODB_PERSISTENCE_UNIT_INFO_NAME)
    private EntityManager entityManager;

    @PersistenceContext(unitName = MYSQL_PERSISTENCE_UNIT_INFO_NAME)
    private EntityManager mysqlEntityManager;

    @Bean(name = "jpaNoSqlQueryService")
    public QueryService jpaNoSqlQueryService(@Autowired QueryParser queryParser) {
        return new JPANoSqlQueryService(entityManager, queryParser);
    }

    @Bean(name = "jpaSqlQueryService")
    public QueryService jpaSqlQueryService(@Autowired QueryParser queryParser) {
        return new JPASqlQueryService(mysqlEntityManager, queryParser);
    }


    @Bean
    public QueryParser queryParser() {
        return new MyBatisQueryParser();
    }
}
