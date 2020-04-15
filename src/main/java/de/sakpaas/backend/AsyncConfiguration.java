package de.sakpaas.backend;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Async
public class AsyncConfiguration {
  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfiguration.class);

  /**
   * Configuration for Async method execution.
   *
   * @return Executor for Async Configuration
   */
  @Bean(name = "taskExecutor")
  public CompletableFuture<Executor> taskExecutor() {
    LOGGER.debug("Creating Async Task Executor");
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(6);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("DatabaseCreationThread-");
    executor.initialize();
    return CompletableFuture.completedFuture(executor);
  }
}
