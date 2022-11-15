package core.framework.kafka.consumer;

import core.framework.kafka.annotation.KafkaMessageHandler;
import core.framework.kafka.configuration.DispatcherKafkaListenerProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerConfigUtils;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static core.framework.kafka.configuration.DispatcherKafkaListenerConfiguration.DISPATCHER_KAFKA_LISTENER_CONTAINER_FACTORY_NAME;
import static org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor.DEFAULT_KAFKA_LISTENER_CONTAINER_FACTORY_BEAN_NAME;

/**
 * @author ebin
 */
public class ExtendKafkaListenerAnnotationBeanPostProcessor
        implements BeanPostProcessor, Ordered, ApplicationContextAware, SmartInitializingSingleton {
    private static final String GENERATED_ID_PREFIX = DispatcherKafkaListenerEndpoint.class.getName() + "Container#";
    private final AtomicInteger counter = new AtomicInteger();
    private final KafkaListenerEndpointRegistrar registrar = new KafkaListenerEndpointRegistrar();
    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;
    private KafkaListenerEndpointRegistry endpointRegistry;

    @Override
    public void afterSingletonsInstantiated() {
        this.registrar.setBeanFactory(this.beanFactory);

        if (this.beanFactory instanceof ListableBeanFactory) {
            Map<String, KafkaListenerConfigurer> instances =
                    ((ListableBeanFactory) this.beanFactory).getBeansOfType(KafkaListenerConfigurer.class);
            for (KafkaListenerConfigurer configurer : instances.values()) {
                configurer.configureKafkaListeners(this.registrar);
            }
        }

        if (this.registrar.getEndpointRegistry() == null) {
            if (this.endpointRegistry == null) {
                Assert.state(this.beanFactory != null,
                        "BeanFactory must be set to find endpoint registry by bean name");
                this.endpointRegistry = this.beanFactory.getBean(
                        KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME,
                        KafkaListenerEndpointRegistry.class);
            }
            this.registrar.setEndpointRegistry(this.endpointRegistry);
        }

        this.registrar.setContainerFactoryBeanName(DEFAULT_KAFKA_LISTENER_CONTAINER_FACTORY_BEAN_NAME);

        // Actually register all listeners
        registerDispatchedKafkaListener();
        this.registrar.afterPropertiesSet();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        if (applicationContext instanceof ConfigurableApplicationContext) {
            setBeanFactory(((ConfigurableApplicationContext) applicationContext).getBeanFactory());
        } else {
            setBeanFactory(applicationContext);
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (bean instanceof MessageHandler) {
            KafkaMessageHandler ann = AnnotatedElementUtils.findMergedAnnotation(targetClass, KafkaMessageHandler.class);
            if (ann != null) {
                Arrays.stream(bean.getClass().getGenericInterfaces())
                        .filter(f -> ((ParameterizedType) bean.getClass().getGenericInterfaces()[0]).getRawType() == MessageHandler.class)
                        .findFirst()
                        .ifPresent(m -> {
                            Type actualTypeArgument = ((ParameterizedType) m).getActualTypeArguments()[0];
                            MessageHandlerAdapterHolder.addMessageHandler(ann.topic(), new MessageHandlerAdapter<>(ann, (MessageHandler<?>) bean, actualTypeArgument));
                        });
            }
        }
        return bean;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private void registerDispatchedKafkaListener() {
        DispatcherKafkaListenerProperties properties = this.beanFactory.getBean(DispatcherKafkaListenerProperties.class);
        DispatcherKafkaListenerEndpoint endpoint = new DispatcherKafkaListenerEndpoint();
        endpoint.setId(GENERATED_ID_PREFIX + this.counter.getAndIncrement());
        endpoint.setTopics(properties.getTopics().toArray(new String[]{}));
        endpoint.setConcurrency(properties.getConcurrency());
        endpoint.setAutoStartup(true);
        endpoint.setSplitIterables(true);
        endpoint.setBatchListener(true);

        KafkaListenerContainerFactory<?> listenerContainerFactory = (KafkaListenerContainerFactory<?>) this.beanFactory.getBean(DISPATCHER_KAFKA_LISTENER_CONTAINER_FACTORY_NAME);
        this.registrar.registerEndpoint(endpoint, listenerContainerFactory);
        endpoint.setBeanFactory(this.beanFactory);
    }
}