package core.framework.query.impl.nosql;

import core.framework.json.JSON;
import core.framework.query.QueryParser;
import core.framework.query.impl.AbstractQueryService;
import org.hibernate.jpa.QueryHints;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class JPANoSqlQueryService extends AbstractQueryService {
    private final String TOTAL_QUERY_NAME_SUFFIX = ".total";
    private final EntityManager entityManager;

    public JPANoSqlQueryService(EntityManager entityManager, QueryParser queryParser) {
        super(queryParser);
        this.entityManager = entityManager;
    }

    @Override
    protected String getQueryStatement(String queryName, Map<String, Object> param) {
        return this.queryParser.getQueryString(queryName, param);
    }

    @Override
    protected String getTotalQueryStatement(String queryName, Map<String, Object> param) {
        return this.queryParser.getQueryString(queryName + TOTAL_QUERY_NAME_SUFFIX, param);
    }

    @Override
    protected <T> List<T> executeSelectQuery(String sql, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(sql, param);
        List<Map<String, Object>> resultList = query.getResultList();
        return resultList.stream().map(result -> transformResult(result, beanClass)).collect(Collectors.toList());
    }

    @Override
    protected <T> List<T> executePagingQuery(String sql, Class<T> beanClass, Map<String, Object> param, Integer start, Integer limit) {
        return null;
    }

    @Override
    protected <T> Optional<T> executeGetQuery(String sql, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(sql, param);
        query.setHint(QueryHints.HINT_FETCH_SIZE, 1);
        return query.getResultList().stream().findFirst().map(result -> transformResult((Map<String, Object>) result, beanClass));
    }

    @Override
    protected Long executeTotalQuery(String sql, Map<String, Object> param) {
        return null;
    }

    private <T> Query createQuery(String sql, Map<String, Object> param) {
        Query query = entityManager.createNativeQuery(sql).setHint(QueryHints.HINT_READONLY, true);
        param.forEach(query::setParameter);
        return query;
    }

    private <T> T transformResult(Map<String, Object> result, Class<T> beanClass) {
        Object id = result.remove("_id");
        if (id != null) {
            result.put("id", id);
        }
        return JSON.fromJSON(beanClass, JSON.toJSON(result));
    }
}
