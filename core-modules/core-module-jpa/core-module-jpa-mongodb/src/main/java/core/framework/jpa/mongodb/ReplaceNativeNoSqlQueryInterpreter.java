package core.framework.jpa.mongodb;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.custom.CustomLoader;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.ogm.dialect.query.spi.QueryableGridDialect;
import org.hibernate.ogm.loader.nativeloader.impl.BackendCustomQuery;
import org.hibernate.ogm.query.impl.NativeNoSqlQueryInterpreter;

/**
 * @author ebin
 */
public class ReplaceNativeNoSqlQueryInterpreter extends NativeNoSqlQueryInterpreter {
    public ReplaceNativeNoSqlQueryInterpreter(QueryableGridDialect<?> gridDialect) {
        super(gridDialect);
    }

    @Override
    public CustomLoader createCustomLoader(CustomQuery customQuery, SessionFactoryImplementor sessionFactory) {
        return new ExtendBackendCustomLoader((BackendCustomQuery<?>) customQuery, sessionFactory);
    }
}
