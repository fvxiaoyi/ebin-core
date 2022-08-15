package core.framework.validate.validation.string;

/**
 * @author ebin
 */
public final class SizeValidationForString {
    private static final String TEMPLATE = """
                    if (bean.%1$s.length() < %3$s || bean.%1$s.length() > %4$s) {
                        return java.util.Optional.of("%2$s");
                    }
            """;

    private SizeValidationForString() {
    }

    public static String getValidation(String beanFieldName, String message, int min, int max) {
        return String.format(TEMPLATE, beanFieldName, message, min, max);
    }
}
