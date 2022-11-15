package core.framework.jpa.mongodb;

import core.framework.jpa.mongodb.type.ZonedDateTimeType;
import org.hibernate.ogm.datastore.mongodb.MongoDBDialect;
import org.hibernate.ogm.datastore.mongodb.impl.MongoDBDatastoreProvider;
import org.hibernate.ogm.type.spi.GridType;
import org.hibernate.type.Type;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public class HibernateMongoDBDialect extends MongoDBDialect {
    public HibernateMongoDBDialect(MongoDBDatastoreProvider provider) {
        super(provider);
    }

    @Override
    public GridType overrideType(Type type) {
        GridType gridType = super.overrideType(type);
        if (gridType == null) {
            if (type.getReturnedClass() == ZonedDateTime.class) {
                return ZonedDateTimeType.INSTANCE;
            }
            return null;
        } else {
            return gridType;
        }
    }
}
