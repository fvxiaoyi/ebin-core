package core.framework.validation.impl.number;

/**
 * @author ebin
 */
public final class DecimalMaxValidationForBigDecimal {
    private static final String TEMPLATE = """
                   if (bean.%1$s.compareTo(new java.math.BigDecimal("%3$s")) > 0) {
                        return java.util.Optional.of("%2$s");
                   }
            """;

    private DecimalMaxValidationForBigDecimal() {
    }

    public static String getValidation(String beanFieldName, String message, String max) {
        return String.format(TEMPLATE, beanFieldName, message, max);
    }
}
