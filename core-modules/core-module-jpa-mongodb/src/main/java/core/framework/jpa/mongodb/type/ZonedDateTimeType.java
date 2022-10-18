package core.framework.jpa.mongodb.type;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.ogm.type.descriptor.impl.PassThroughGridTypeDescriptor;
import org.hibernate.ogm.type.impl.AbstractGenericBasicType;
import org.hibernate.type.descriptor.java.ZonedDateTimeJavaDescriptor;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public class ZonedDateTimeType extends AbstractGenericBasicType<ZonedDateTime> {
    public static final ZonedDateTimeType INSTANCE = new ZonedDateTimeType();

    public ZonedDateTimeType() {
        super(PassThroughGridTypeDescriptor.INSTANCE, ZonedDateTimeJavaDescriptor.INSTANCE);
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        return 1;
    }

    @Override
    public String getName() {
        return null;
    }

}
