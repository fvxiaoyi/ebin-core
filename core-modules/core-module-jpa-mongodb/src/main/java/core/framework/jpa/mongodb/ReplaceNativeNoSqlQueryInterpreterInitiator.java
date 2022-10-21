package core.framework.jpa.mongodb;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.query.spi.NativeQueryInterpreter;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.ogm.dialect.query.spi.QueryableGridDialect;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;

/**
 * @author ebin
 */
public class ReplaceNativeNoSqlQueryInterpreterInitiator implements SessionFactoryServiceInitiator<NativeQueryInterpreter> {

    public static ReplaceNativeNoSqlQueryInterpreterInitiator INSTANCE = new ReplaceNativeNoSqlQueryInterpreterInitiator();

    private ReplaceNativeNoSqlQueryInterpreterInitiator() {
    }

    @Override
    public NativeQueryInterpreter initiateService(SessionFactoryImplementor sessionFactory, SessionFactoryOptions sessionFactoryOptions, ServiceRegistryImplementor registry) {
        return getParameterMetadataRecognizer(registry);
    }

    @Override
    public Class<NativeQueryInterpreter> getServiceInitiated() {
        return NativeQueryInterpreter.class;
    }

    private NativeQueryInterpreter getParameterMetadataRecognizer(ServiceRegistryImplementor registry) {
        QueryableGridDialect<?> queryableGridDialect = registry.getService(QueryableGridDialect.class);

        if (queryableGridDialect != null) {
            return new ReplaceNativeNoSqlQueryInterpreter(queryableGridDialect);
        } else {
            return null;
        }
    }
}
