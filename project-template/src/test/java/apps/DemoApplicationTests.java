package apps;

import apps.user.domain.User;
import apps.user.domain.service.UserRepo;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static core.framework.jpa.mongodb.configuration.HibernateMongoDBConfiguration.MONGODB_TRANSACTION_MANAGER_BEAN_NAME;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DemoApplicationTests {
    @Autowired
    UserRepo userRepo;

    @Autowired
    @Qualifier(MONGODB_TRANSACTION_MANAGER_BEAN_NAME)
    PlatformTransactionManager mongoTransactionManager;

    @Test
    void contextLoads() {
        TransactionStatus transaction = mongoTransactionManager.getTransaction(TransactionDefinition.withDefaults());
        userRepo.persist(new User("abc"));
        mongoTransactionManager.commit(transaction);
    }

    public static void main(String[] args) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.19.1:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-1");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");                      // refer to org.apache.kafka.clients.consumer.ConsumerConfig, must be in("latest", "earliest", "none")
        var deserializer = new StringDeserializer();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config, deserializer, deserializer);
        consumer.subscribe(Set.of("otlp_spans"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value());
            }
        }
    }
}
