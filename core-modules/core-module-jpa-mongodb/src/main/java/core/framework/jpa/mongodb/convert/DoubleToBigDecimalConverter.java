package core.framework.jpa.mongodb.convert;

import java.math.BigDecimal;

/**
 * @author ebin
 */
public class DoubleToBigDecimalConverter implements Converter<BigDecimal> {

    @Override
    public BigDecimal convert(Object value) {
        if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value);
        } else {
            return null;
        }
    }

    @Override
    public boolean support(Class<?> a, Class<?> b) {
        return Double.class == a && BigDecimal.class == b;
    }
}
