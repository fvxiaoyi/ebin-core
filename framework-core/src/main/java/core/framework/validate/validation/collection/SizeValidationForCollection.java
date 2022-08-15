package core.framework.validate.validation.collection;

/**
 * @author ebin
 */
public final class SizeValidationForCollection {
    private static final String TEMPLATE = """
                    if (bean.%1$s.size() < %3$s || bean.%1$s.size() > %4$s) {
                        return java.util.Optional.of("%2$s");
                    }
            """;

    private SizeValidationForCollection() {
    }

    public static String getValidation(String beanFieldName, String message, int min, int max) {
        return String.format(TEMPLATE, beanFieldName, message, min, max);
    }
}
