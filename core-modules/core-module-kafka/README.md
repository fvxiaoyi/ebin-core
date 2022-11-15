# core-module-kafka

## 1. Listener
Spring's @KafkaListener is for when a topic has a large amount of message data and cannot affect the consumption speed of other consumers, or
When other consumers affect their consumption progress. Using @KafkaListener can be a good solution.
However, it is not convenient to consume multiple topics on @KafkaListener, and the message structures of most topics are different. And a @KafkaListener will spawn one or more Kafka consumers. When there are many topics subscribed, the system will generate too many consumer threads.

It is recommended to use DispatcherKafkaListener for projects that subscribe to multiple topics and have a small amount of data.

DispatcherKafkaListener will subscribe to multiple topics and assign corresponding Messages to different KafkaMessageHandlers. All consumption exceptions will be caught, and the corresponding partition and offset will be logged to facilitate subsequent data recovery (kafka seek). DispatcherKafkaListener is completely independent from Spring's KafkaListener and will not affect each other's use.


#### Example
1.Turn on the enable config in the configuration file, such as:
~~~
spring:
    kafka:
        dispatcher:
            enable: true
            topics:         # Topic to subscribe to
                - tp1
                - tp2
                - ...
            concurrency: 1  # How many consumers to enable
~~~

2.Add topic handle, the handle need impl MessageHandler and add KafkaMessageHandler annotation, such as:
~~~
@KafkaMessageHandler(topic = "demo", batch = false)
public class DemoMessageHandle implements MessageHandler<DemoMessage> {
    @Override
    public void handle(Message<DemoMessage> message) throws Exception {

    }
}
~~~