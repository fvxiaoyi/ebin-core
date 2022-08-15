package core.framework.validate.validation.collection;

/**
 * @author ebin
 */
public final class CollectionValueEachValidation {
    private static final String TEMPLATE = """
                    for (java.util.Iterator iterator = bean.%1$s.iterator(); iterator.hasNext();) {
                        %2$s value = (%2$s) iterator.next();
                        if (value != null) {
                            java.util.Optional result = %3$s(value);
                            if (result.isPresent()) {
                                return result;
                            }
                        }
                    }
            """;

    private CollectionValueEachValidation() {
    }

    public static String getValidation(String beanFieldName, Class<?> valueType, String validationMethodName) {
        return String.format(TEMPLATE, beanFieldName, valueType.getName(), validationMethodName);
    }
}
