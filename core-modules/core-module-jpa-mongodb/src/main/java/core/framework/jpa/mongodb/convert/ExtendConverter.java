package core.framework.jpa.mongodb.convert;

import java.util.List;

/**
 * @author ebin
 */
public class ExtendConverter {

    private static List<Converter<?>> converters = List.of(
            new DateToZonedDateTimeConverter(),
            new DateToLocalDateTimeConverter(),
            new DoubleToBigDecimalConverter());

    public static Converter<?> get(Class<?> a, Class<?> b) {
        return converters.stream().filter(converter -> converter.support(a, b)).findFirst().orElse(null);
    }
}
