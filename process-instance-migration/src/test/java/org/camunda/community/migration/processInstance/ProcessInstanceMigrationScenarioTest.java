package org.camunda.community.migration.processInstance;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.process.test.api.CamundaProcessTest;
import io.camunda.zeebe.client.ZeebeClient;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.community.migration.processInstance.service.Camunda7Service;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.MigrationTaskService;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintService;
import org.camunda.community.migration.processInstance.service.TaskMappingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

@CamundaProcessTest
@ExtendWith(ProcessEngineExtension.class)
@Disabled
public class ProcessInstanceMigrationScenarioTest {

  public ZeebeClient zeebeClient;

  private static Stream<
          Entry<MigrationTestProcessDefinitionInput, List<MigrationTestProcessInstanceInput>>>
      input() {
    Map<MigrationTestProcessDefinitionInput, List<MigrationTestProcessInstanceInput>> input =
        new HashMap<>();
    input.put(
        new MigrationTestProcessDefinitionInput(
            "bpmn/c7/subprocess.bpmn", "bpmn/c8/subprocess.bpmn", "SubProcess"),
        Collections.singletonList(
            new MigrationTestProcessInstanceInput(
                "Moved in subprocess",
                Collections.singletonMap("a", "b"),
                Collections.singletonList(pi -> complete(task())),
                Collections.singletonList("InSubprocessTaskTask"))));
    input.put(
        new MigrationTestProcessDefinitionInput(
            "bpmn/c7/user-tasks-linear.bpmn",
            "bpmn/c8/user-tasks-linear.bpmn",
            "UserTasksLinearProcess"),
        Collections.singletonList(
            new MigrationTestProcessInstanceInput(
                "Advanced to step 2",
                new HashMap<>(),
                Collections.singletonList(pi -> complete(task())),
                Collections.singletonList("UserTask2Task"))));
    input.put(
        new MigrationTestProcessDefinitionInput(
            "bpmn/c7/user-tasks-parallel.bpmn",
            "bpmn/c8/user-tasks-parallel.bpmn",
            "UserTasksParallelProcess"),
        Collections.singletonList(
            new MigrationTestProcessInstanceInput(
                "Advanced on both branches",
                new HashMap<>(),
                Arrays.asList(
                    pi -> complete(task(taskQuery().taskName("Task 1A"))),
                    pi -> complete(task(taskQuery().taskName("Task 1B")))),
                Arrays.asList("Task2ATask", "Task2BTask"))));
    return input.entrySet().stream();
  }

  private static ScenarioTestSuite buildFrom(
      ZeebeClient zeebeClient,
      MigrationTestProcessDefinitionInput pdInput,
      MigrationTestProcessInstanceInput piInput) {
    CamundaOperateClient operateClient = operateClient();
    Camunda8Service camunda8Service = camunda8Service(zeebeClient, operateClient);
    MigrationTaskService taskService = taskService(camunda8Service);
    Camunda7Service camunda7Service = camunda7Service();
    TaskMappingService taskMappingService = taskMappingService(camunda7Service);
    ZeebeJobClient zeebeJobClient =
        zeebeJobClient(camunda7Service, taskService, camunda8Service, taskMappingService);
    return new ScenarioTestSuite(
        //        pdInput,
        //        piInput,
        //        operateClient,
        //        camunda8Service,
        //        taskService,
        //        zeebeClient,
        //        zeebeJobClient
        );
  }

  private static TaskMappingService taskMappingService(Camunda7Service camunda7Service) {
    return new TaskMappingService(camunda7Service);
  }

  private static Camunda7Service camunda7Service() {
    return new Camunda7Service(
        new Camunda7EmbeddedClient(new ObjectMapper()), Collections.emptySet());
  }

  private static ZeebeJobClient zeebeJobClient(
      Camunda7Service camunda7Service,
      MigrationTaskService taskService,
      Camunda8Service camunda8Service,
      TaskMappingService taskMappingService) {
    return new ZeebeJobClient(
        camunda7Service,
        taskService,
        camunda8Service,
        mock(ProcessDefinitionMigrationHintService.class),
        taskMappingService);
  }

  private static Camunda8Service camunda8Service(
      ZeebeClient zeebeClient, CamundaOperateClient operateClient) {
    return new Camunda8Service(zeebeClient, operateClient);
  }

  private static CamundaOperateClient operateClient() {
    return mock(CamundaOperateClient.class);
  }

  private static MigrationTaskService taskService(Camunda8Service camunda8Service) {
    return new MigrationTaskService(camunda8Service);
  }

  @TestFactory
  Stream<DynamicContainer> scenarios() {
    return input()
        .map(
            e ->
                DynamicContainer.dynamicContainer(
                    e.getKey().getBpmnProcessId(),
                    e.getValue().stream()
                        .map(
                            piInput -> {
                              ScenarioTestSuite testSuite =
                                  buildFrom(zeebeClient, e.getKey(), piInput);
                              return DynamicContainer.dynamicContainer(
                                  piInput.getScenarioName(),
                                  Stream.of(
                                      // TODO add back these tests
                                      //
                                      // DynamicTest.dynamicTest(
                                      //                                          "Routed
                                      // migration", testSuite::testRoutedMigration),
                                      //
                                      // DynamicTest.dynamicTest(
                                      //                                          "Ad-Hoc
                                      // Migration", testSuite::testAdHocMigration))
                                      ));
                            })));
  }
}
