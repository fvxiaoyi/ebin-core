package core.framework.validate.validation.number;

/**
 * @author ebin
 */
public final class DecimalMaxValidation {
    private static final String TEMPLATE = """
                   if (new java.math.BigDecimal(String.valueOf(bean.%1$s)).compareTo(new java.math.BigDecimal("%3$s")) > 0) {
                        return java.util.Optional.of("%2$s");
                   }
            """;

    private DecimalMaxValidation() {
    }

    public static String getValidation(String beanFieldName, String message, String max) {
        return String.format(TEMPLATE, beanFieldName, message, max);
    }
}
