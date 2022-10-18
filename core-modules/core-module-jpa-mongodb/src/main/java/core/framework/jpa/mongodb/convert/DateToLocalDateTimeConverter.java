package core.framework.jpa.mongodb.convert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author ebin
 */
public class DateToLocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public LocalDateTime convert(Object date) {
        if (date instanceof Date) {
            return LocalDateTime.ofInstant(((Date) date).toInstant(), ZoneId.systemDefault());
        } else {
            return null;
        }
    }

    @Override
    public boolean support(Class<?> a, Class<?> b) {
        return Date.class == a && LocalDateTime.class == b;
    }
}
