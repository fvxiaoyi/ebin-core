package core.framework.validation.impl.collection;

/**
 * @author ebin
 */
public final class NotEmptyValidationForMap {
    private static final String TEMPLATE = """
                    if (bean.%1$s.isEmpty()) {
                        return java.util.Optional.of("%2$s");
                    }
            """;

    private NotEmptyValidationForMap() {
    }

    public static String getValidation(String beanFieldName, String message) {
        return String.format(TEMPLATE, beanFieldName, message);
    }
}
