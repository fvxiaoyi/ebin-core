package core.framework.kafka.publisher;

import core.framework.json.JSON;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author ebin
 */
public class MessagePublisher {
    @Autowired
    private KafkaTemplate<byte[], byte[]> producer;
    @Autowired
    private Validator validator;

    public void publish(String topic, String key, Object value) {
        if (topic == null) throw new RuntimeException("topic must not be null");
        if (value == null) throw new RuntimeException("value must not be null");

        Set<ConstraintViolation<Object>> result = validator.validate(value);
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }

        byte[] keyBytes = key == null ? null : key.getBytes(UTF_8);
        byte[] message = JSON.toJSON(value).getBytes(UTF_8);
        var producerRecord = new ProducerRecord<>(topic, null, System.currentTimeMillis(), keyBytes, message, null);
        linkContext(producerRecord.headers());
        producer.send(producerRecord);
    }

    private void linkContext(Headers headers) {
        //TODO
    }

}
