package core.framework.validation.impl.number;

/**
 * @author ebin
 */
public final class DecimalMinValidation {
    private static final String TEMPLATE = """
                   if (new java.math.BigDecimal(String.valueOf(bean.%1$s)).compareTo(new java.math.BigDecimal("%3$s")) < 0) {
                        return java.util.Optional.of("%2$s");
                   }
            """;

    private DecimalMinValidation() {
    }

    public static String getValidation(String beanFieldName, String message, String min) {
        return String.format(TEMPLATE, beanFieldName, message, min);
    }
}
