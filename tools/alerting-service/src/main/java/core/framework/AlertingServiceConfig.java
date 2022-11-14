package core.framework;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ebin
 */
@Configuration
public class AlertingServiceConfig {
    private static final int DOMAIN_EVENT_TASK_EXECUTOR_AWAIT_TERMINATION_SECONDS = 60 * 2;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadPriority(Thread.MAX_PRIORITY);
        taskExecutor.setThreadGroupName("DomainEventThreadGroup");
        taskExecutor.setThreadNamePrefix("DomainEventThread");
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(DOMAIN_EVENT_TASK_EXECUTOR_AWAIT_TERMINATION_SECONDS);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
