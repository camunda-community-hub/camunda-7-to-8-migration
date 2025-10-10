package org.camunda.community.migration.adapter;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.worker.JobWorker;
import io.camunda.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3;
import io.camunda.client.bean.BeanInfo;
import io.camunda.client.spring.annotation.processor.AbstractCamundaAnnotationProcessor;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.camunda.bpm.client.spring.impl.client.ClientConfiguration;
import org.camunda.bpm.client.spring.impl.subscription.SpringTopicSubscriptionImpl;
import org.camunda.community.migration.adapter.worker.ExternalTaskHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalTaskWorkerRegistration extends AbstractCamundaAnnotationProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(ExternalTaskWorkerRegistration.class);
  private final ClientConfiguration clientConfiguration;
  private final Map<String, Supplier<Object>> springTopicSubscriptions = new HashMap<>();
  private final List<JobWorker> openedWorkers = new ArrayList<>();

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

  private <T> void setIfPresent(T value, Consumer<T> setter) {
    if (value != null) {
      setter.accept(value);
    }
  }

  @Override
  protected boolean isApplicableFor(BeanInfo beanInfo) {
    return SpringTopicSubscriptionImpl.class.isAssignableFrom(beanInfo.getTargetClass());
  }

  @Override
  protected void configureFor(BeanInfo beanInfo) {
    LOG.info("Registering Zeebe worker(s) of bean: {}", beanInfo.getBeanName());
    springTopicSubscriptions.put(beanInfo.getBeanName(), beanInfo.getBeanSupplier());
  }

  @Override
  public void start(CamundaClient zeebeClient) {
    springTopicSubscriptions.forEach(
        (beanName, beanSupplier) -> {
          SpringTopicSubscriptionImpl bean = (SpringTopicSubscriptionImpl) beanSupplier.get();
          JobWorkerBuilderStep3 builder =
              zeebeClient
                  .newWorker()
                  .jobType(bean.getTopicName())
                  .handler(
                      new ExternalTaskHandlerWrapper(
                          bean.getExternalTaskHandler(), Optional.empty()))
                  .name(beanName);
          setIfPresent(calculateLockDuration(bean), builder::timeout);
          setIfPresent(clientConfiguration.getMaxTasks(), builder::maxJobsActive);
          setIfPresent(
              clientConfiguration.getAsyncResponseTimeout(),
              timeout -> builder.pollInterval(Duration.ofMillis(timeout)));
          setIfPresent(bean.getVariableNames(), builder::fetchVariables);
          setIfPresent(
              clientConfiguration.getAsyncResponseTimeout(),
              timeout -> builder.requestTimeout(Duration.ofMillis(timeout)));
          openedWorkers.add(builder.open());
        });
  }

  @Override
  public void stop(CamundaClient zeebeClient) {
    openedWorkers.forEach(JobWorker::close);
  }
}
