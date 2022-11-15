package core.framework.jpa.mongodb.convert;

import java.util.List;

/**
 * @author ebin
 */
public final class ExtendConverter {
    private static List<Converter<?>> converters = List.of(
            new DateToZonedDateTimeConverter(),
            new DateToLocalDateTimeConverter(),
            new DoubleToBigDecimalConverter());

    private ExtendConverter() {
    }
    public static Converter<?> get(Class<?> a, Class<?> b) {
        return converters.stream().filter(converter -> converter.support(a, b)).findFirst().orElse(null);
    }
}
