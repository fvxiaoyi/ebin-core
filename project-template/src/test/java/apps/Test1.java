package apps;

import org.apache.kafka.clients.consumer.ConsumerPartitionAssignor;
import org.apache.kafka.clients.consumer.RangeAssignor;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.clients.consumer.StickyAssignor;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        AbstractPartitionAssignor rangeAssignor = new StickyAssignor();
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put("A", 3);
        partitionsPerTopic.put("B", 2);
        partitionsPerTopic.put("C", 1);

        Map<String, ConsumerPartitionAssignor.Subscription> subscriptions = new HashMap<>();
        subscriptions.put("C1", new ConsumerPartitionAssignor.Subscription(List.of("A", "B", "C")));
        subscriptions.put("C2", new ConsumerPartitionAssignor.Subscription(List.of("A", "B", "C")));
        subscriptions.put("C3", new ConsumerPartitionAssignor.Subscription(List.of("A", "B", "C")));

        Map<String, List<TopicPartition>> assign = rangeAssignor.assign(partitionsPerTopic, subscriptions);
        System.out.println(assign);
//        ExecutorService executorService = Executors.newFixedThreadPool(2);
//        executorService.execute(new A(1));
//        executorService.execute(new A(2));
//        executorService.execute(new A(3));
//        executorService.shutdown();
//        Thread.sleep(1000000L);
    }

    public static class A implements Runnable {
        public int num;

        public A(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + " " + num);
            }
        }
    }
}
