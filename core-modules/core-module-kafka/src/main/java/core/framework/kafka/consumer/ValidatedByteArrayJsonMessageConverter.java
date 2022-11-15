package core.framework.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author ebin
 */
public class ValidatedByteArrayJsonMessageConverter extends ByteArrayJsonMessageConverter {
    private final Validator validator;

    public ValidatedByteArrayJsonMessageConverter(ObjectMapper objectMapper, Validator validator) {
        super(objectMapper);
        this.validator = validator;
    }

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> kafkaRecord, Type type) {
        Object value = super.extractAndConvertValue(kafkaRecord, type);
        if (validator != null) {
            Set<ConstraintViolation<Object>> result = this.validator.validate(value);
            if (!result.isEmpty()) {
                throw new ConstraintViolationException(result);
            }
        }
        return value;
    }
}
