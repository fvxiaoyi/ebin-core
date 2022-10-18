package core.framework.validation.impl.collection;

/**
 * @author ebin
 */
public final class MapValueEachValidation {
    private static final String TEMPLATE = """
                    for (java.util.Iterator iterator = bean.%1$s.entrySet().iterator(); iterator.hasNext(); ) {
                        java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
                        %2$s value = (%2$s) entry.getValue();
                        if (value != null) {
                            java.util.Optional result = %3$s(value);
                            if (result.isPresent()) {
                                return result;
                            }
                        }
                    }
            """;

    private MapValueEachValidation() {
    }

    public static String getValidation(String beanFieldName, Class<?> valueType, String validationMethodName) {
        return String.format(TEMPLATE, beanFieldName, valueType.getName(), validationMethodName);
    }
}
