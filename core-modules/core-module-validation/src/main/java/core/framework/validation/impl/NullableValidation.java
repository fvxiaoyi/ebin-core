package core.framework.validation.impl;

/**
 * @author ebin
 */
public final class NullableValidation {
    private static final String TEMPLATE = """
                if (bean.%1$s != null) {
            %2$s
                }
            """;

    private NullableValidation() {
    }

    public static String getValidation(String beanFieldName, String otherValidation) {
        return String.format(TEMPLATE, beanFieldName, otherValidation);
    }
}
