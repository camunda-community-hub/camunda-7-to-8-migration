package org.camunda.community.migration;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.config.processor.BeanInfoPostProcessor;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import org.camunda.bpm.client.spring.impl.client.ClientConfiguration;
import org.camunda.bpm.client.spring.impl.subscription.SpringTopicSubscriptionImpl;
import org.camunda.community.migration.worker.ExternalTaskHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalTaskWorkerRegistration extends BeanInfoPostProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(ExternalTaskWorkerRegistration.class);
  private final ClientConfiguration clientConfiguration;

  public ExternalTaskWorkerRegistration(ClientConfiguration clientConfiguration) {
    this.clientConfiguration = clientConfiguration;
  }

  private Long calculateLockDuration(SpringTopicSubscriptionImpl subscription) {
    Long lockDuration = clientConfiguration.getLockDuration();
    if (subscription.getLockDuration() != null && subscription.getLockDuration() < 0L) {
      lockDuration = subscription.getLockDuration();
    }
    return lockDuration;
  }

  @Override
  public Consumer<ZeebeClient> apply(ClassInfo beanInfo) {
    LOG.info("Registering Zeebe worker(s) of bean: {}", beanInfo.getBean());
    SpringTopicSubscriptionImpl bean = (SpringTopicSubscriptionImpl) beanInfo.getBean();
    return zeebeClient -> {
      JobWorkerBuilderStep3 builder =
          zeebeClient
              .newWorker()
              .jobType(bean.getTopicName())
              .handler(
                  new ExternalTaskHandlerWrapper(bean.getExternalTaskHandler(), Optional.empty()))
              .name(beanInfo.getBeanName());
      setIfPresent(calculateLockDuration(bean), builder::timeout);
      setIfPresent(clientConfiguration.getMaxTasks(), builder::maxJobsActive);
      setIfPresent(
          clientConfiguration.getAsyncResponseTimeout(),
          timeout -> builder.pollInterval(Duration.ofMillis(timeout)));
      setIfPresent(bean.getVariableNames(), builder::fetchVariables);
      setIfPresent(
          clientConfiguration.getAsyncResponseTimeout(),
          timeout -> builder.requestTimeout(Duration.ofMillis(timeout)));
      builder.open();
    };
  }

  private <T> void setIfPresent(T value, Consumer<T> setter) {
    if (value != null) {
      setter.accept(value);
    }
  }

  @Override
  public boolean test(ClassInfo classInfo) {
    LOG.info("Checking {}", classInfo.getBean().getClass());
    return SpringTopicSubscriptionImpl.class.isAssignableFrom(classInfo.getBean().getClass());
  }
}
