package org.camunda.community.migration.processInstance.api.model.data;

import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.processInstance.api.model.data.Builder.*;
import static org.camunda.community.migration.processInstance.api.model.data.Builder.Json.*;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** These tests assert that each builder method does affect the built pojo in the specified way */
public class ApiIntegrityTest {
  private static final Logger LOG = LoggerFactory.getLogger(ApiIntegrityTest.class);

  @TestFactory
  Stream<DynamicContainer> shouldBuildCorrectly() {
    return Arrays.stream(Builder.class.getMethods())
        .map(
            m ->
                (Supplier<Object>)
                    () -> {
                      try {
                        return m.invoke(null);
                      } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                      }
                    })
        .map(this::builderClassContainer);
  }

  @TestFactory
  Stream<DynamicTest> shouldVerifyEqualsAndHashcode() {
    return Arrays.stream(Builder.class.getMethods())
        .map(
            m -> {
              try {
                return ((FinalBuildStep<?>) m.invoke(null)).build();
              } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
              } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
              }
            })
        .map(
            builder ->
                DynamicTest.dynamicTest(
                    builder.getClass().getName(),
                    () ->
                        EqualsVerifier.forClass(builder.getClass())
                            .suppress(Warning.NONFINAL_FIELDS, Warning.STRICT_INHERITANCE)
                            .withRedefinedSuperclass()
                            .withPrefabValues(
                                Map.class,
                                Collections.singletonMap("foo", "bar"),
                                Collections.singletonMap("foo", "baz"))
                            .withPrefabValues(JsonNode.class, text("foo"), text("bar"))
                            .verify()));
  }

  DynamicContainer builderClassContainer(Supplier<Object> builderSupplier) {
    Object builder = builderSupplier.get();
    Map<String, Map<List<String>, Optional<Method>>> mappedMethods =
        Arrays.stream(builder.getClass().getMethods())
            .filter(method -> method.getName().startsWith("with"))
            .collect(
                Collectors.groupingBy(
                    Method::getName,
                    Collectors.groupingBy(
                        method ->
                            Arrays.stream(method.getParameterTypes())
                                .map(Class::getName)
                                .collect(Collectors.toList()),
                        Collectors.reducing((a, b) -> b))));
    List<DynamicTest> test = new ArrayList<>();
    mappedMethods.forEach(
        (methodName, value) ->
            value.forEach(
                (parameterTypes, methodOptional) ->
                    test.add(
                        builderMethodTest(
                            builderSupplier,
                            methodName,
                            parameterTypes.stream()
                                .map(
                                    c -> {
                                      try {
                                        return Class.forName(c);
                                      } catch (ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                      }
                                    })
                                .collect(Collectors.toList()),
                            methodOptional.orElseThrow(RuntimeException::new)))));
    return DynamicContainer.dynamicContainer(builder.getClass().getSimpleName(), test);
  }

  DynamicTest builderMethodTest(
      Supplier<Object> builderSupplier,
      String methodName,
      List<Class<?>> parameterTypes,
      Method method) {
    return DynamicTest.dynamicTest(
        String.format(
            "%s(%s)",
            methodName,
            parameterTypes.stream().map(Class::getName).collect(Collectors.joining(", "))),
        () -> {
          Object builder = builderSupplier.get();
          LOG.info(
              "Checking method {}({})",
              methodName,
              parameterTypes.stream().map(Class::getName).collect(Collectors.joining(", ")));
          String getterMethodName = findGetterMethodName(methodName);
          LOG.info("Getter should be {}()", getterMethodName);
          BuilderTestData data = findData(methodName, parameterTypes);
          method.invoke(builder, data.builderArgs);
          LOG.info("Builder invoked with {}", data.builderArgs);
          Method build = builder.getClass().getMethod("build");
          Object result = build.invoke(builder);
          Method getter = result.getClass().getMethod(getterMethodName);
          Object gotValue = getter.invoke(result);
          Assertions.assertThat(gotValue).isEqualTo(data.expectedGetterValue);
        });
  }

  private String findGetterMethodName(String methodName) {
    return Stream.of(
            entry("withVariable", "getVariables"),
            entry("withCompletedInstanceLoopCounter", "getCompletedInstanceLoopCounters"),
            entry("withActivity", "getActivities"),
            entry("withInstance", "getInstances"))
        .collect(Collectors.toMap(MapEntry::getKey, MapEntry::getValue))
        .getOrDefault(methodName, methodName.replace("with", "get"));
  }

  private BuilderTestData findData(String methodName, List<Class<?>> parameterTypes) {
    BuilderTestData data = new BuilderTestData();
    data.parameterTypes = parameterTypes;
    data.methodName = methodName;
    Arrays.asList(
            new ParameterTypeFilteringTestDataVisitor(
                Collections.singletonList(String.class),
                d -> {
                  d.expectedGetterValue = "foo";
                  d.builderArgs = new Object[] {"foo"};
                }),
            new ParameterTypeFilteringTestDataVisitor(
                Collections.singletonList(Boolean.class),
                d -> {
                  d.expectedGetterValue = true;
                  d.builderArgs = new Object[] {true};
                }),
            new MethodNameFilteringTestDataVisitor(
                "withInstances",
                d -> {
                  List<ActivityNodeData> list = Collections.singletonList(manualTaskData().build());
                  d.expectedGetterValue = list;
                  d.builderArgs = new Object[] {list};
                }),
            new MethodNameFilteringTestDataVisitor(
                "withInstance",
                d -> {
                  ActivityNodeData taskData = manualTaskData().build();
                  d.expectedGetterValue = Collections.singletonList(taskData);
                  d.builderArgs = new Object[] {taskData};
                }),
            new MethodNameFilteringTestDataVisitor(
                "withVariables",
                d -> {
                  Map<String, JsonNode> variables = Collections.singletonMap("foo", text("bar"));
                  d.expectedGetterValue = variables;
                  d.builderArgs = new Object[] {variables};
                }),
            new MethodNameAndParameterTypeFilteringTestDataVisitor(
                Collections.singletonList(List.class),
                "withActivities",
                d -> {
                  d.expectedGetterValue = Collections.singletonList(manualTaskData().build());
                  d.builderArgs =
                      new Object[] {Collections.singletonList(manualTaskData().build())};
                }),
            new MethodNameAndParameterTypeFilteringTestDataVisitor(
                Collections.singletonList(CommonActivityNodeData.class),
                "withActivity",
                d -> {
                  ActivityNodeData taskData = manualTaskData().build();
                  d.expectedGetterValue = Collections.singletonList(taskData);
                  d.builderArgs = new Object[] {taskData};
                }),
            new MethodNameFilteringTestDataVisitor(
                "withVariable",
                d -> {
                  d.expectedGetterValue =
                      Collections.<String, JsonNode>singletonMap("foo", text("bar"));
                  d.builderArgs = new Object[] {"foo", text("bar")};
                }),
            new MethodNameAndParameterTypeFilteringTestDataVisitor(
                Collections.singletonList(Map.class),
                "withActivities",
                d -> {
                  Map<String, ActivityNodeData> activities =
                      Collections.singletonMap("foo", manualTaskData().build());
                  d.expectedGetterValue = activities;
                  d.builderArgs = new Object[] {activities};
                }),
            new MethodNameAndParameterTypeFilteringTestDataVisitor(
                Arrays.asList(String.class, ActivityNodeData.class),
                "withActivity",
                d -> {
                  ActivityNodeData taskData = manualTaskData().build();
                  d.expectedGetterValue = Collections.singletonMap("foo", taskData);
                  d.builderArgs = new Object[] {"foo", taskData};
                }),
            new MethodNameFilteringTestDataVisitor(
                "withProcessInstance",
                d -> {
                  ProcessInstanceData processInstanceData = processInstanceData().build();
                  d.expectedGetterValue = processInstanceData;
                  d.builderArgs = new Object[] {processInstanceData};
                }),
            new MethodNameFilteringTestDataVisitor(
                "withCompletedInstanceLoopCounter",
                d -> {
                  d.expectedGetterValue = Collections.singletonList(3);
                  d.builderArgs = new Object[] {3};
                }),
            new MethodNameFilteringTestDataVisitor(
                "withCompletedInstanceLoopCounters",
                d -> {
                  Iterable<Integer> values = Collections.singletonList(3);
                  d.expectedGetterValue = values;
                  d.builderArgs = new Object[] {values};
                }))
        .forEach(v -> v.visit(data));
    if (data.expectedGetterValue == null || data.builderArgs == null) {
      throw new RuntimeException(
          "No mock data for method '"
              + methodName
              + "' and types '"
              + parameterTypes.stream().map(Class::getName).collect(Collectors.joining("', '"))
              + "'");
    }
    return data;
  }

  private interface TestDataVisitor {
    void visit(BuilderTestData testData);
  }

  private static class ParameterTypeFilteringTestDataVisitor implements TestDataVisitor {
    private final List<Class<?>> parameterTypes;
    private final Consumer<BuilderTestData> visitor;

    public ParameterTypeFilteringTestDataVisitor(
        List<Class<?>> parameterTypes, Consumer<BuilderTestData> visitor) {
      this.parameterTypes = parameterTypes;
      this.visitor = visitor;
    }

    @Override
    public void visit(BuilderTestData testData) {
      if (testData.parameterTypes.equals(parameterTypes)) {
        visitor.accept(testData);
      }
    }
  }

  private static class MethodNameFilteringTestDataVisitor implements TestDataVisitor {
    private final String methodName;
    private final Consumer<BuilderTestData> visitor;

    public MethodNameFilteringTestDataVisitor(
        String methodName, Consumer<BuilderTestData> visitor) {
      this.methodName = methodName;
      this.visitor = visitor;
    }

    @Override
    public void visit(BuilderTestData testData) {
      if (testData.methodName.equals(methodName)) {
        visitor.accept(testData);
      }
    }
  }

  private static class MethodNameAndParameterTypeFilteringTestDataVisitor
      implements TestDataVisitor {
    private final List<Class<?>> parameterTypes;
    private final String methodName;
    private final Consumer<BuilderTestData> visitor;

    public MethodNameAndParameterTypeFilteringTestDataVisitor(
        List<Class<?>> parameterTypes, String methodName, Consumer<BuilderTestData> visitor) {
      this.parameterTypes = parameterTypes;
      this.methodName = methodName;
      this.visitor = visitor;
    }

    @Override
    public void visit(BuilderTestData testData) {
      if (testData.parameterTypes.equals(parameterTypes)
          && testData.methodName.equals(methodName)) {
        visitor.accept(testData);
      }
    }
  }

  private static class BuilderTestData {
    private String methodName;
    private List<Class<?>> parameterTypes;
    private Object[] builderArgs;
    private Object expectedGetterValue;
  }
}
