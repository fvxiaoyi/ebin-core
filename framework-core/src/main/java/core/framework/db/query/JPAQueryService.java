package core.framework.db.query;

import org.hibernate.jpa.QueryHints;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class JPAQueryService extends AbstractSqlQueryService {
    private final EntityManager entityManager;
    private final Validator validator;
    private final Map<Class<?>, ResultTransformer> resultBeanTransformers = new ConcurrentHashMap<>();

    public JPAQueryService(EntityManager entityManager, QueryParser queryParser, Validator validator) {
        super(queryParser);
        this.entityManager = entityManager;
        this.validator = validator;
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

    protected <T> T executeGetQuery(String sql, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(sql, beanClass, param);
        query.setHint(QueryHints.HINT_FETCH_SIZE, 1);
        return (T) query.getResultList().stream().findFirst().orElse(null);
    }

    private <T> Query createQuery(String sql, Class<T> beanType, Map<String, Object> param) {
        Query query = entityManager.createNativeQuery(sql).setHint(QueryHints.HINT_READONLY, true);
        param.forEach(query::setParameter);
        org.hibernate.query.Query<?> unwrapQuery = query.unwrap(org.hibernate.query.Query.class);
        unwrapQuery.setResultTransformer(getTransformer(beanType));
        return query;
    }

    private <T> ResultTransformer getTransformer(Class<T> beanType) {
        return resultBeanTransformers.computeIfAbsent(beanType, k -> {
            boolean valid = false;
            Field[] declaredFields = beanType.getDeclaredFields();
            for (Field field : declaredFields) {
                Annotation[] annotations = field.getAnnotations();
                for (Annotation ann : annotations) {
                    if (ann.annotationType().getName().startsWith("javax.validation")) {
                        valid = true;
                        break;
                    }
                }
            }
            return new AliasToJSONBeanTransformer(beanType, valid ? validator : null);
        });
    }
}
