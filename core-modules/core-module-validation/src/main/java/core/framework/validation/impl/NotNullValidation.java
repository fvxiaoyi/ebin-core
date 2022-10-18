package core.framework.validation.impl;

/**
 * @author ebin
 */
public final class NotNullValidation {
    private static final String TEMPLATE = """
                if (bean.%1$s != null) {
            %3$s
                } else {
                    return java.util.Optional.of("%2$s");
                }
            """;

    private NotNullValidation() {
    }

    public static String getValidation(String beanFieldName, String message, String otherValidation) {
        return String.format(TEMPLATE, beanFieldName, message, otherValidation);
    }
}
