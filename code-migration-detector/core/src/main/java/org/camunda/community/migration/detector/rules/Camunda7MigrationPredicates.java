package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import org.springframework.context.event.EventListener;

public class Camunda7MigrationPredicates {
  public static DescribedPredicate<JavaMethod> isSpringEventListener() {
    return new DescribedPredicate<>("is spring event listener") {
      @Override
      public boolean test(JavaMethod javaMethod) {
        return javaMethod.isAnnotatedWith(EventListener.class);
      }
    };
  }

  public static DescribedPredicate<JavaClass> camunda7Api() {
    return new DescribedPredicate<>("camunda 7 api") {
      @Override
      public boolean test(JavaClass javaClass) {
        return javaClass.getPackageName().startsWith("org.camunda.bpm");
      }
    };
  }
}
