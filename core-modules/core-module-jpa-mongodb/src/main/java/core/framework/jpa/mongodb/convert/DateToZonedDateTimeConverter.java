package core.framework.jpa.mongodb.convert;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author ebin
 */
public class DateToZonedDateTimeConverter implements Converter<ZonedDateTime> {

    @Override
    public ZonedDateTime convert(Object date) {
        if (date instanceof Date) {
            return ZonedDateTime.ofInstant(((Date) date).toInstant(), ZoneId.systemDefault());
        } else {
            return null;
        }
    }

    @Override
    public boolean support(Class<?> a, Class<?> b) {
        return Date.class == a && ZonedDateTime.class == b;
    }
}
