package core.framework.validation.impl.number;

/**
 * @author ebin
 */
public final class MinValidation {
    private static final String TEMPLATE = """
                    if (bean.%1$s.longValue() < %3$s) {
                        return java.util.Optional.of("%2$s");
                    }
            """;

    private MinValidation() {
    }

    public static String getValidation(String beanFieldName, String message, long min) {
        return String.format(TEMPLATE, beanFieldName, message, min);
    }
}
