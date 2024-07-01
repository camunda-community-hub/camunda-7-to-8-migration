package org.camunda.community.migration.detector.rules;

import static org.camunda.community.migration.detector.rules.Camunda7MigrationPredicates.*;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Camunda7MigrationConditions {
  public static ArchCondition<JavaMethod> haveCamunda7ParameterTypes() {
    return new ArchCondition<>("have camunda 7 parameter types") {
      @Override
      public void check(JavaMethod item, ConditionEvents events) {
        for (JavaClass parameterType : item.getRawParameterTypes()) {
          if (isCamunda7Code(parameterType)) {
            events.add(
                SimpleConditionEvent.satisfied(
                    item,
                    String.format(
                        "Parameter type %s comes from camunda 7", parameterType.getName())));
          }
        }
      }
    };
  }

  public static ArchCondition<JavaClass> implementCamunda7Interface() {
    return new ArchCondition<>("implement camunda 7 interface") {

      @Override
      public void check(JavaClass javaClass, ConditionEvents conditionEvents) {
        Set<JavaClass> interfaces = new HashSet<>();
        interfaces.addAll(javaClass.getAllRawInterfaces());
        interfaces.addAll(
            javaClass.getAllRawSuperclasses().stream()
                .flatMap(jc -> jc.getAllRawInterfaces().stream())
                .collect(Collectors.toSet()));
        Set<JavaClass> violationCandidates = new HashSet<>();
        for (JavaClass interFace : interfaces) {
          if (isCamunda7Code(interFace)) {
            violationCandidates.add(interFace);
          }
        }
        for (JavaClass interFace : violationCandidates) {
          conditionEvents.add(
              SimpleConditionEvent.satisfied(
                  javaClass,
                  String.format(
                      "Class <%s> does implement <%s> in %s",
                      javaClass.getName(),
                      interFace.getName(),
                      javaClass.getSourceCodeLocation())));
        }
      }
    };
  }

  private static boolean isCamunda7Code(JavaClass javaClass) {
    return javaClass.getPackageName().startsWith("org.camunda.bpm");
  }
}
