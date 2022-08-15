package core.framework.db.query;

import org.hibernate.jpa.QueryHints;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class JPAQueryService extends AbstractSqlQueryService {
    private final EntityManager entityManager;
    private final Map<Class<?>, ResultTransformer> resultBeanTransformers = new ConcurrentHashMap<>();

    public JPAQueryService(EntityManager entityManager, QueryParser queryParser) {
        super(queryParser);
        this.entityManager = entityManager;
    }

    protected <T> List<T> executeSelectQuery(String sql, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(sql, beanClass, param);
        return query.getResultList();
    }

    protected <T> List<T> executePagingQuery(String sql, Class<T> beanClass, Map<String, Object> param, Integer start, Integer limit) {
        Query query = this.createQuery(sql, beanClass, param);
        query.setFirstResult(start).setMaxResults(limit).setHint(QueryHints.HINT_FETCH_SIZE, limit);
        return query.getResultList();
    }

    protected <T> Optional<T> executeGetQuery(String sql, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(sql, beanClass, param);
        query.setHint(QueryHints.HINT_FETCH_SIZE, 1);
        return query.getResultList().stream().findFirst();
    }

    private <T> Query createQuery(String sql, Class<T> beanType, Map<String, Object> param) {
        Query query = entityManager.createNativeQuery(sql).setHint(QueryHints.HINT_READONLY, true);
        param.forEach(query::setParameter);
        org.hibernate.query.Query<?> unwrapQuery = query.unwrap(org.hibernate.query.Query.class);
        unwrapQuery.setResultTransformer(getTransformer(beanType));
        return query;
    }

    private <T> ResultTransformer getTransformer(Class<T> beanType) {
        return resultBeanTransformers.computeIfAbsent(beanType, k -> new AliasToJSONBeanTransformer(beanType));
    }
}
