package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

public class Camunda7MigrationConditions {
  public static ArchCondition<JavaMethod> haveCamundaBpmParameterTypes() {
    return new ArchCondition<>("not have camunda bpm parameter types") {
      @Override
      public void check(JavaMethod item, ConditionEvents events) {
        for (JavaClass parameterType : item.getRawParameterTypes()) {
          if (parameterType.getPackage().getName().startsWith("org.camunda.bpm")) {
            events.add(
                SimpleConditionEvent.satisfied(
                    item,
                    String.format(
                        "Parameter type %s comes from camunda bpm", parameterType.getName())));
          }
        }
      }
    };
  }
}
