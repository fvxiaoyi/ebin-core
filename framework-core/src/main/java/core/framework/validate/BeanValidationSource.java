package core.framework.validate;

import core.framework.validate.validation.NotNullValidation;
import core.framework.validate.validation.NullableValidation;
import core.framework.validate.validation.ValidationMethod;
import core.framework.validate.validation.collection.CollectionValueEachValidation;
import core.framework.validate.validation.collection.MapValueEachValidation;
import core.framework.validate.validation.collection.NotEmptyValidationForCollection;
import core.framework.validate.validation.collection.NotEmptyValidationForMap;
import core.framework.validate.validation.collection.SizeValidationForCollection;
import core.framework.validate.validation.collection.SizeValidationForMap;
import core.framework.validate.validation.number.DecimalMaxValidation;
import core.framework.validate.validation.number.DecimalMaxValidationForBigDecimal;
import core.framework.validate.validation.number.DecimalMinValidation;
import core.framework.validate.validation.number.DecimalMinValidationForBigDecimal;
import core.framework.validate.validation.number.MaxValidation;
import core.framework.validate.validation.number.MinValidation;
import core.framework.validate.validation.string.NotBlankValidation;
import core.framework.validate.validation.string.NotEmptyValidationForString;
import core.framework.validate.validation.string.PatternValidation;
import core.framework.validate.validation.string.SizeValidationForString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.MessageInterpolator;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public class BeanValidationSource {
    private static final String BEAN_GET_METHOD = "get%s";
    private static final String VALIDATE_METHOD_NAME = "validate";
    private static final String MESSAGE_TEMPLATE = "%1$s %2$s";
    private static final MessageInterpolator MESSAGE_INTERPOLATOR = new MessageInterpolatorImpl();

    private final List<String> fields = new ArrayList<>();
    private final List<String> methods = new ArrayList<>();
    private int index;

    public BeanValidationSource(Class<?> beanClass) {
        this.validateMethod(beanClass, null);
    }

    public List<String> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public List<String> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    private String validateMethod(Class<?> beanClass, String parent) {
        String methodName;
        if (parent == null) {
            methodName = VALIDATE_METHOD_NAME;
        } else {
            methodName = VALIDATE_METHOD_NAME + beanClass.getSimpleName() + (index++);
        }

        ValidationMethod.ValidationMethodBuilder builder = ValidationMethod.builder(methodName, beanClass);

        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String accessName;
            if (Modifier.PUBLIC == field.getModifiers()) {
                accessName = field.getName();
            } else {
                try {
                    Method getMethod = beanClass.getMethod(String.format(BEAN_GET_METHOD, StringUtils.capitalize(field.getName())));
                    accessName = getMethod.getName() + "()";
                } catch (NoSuchMethodException e) {
                    throw new Error(e);
                }
            }
            builder.addValidationBody(buildValidation(field, accessName, parent));
        }
        methods.add(builder.build());
        return methodName;
    }

    private String buildValidation(Field field, String accessName, String parent) {
        Type fieldType = field.getGenericType();
        Class<?> fieldClass = rawClass(fieldType);
        StringBuilder propertiesValidationBody = new StringBuilder();
        if (String.class.equals(fieldClass)) {
            buildStringPropertyValidation(field, accessName, propertiesValidationBody, parent);
        } else if (Number.class.isAssignableFrom(fieldClass)) {
            buildNumberValidation(field, fieldClass, accessName, propertiesValidationBody, parent);
        } else if (Collection.class.isAssignableFrom(fieldClass)) {
            buildCollectionPropertyValidation(field, accessName, propertiesValidationBody, parent);
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            buildMapPropertyValidation(field, accessName, propertiesValidationBody, parent);
        }

        StringBuilder validationBody = new StringBuilder();
        NotNull notNull = field.getDeclaredAnnotation(NotNull.class);
        if (notNull != null) {
            String name = messagePropertiesName(field, parent);
            String message = String.format(MESSAGE_TEMPLATE, name, MESSAGE_INTERPOLATOR.interpolate(notNull.message(), null));
            validationBody.append(NotNullValidation.getValidation(accessName, message, propertiesValidationBody.toString()));
        } else {
            validationBody.append(NullableValidation.getValidation(accessName, propertiesValidationBody.toString()));
        }
        return validationBody.toString();
    }

    public String messagePropertiesName(Field field, String parent) {
        return Optional.ofNullable(parent).map(m -> m + "." + field.getName()).orElse(field.getName());
    }

    private void buildCollectionPropertyValidation(Field field, String accessName, StringBuilder validationBody, String parent) {
        String name = messagePropertiesName(field, parent);
        Arrays.stream(field.getAnnotations()).forEach(annotation -> {
            if (annotation instanceof Size ann) {
                String message = String.format(MESSAGE_TEMPLATE, name,
                        MessageFormat.format(MESSAGE_INTERPOLATOR.interpolate(ann.message(), null), ann.min(), ann.max()));
                validationBody.append(SizeValidationForCollection.getValidation(accessName, message, ann.min(), ann.max()));
            } else if (annotation instanceof NotEmpty ann) {
                String message = String.format(MESSAGE_TEMPLATE, name, MESSAGE_INTERPOLATOR.interpolate(ann.message(), null));
                validationBody.append(NotEmptyValidationForCollection.getValidation(accessName, message));
            }
        });

        Class<?> valueClass = listValueClass(field.getGenericType());
        if (!isValueClass(valueClass)) {
            String methodName = validateMethod(valueClass, valueClass.getSimpleName());
            validationBody.append(CollectionValueEachValidation.getValidation(accessName, valueClass, methodName));
        }
    }

    private void buildMapPropertyValidation(Field field, String accessName, StringBuilder validationBody, String parent) {
        String name = messagePropertiesName(field, parent);
        Arrays.stream(field.getAnnotations()).forEach(annotation -> {
            if (annotation instanceof Size ann) {
                String message = String.format(MESSAGE_TEMPLATE, name,
                        MessageFormat.format(MESSAGE_INTERPOLATOR.interpolate(ann.message(), null), ann.min(), ann.max()));
                validationBody.append(SizeValidationForMap.getValidation(accessName, message, ann.min(), ann.max()));
            } else if (annotation instanceof NotEmpty ann) {
                String message = String.format(MESSAGE_TEMPLATE, name, MESSAGE_INTERPOLATOR.interpolate(ann.message(), null));
                validationBody.append(NotEmptyValidationForMap.getValidation(accessName, message));
            }
        });

        Type valueType = mapValueType(field.getGenericType());
        if (isList(valueType)) return; // ensured by class validator, if it's list it must be List<Value>

        Class<?> valueClass = rawClass(valueType);
        if (!isValueClass(valueClass)) {
            String methodName = validateMethod(valueClass, valueClass.getSimpleName());
            validationBody.append(MapValueEachValidation.getValidation(accessName, valueClass, methodName));
        }
    }

    private void buildNumberValidation(Field field, Class<?> fieldClass, String accessName, StringBuilder validationBody, String parent) {
        String name = messagePropertiesName(field, parent);
        Arrays.stream(field.getAnnotations()).forEach(annotation -> {
            if (annotation instanceof Min ann) {
                String message = String.format(MESSAGE_TEMPLATE, name,
                        MessageFormat.format(MESSAGE_INTERPOLATOR.interpolate(ann.message(), null), ann.value()));
                validationBody.append(MinValidation.getValidation(accessName, message, ann.value()));
            } else if (annotation instanceof Max ann) {
                String message = String.format(MESSAGE_TEMPLATE, name,
                        MessageFormat.format(MESSAGE_INTERPOLATOR.interpolate(ann.message(), null), ann.value()));
                validationBody.append(MaxValidation.getValidation(accessName, message, ann.value()));
            } else if (annotation instanceof DecimalMin ann) {
                String message = String.format(MESSAGE_TEMPLATE, name,
                        MessageFormat.format(MESSAGE_INTERPOLATOR.interpolate(ann.message(), null), ann.value()));
                if (BigDecimal.class.isAssignableFrom(fieldClass)) {
                    validationBody.append(DecimalMinValidationForBigDecimal.getValidation(accessName, message, ann.value()));
                } else {
                    validationBody.append(DecimalMinValidation.getValidation(accessName, message, ann.value()));
                }
            } else if (annotation instanceof DecimalMax ann) {
                String message = String.format(MESSAGE_TEMPLATE, name,
                        MessageFormat.format(MESSAGE_INTERPOLATOR.interpolate(ann.message(), null), ann.value()));
                if (BigDecimal.class.isAssignableFrom(fieldClass)) {
                    validationBody.append(DecimalMaxValidationForBigDecimal.getValidation(accessName, message, ann.value()));
                } else {
                    validationBody.append(DecimalMaxValidation.getValidation(accessName, message, ann.value()));
                }
            }
        });
    }

    private void buildStringPropertyValidation(Field field, String accessName, StringBuilder validationBody, String parent) {
        String name = messagePropertiesName(field, parent);
        Arrays.stream(field.getAnnotations()).forEach(annotation -> {
            if (annotation instanceof NotBlank ann) {
                String message = String.format(MESSAGE_TEMPLATE, name, MESSAGE_INTERPOLATOR.interpolate(ann.message(), null));
                validationBody.append(NotBlankValidation.getValidation(accessName, message));
            } else if (annotation instanceof Size ann) {
                String message = String.format(MESSAGE_TEMPLATE, name,
                        MessageFormat.format(MESSAGE_INTERPOLATOR.interpolate(ann.message(), null), ann.min(), ann.max()));
                validationBody.append(SizeValidationForString.getValidation(accessName, message, ann.min(), ann.max()));
            } else if (annotation instanceof NotEmpty ann) {
                String message = String.format(MESSAGE_TEMPLATE, name, MESSAGE_INTERPOLATOR.interpolate(ann.message(), null));
                validationBody.append(NotEmptyValidationForString.getValidation(accessName, message));
            } else if (annotation instanceof Pattern ann) {
                String message = String.format(MESSAGE_TEMPLATE, name, MESSAGE_INTERPOLATOR.interpolate(ann.message(), null));
                String patternFieldName = accessName + "Pattern" + (index++);
                String patternVariable = variable(ann.regexp());
                this.fields.add(PatternValidation.patternField(patternFieldName, patternVariable));
                validationBody.append(PatternValidation.getValidation(accessName, message, patternFieldName));
            }
        });
    }

    private Class<?> rawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            throw new Error("unsupported type, type=" + type);
        }
    }

    private boolean isValueClass(Class<?> fieldClass) {
        return fieldClass.getName().startsWith("java.")
                || fieldClass.isEnum()
                || "org.bson.types.ObjectId".equals(fieldClass.getCanonicalName());
    }

    private Class<?> listValueClass(Type type) {
        return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private Type mapValueType(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments()[1];
    }

    private boolean isList(Type type) {
        return List.class.isAssignableFrom(rawClass(type));
    }

    private String variable(String text) {
        if (text == null) return "null";

        var builder = new StringBuilder("\"");
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            switch (ch) {
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                default -> builder.append(ch);
            }
        }
        builder.append('\"');
        return builder.toString();
    }
}
