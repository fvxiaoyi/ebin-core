package core.framework.validate.validation;

/**
 * @author ebin
 */
public final class ValidationMethod {
    private static final String START = """
            public java.util.Optional %1$s(Object instance) {
                %2$s bean = (%2$s)instance;
            """;

    private static final String END = """
                return java.util.Optional.empty();
            }
            """;

    private ValidationMethod() {
    }

    public static ValidationMethodBuilder builder(String methodName, Class<?> beanClass) {
        ValidationMethodBuilder builder = new ValidationMethodBuilder();
        builder.methodBody.append(String.format(START, methodName, beanClass.getName()));
        return builder;
    }

    public static class ValidationMethodBuilder {
        private StringBuilder methodBody = new StringBuilder();

        public ValidationMethodBuilder addValidationBody(String validationBody) {
            methodBody.append(validationBody);
            return this;
        }

        public String build() {
            methodBody.append(END);
            return methodBody.toString();
        }
    }
}
