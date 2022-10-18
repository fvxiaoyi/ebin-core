package core.framework.validation.impl.number;

/**
 * @author ebin
 */
public final class MaxValidation {
    private static final String TEMPLATE = """
                    if (bean.%1$s.longValue() > %3$s) {
                        return java.util.Optional.of("%2$s");
                    }
            """;

    private MaxValidation() {
    }

    public static String getValidation(String beanFieldName, String message, long max) {
        return String.format(TEMPLATE, beanFieldName, message, max);
    }
}
