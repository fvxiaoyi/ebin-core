package core.framework.validation.impl.string;

/**
 * @author ebin
 */
public final class NotEmptyValidationForString {
    private static final String TEMPLATE = """
                    if (bean.%1$s.isEmpty()) {
                        return java.util.Optional.of("%2$s");
                    }
            """;

    private NotEmptyValidationForString() {
    }

    public static String getValidation(String beanFieldName, String message) {
        return String.format(TEMPLATE, beanFieldName, message);
    }
}
