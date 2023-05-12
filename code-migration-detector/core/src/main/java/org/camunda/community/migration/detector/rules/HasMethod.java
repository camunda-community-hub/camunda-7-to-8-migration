package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

/** Class condition checking that a class has at least one method matching given condition. */
public class HasMethod extends ArchCondition<JavaClass> {

  private final DescribedPredicate<JavaMethod> predicate;

  public HasMethod(DescribedPredicate<JavaMethod> predicate) {
    super("has method which " + predicate.getDescription());
    this.predicate = predicate;
  }

  @Override
  public void check(JavaClass javaClass, ConditionEvents events) {
    javaClass.getMethods().stream()
        .filter(this.predicate)
        .forEach(
            (method) -> events.add(new SimpleConditionEvent(method, true, method.getFullName())));
  }
}
