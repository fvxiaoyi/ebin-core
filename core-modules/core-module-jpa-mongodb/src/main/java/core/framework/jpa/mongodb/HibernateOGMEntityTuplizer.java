package core.framework.jpa.mongodb;

import core.framework.jpa.mongodb.convert.Converter;
import core.framework.jpa.mongodb.convert.ExtendConverter;
import org.hibernate.HibernateException;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.PojoEntityTuplizer;

/**
 * @author ebin
 */
public class HibernateOGMEntityTuplizer extends PojoEntityTuplizer {

    public HibernateOGMEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
        super(entityMetamodel, mappedEntity);
    }

    @Override
    public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
        Object[] convertedValues = new Object[values.length];
        int propertySpan = getEntityMetamodel().getPropertySpan();
        NonIdentifierAttribute[] properties = getEntityMetamodel().getProperties();
        for (int j = 0; j < propertySpan; j++) {
            convertedValues[j] = convert(properties[j], values[j]);
        }
        super.setPropertyValues(entity, convertedValues);
    }

    private Object convert(NonIdentifierAttribute attribute, Object value) {
        Class<?> returnedClass = attribute.getType().getReturnedClass();
        Class<?> valueClass = value.getClass();
        Converter<?> converter = ExtendConverter.get(valueClass, returnedClass);
        if (converter != null) {
            return converter.convert(value);
        }
        return value;
    }
}
