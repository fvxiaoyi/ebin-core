package apps;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author ebin
 */
public class ProduceTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Object> config = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.19.128:30200",
                ProducerConfig.COMPRESSION_TYPE_CONFIG, CompressionType.SNAPPY.name,
                ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60_000,                           // 60s, DELIVERY_TIMEOUT_MS_CONFIG is INT type
                ProducerConfig.LINGER_MS_CONFIG, 5L,                                         // use small linger time within acceptable range to improve batching
                ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 500L,                            // longer backoff to reduce cpu usage when kafka is not available
                ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000L,                      // 5s
                ProducerConfig.MAX_BLOCK_MS_CONFIG, 30_000L);

        var serializer = new ByteArraySerializer();
        var producer = new KafkaProducer<>(config, serializer, serializer);
        byte[] keyBytes = null;
        var record = new ProducerRecord<>("otlp_spans", null, System.currentTimeMillis(), keyBytes, "abc".getBytes(UTF_8), null);
        Future<RecordMetadata> send = producer.send(record, new KafkaCallback(record));
        send.get();
    }

    static final class KafkaCallback implements Callback {
        private static final Logger LOGGER = LoggerFactory.getLogger(KafkaCallback.class);
        private final ProducerRecord<byte[], byte[]> record;

        KafkaCallback(ProducerRecord<byte[], byte[]> record) {
            this.record = record;
        }

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception != null) {    // if failed to send message (kafka is down), fallback to error output
                byte[] key = record.key();
                LOGGER.error("failed to send kafka message, error={}, topic={}, key={}, value={}",
                        exception.getMessage(),
                        record.topic(),
                        key == null ? null : new String(key, UTF_8),
                        new String(record.value(), UTF_8),
                        exception);
            } else {
                System.out.println("ok");
            }

        }
    }
}
