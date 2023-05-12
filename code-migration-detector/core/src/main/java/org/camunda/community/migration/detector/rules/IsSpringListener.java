package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import org.springframework.context.event.EventListener;

/**
 * Predicate matching if the method is a Spring Listener.
 *
 * @param <T> type of the Spring event.
 */
public class IsSpringListener<T> extends DescribedPredicate<JavaMethod> {
  private final Class<T> eventType;

  public IsSpringListener(Class<T> eventType) {
    super("is a Spring Event Listener for " + eventType.getName());
    this.eventType = eventType;
  }

  @Override
  public boolean test(JavaMethod javaMethod) {
    return javaMethod.isAnnotatedWith(EventListener.class)
        && javaMethod.getRawParameterTypes().stream()
            .anyMatch((clazz) -> eventType.isAssignableFrom(clazz.reflect()));
  }
}
