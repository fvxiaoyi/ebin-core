package core.framework.validation.impl.string;

/**
 * @author ebin
 */
public final class PatternValidation {
    private static final String FIELD_TEMPLATE = "private final java.util.regex.Pattern %1$s = java.util.regex.Pattern.compile(%2$s);";
    private static final String TEMPLATE = """
                    if (!this.%3$s.matcher(bean.%1$s).matches()) {
                        return java.util.Optional.of("%2$s");
                    }
            """;

    private PatternValidation() {
    }

    public static String patternField(String patternFieldName, String patternVariable) {
        return String.format(FIELD_TEMPLATE, patternFieldName, patternVariable);
    }

    public static String getValidation(String beanFieldName, String message, String patternFieldName) {
        return String.format(TEMPLATE, beanFieldName, message, patternFieldName);
    }
}
