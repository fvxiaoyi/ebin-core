package core.framework.validate.validation.string;

/**
 * @author ebin
 */
public final class NotBlankValidation {
    private static final String TEMPLATE = """
                    if (bean.%1$s.isBlank()) {
                        return java.util.Optional.of("%2$s");
                    }
            """;

    private NotBlankValidation() {
    }

    public static String getValidation(String beanFieldName, String message) {
        return String.format(TEMPLATE, beanFieldName, message);
    }
}
