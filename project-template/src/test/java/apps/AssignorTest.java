package apps;

import org.apache.kafka.clients.consumer.ConsumerPartitionAssignor;
import org.apache.kafka.clients.consumer.RangeAssignor;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class AssignorTest {
    public static void main(String[] args) {
        Map<String, Integer> partitionsPerTopic = Map.of(
                "T1", 1,
                "T2", 1,
                "T3", 1
        );
        Map<String, ConsumerPartitionAssignor.Subscription> subscriptions = Map.of(
                "C1", new ConsumerPartitionAssignor.Subscription(List.of("T1", "T2", "T3")),
                "C2", new ConsumerPartitionAssignor.Subscription(List.of("T1", "T2", "T3")),
                "C3", new ConsumerPartitionAssignor.Subscription(List.of("T1", "T2", "T3"))
        );
        AbstractPartitionAssignor roundRobinAssignor = new RangeAssignor();
        Map<String, List<TopicPartition>> assign = roundRobinAssignor.assign(partitionsPerTopic, subscriptions);
        System.out.println(assign);
    }
}
