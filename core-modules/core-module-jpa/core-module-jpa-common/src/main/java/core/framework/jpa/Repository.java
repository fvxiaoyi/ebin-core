package core.framework.jpa;

import core.framework.jpa.impl.AbstractAggregateRoot;
import org.hibernate.jpa.QueryHints;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public interface Repository<T extends AbstractAggregateRoot<T>> extends SpringJpaRepositorySupport<T> {
    int START_INDEX = 0;
    int HINT_FETCH_SIZE = 1;

    default List<T> selectByQueryString(String queryString, Object... params) {
        TypedQuery<T> query = getEntityManager().createQuery(queryString, getEntityClass());
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> query.setParameter(index, params[index]));
        }
        List<T> resultList = query.getResultList();
        if (resultList == null)
            return List.of();
        return resultList;
    }

    default List<T> selectByNamedQuery(String queryName, Object... params) {
        TypedQuery<T> namedQuery = getEntityManager().createNamedQuery(queryName, getEntityClass());
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> namedQuery.setParameter(index, params[index]));
        }
        List<T> resultList = namedQuery.getResultList();
        if (resultList == null)
            return List.of();
        return resultList;
    }

    default T find(Object id) {
        return (id != null) ? getEntityManager().find(getEntityClass(), id) : null;
    }

    default T findByNamedQuery(String queryName, Object... params) {
        TypedQuery<T> namedQuery = getEntityManager().createNamedQuery(queryName, getEntityClass());
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> namedQuery.setParameter(index, params[index]));
        }
        namedQuery.setHint(QueryHints.HINT_FETCH_SIZE, HINT_FETCH_SIZE);
        List<T> resultList = namedQuery.getResultList();
        return resultList.stream().findFirst().orElse(null);
    }

    default T findByQueryString(String queryString, Object... params) {
        TypedQuery<T> query = getEntityManager().createQuery(queryString, getEntityClass());
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> query.setParameter(index, params[index]));
        }
        query.setHint(QueryHints.HINT_FETCH_SIZE, HINT_FETCH_SIZE);
        List<T> resultList = query.getResultList();
        return resultList.stream().findFirst().orElse(null);
    }

    default void persist(T entity) {
        if (entity != null) {
            getEntityManager().persist(entity);
        }
    }

    default T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    default void remove(T entity) {
        if (entity != null) {
            getEntityManager().remove(entity);
        }
    }
}
