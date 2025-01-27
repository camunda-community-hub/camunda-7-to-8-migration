package org.camunda.community.migration.processInstance.migrator.impl;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrationSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

public class ProcessInstanceMigrationExecutor implements SmartLifecycle {
  private static final Logger LOG = LoggerFactory.getLogger(ProcessInstanceMigrationExecutor.class);
  private final ScheduledExecutorService extractExecutorService;
  private final ScheduledExecutorService startExecutorService;
  private final ProcessInstanceMigrationSteps migrationSteps;
  private final Duration rate;
  private boolean running = false;

  public ProcessInstanceMigrationExecutor(
      ScheduledExecutorService extractExecutorService,
      ScheduledExecutorService startExecutorService,
      ProcessInstanceMigrationSteps migrationSteps,
      Duration rate) {
    this.extractExecutorService = extractExecutorService;
    this.startExecutorService = startExecutorService;
    this.migrationSteps = migrationSteps;
    this.rate = rate;
  }

  public ProcessInstanceMigrationExecutor(
      ProcessInstanceMigrationSteps migrationSteps, Duration rate) {
    this(
        Executors.newSingleThreadScheduledExecutor(),
        Executors.newSingleThreadScheduledExecutor(),
        migrationSteps,
        rate);
  }

  @Override
  public void start() {
    startExecutorService.scheduleWithFixedDelay(
        () -> executeIgnoreError(migrationSteps::updateToStarted),
        1,
        rate.getSeconds(),
        TimeUnit.SECONDS);
    extractExecutorService.scheduleWithFixedDelay(
        () -> executeIgnoreError(migrationSteps::updateToExtracted),
        0,
        rate.getSeconds(),
        TimeUnit.SECONDS);
    running = true;
  }

  private void executeIgnoreError(Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void stop() {
    extractExecutorService.shutdown();
    startExecutorService.shutdown();
    running = false;
  }

  @Override
  public boolean isRunning() {
    return running;
  }
}
