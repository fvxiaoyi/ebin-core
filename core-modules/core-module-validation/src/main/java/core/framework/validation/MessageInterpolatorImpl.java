package core.framework.validation;

import javax.validation.MessageInterpolator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author ebin
 */
public class MessageInterpolatorImpl implements MessageInterpolator {
    private static Map<String, String> message = new HashMap<>();
    private static final String MIN_MESSAGE = "must be greater than or equal to {0}";
    private static final String MAX_MESSAGE = "must be less than or equal to {0}";

    static {
        message.put("{javax.validation.constraints.NotNull.message}", "must not be null");
        message.put("{javax.validation.constraints.NotBlank.message}", "must not be blank");
        message.put("{javax.validation.constraints.NotEmpty.message}", "must not be empty");
        message.put("{javax.validation.constraints.Size.message}", "size must be between {0} and {1}");
        message.put("{javax.validation.constraints.Pattern.message}", "must match pattern");
        message.put("{javax.validation.constraints.Max.message}", MAX_MESSAGE);
        message.put("{javax.validation.constraints.Min.message}", MIN_MESSAGE);
        message.put("{javax.validation.constraints.DecimalMax.message}", MAX_MESSAGE);
        message.put("{javax.validation.constraints.DecimalMin.message}", MIN_MESSAGE);
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return message.getOrDefault(messageTemplate, messageTemplate);
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        return this.interpolate(messageTemplate, context);
    }
}
