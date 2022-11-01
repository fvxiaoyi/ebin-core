package core.framework.jpa.mongodb;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.custom.CustomLoader;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.loader.custom.Return;
import org.hibernate.loader.custom.RootReturn;
import org.hibernate.loader.custom.ScalarReturn;
import org.hibernate.ogm.dialect.query.spi.BackendQuery;
import org.hibernate.ogm.dialect.query.spi.ClosableIterator;
import org.hibernate.ogm.dialect.query.spi.QueryableGridDialect;
import org.hibernate.ogm.dialect.spi.TupleContext;
import org.hibernate.ogm.loader.nativeloader.impl.BackendCustomQuery;
import org.hibernate.ogm.model.spi.Tuple;
import org.hibernate.ogm.type.spi.GridType;
import org.hibernate.ogm.type.spi.TypeTranslator;
import org.hibernate.ogm.util.impl.CustomLoaderHelper;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hibernate.ogm.util.impl.TupleContextHelper.tupleContext;

/**
 * @author ebin
 */
public class ExtendBackendCustomLoader extends CustomLoader {
    private final CustomQuery customQuery;
    private final TypeTranslator typeTranslator;
    private final BackendCustomLoaderContext<?> loaderContext;

    public ExtendBackendCustomLoader(BackendCustomQuery<?> customQuery, SessionFactoryImplementor factory) {
        super(customQuery, factory);

        this.customQuery = customQuery;
        this.typeTranslator = factory.getServiceRegistry().getService(TypeTranslator.class);
        this.loaderContext = getLoaderContext(customQuery, factory);
    }

    @Override
    public Set<String> getQuerySpaces() {
        @SuppressWarnings("unchecked")
        Set<String> querySpaces = super.getQuerySpaces();
        return querySpaces;
    }

    private static <T extends Serializable> BackendCustomLoaderContext<T> getLoaderContext(BackendCustomQuery<T> customQuery, SessionFactoryImplementor factory) {
        @SuppressWarnings("unchecked")
        QueryableGridDialect<T> gridDialect = factory.getServiceRegistry().getService(QueryableGridDialect.class);
        return new BackendCustomLoaderContext<T>(gridDialect, customQuery);
    }

    /**
     * Whether this query is a selection of a complete entity or not. Queries mixing scalar values and entire entities
     * in one result are not supported atm.
     */
    private boolean isEntityQuery() {
        for (Return queryReturn : customQuery.getCustomQueryReturns()) {
            if (queryReturn instanceof RootReturn) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<?> list(SharedSessionContractImplementor session, org.hibernate.engine.spi.QueryParameters queryParameters, Set<Serializable> querySpaces,
                           Type[] resultTypes) throws HibernateException {
        ClosableIterator<Tuple> tuples = loaderContext.executeQuery(session,
                org.hibernate.ogm.dialect.query.spi.QueryParameters.fromOrmQueryParameters(queryParameters, typeTranslator, session.getFactory()));
        try {
            if (isEntityQuery()) {
                return CustomLoaderHelper.listOfEntities(session, resultTypes, tuples);
            } else {
                return listOfArrays(session, tuples);
            }
        } finally {
            tuples.close();
        }
    }

    private List<Object> listOfArrays(SharedSessionContractImplementor session, Iterator<Tuple> tuples) {
        List<Object> results = new ArrayList<Object>();
        while (tuples.hasNext()) {
            Tuple tuple = tuples.next();
            Map<String, Object> entry;
            if (!customQuery.getCustomQueryReturns().isEmpty()) {
                entry = new HashMap<>(customQuery.getCustomQueryReturns().size());
                for (Return queryReturn : customQuery.getCustomQueryReturns()) {
                    ScalarReturn scalarReturn = (ScalarReturn) queryReturn;
                    Type type = scalarReturn.getType();

                    if (type != null) {
                        GridType gridType = typeTranslator.getType(type);
                        entry.put(scalarReturn.getColumnAlias(), gridType.nullSafeGet(tuple, scalarReturn.getColumnAlias(), session, null));
                    } else {
                        entry.put(scalarReturn.getColumnAlias(), tuple.get(scalarReturn.getColumnAlias()));
                    }
                }
            } else {
                // TODO OGM-564 As a temporary work-around, retrieving the names from the actual result in case there
                // are no query returns defined (no result mapping has been given for a native query). Actually we
                // should drive this based on the selected columns as otherwise the order might not be correct and/or
                // null values will not show up
                entry = new HashMap(tuple.getColumnNames().size());
                for (String column : tuple.getColumnNames()) {
                    entry.put(column, tuple.get(column));
                }
            }
            results.add(entry);
        }

        return results;
    }

    /**
     * Extracted as separate class for the sole purpose of capturing the type parameter {@code T} without exposing it to
     * the callers which don't actually need it.
     *
     * @author Gunnar Morling
     */
    private static class BackendCustomLoaderContext<T extends Serializable> {

        private final QueryableGridDialect<T> gridDialect;
        private final BackendQuery<T> query;

        BackendCustomLoaderContext(QueryableGridDialect<T> gridDialect, BackendCustomQuery<T> customQuery) {
            this.gridDialect = gridDialect;
            this.query = new BackendQuery<T>(
                    customQuery.getQueryObject(),
                    customQuery.getSingleEntityMetadataInformationOrNull());
        }

        public ClosableIterator<Tuple> executeQuery(SharedSessionContractImplementor session, org.hibernate.ogm.dialect.query.spi.QueryParameters queryParameters) {
            TupleContext tupleContext = tupleContext(session, query.getSingleEntityMetadataInformationOrNull());
            return gridDialect.executeBackendQuery(query, queryParameters, tupleContext);
        }
    }
}
