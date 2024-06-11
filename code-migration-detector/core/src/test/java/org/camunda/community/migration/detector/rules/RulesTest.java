package org.camunda.community.migration.detector.rules;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.io.IOException;
import java.util.stream.Stream;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.context.event.EventListener;

public class RulesTest {

  private static JavaClasses classes(Class<?>... classes) {
    return new ClassFileImporter().importClasses(classes);
  }

  @TestFactory
  Stream<DynamicTest> rules() {
    return Stream.of(
            new DynamicTestInput(
                "should detect execution listener implementation",
                Camunda7MigrationRules.ensureNoExecutionListener(),
                classes(MyExecutionListener.class),
                RulesTest.class,
                "no classes should implement " + ExecutionListener.class.getName(),
                MyExecutionListener.class.getName(),
                ExecutionListener.class.getName()),
            new DynamicTestInput(
                "should detect task listener implementation",
                Camunda7MigrationRules.ensureNoTaskListener(),
                classes(MyTaskListener.class),
                RulesTest.class,
                "no classes should implement " + TaskListener.class.getName(),
                MyTaskListener.class.getName(),
                TaskListener.class.getName()),
            new DynamicTestInput(
                "should detect process engine plugin implementation",
                Camunda7MigrationRules.ensureNoProcessEnginePlugin(),
                classes(MyProcessEnginePlugin.class),
                RulesTest.class,
                "no classes should implement " + ProcessEnginePlugin.class.getName(),
                MyProcessEnginePlugin.class.getName(),
                ProcessEnginePlugin.class.getName()),
            new DynamicTestInput(
                "should detect process engine method invocation",
                Camunda7MigrationRules.ensureNoInvocationOfProcessEngine(),
                classes(MyDelegate.class),
                RulesTest.class,
                "no classes should call method where target owner assignable to "
                    + ProcessEngine.class.getName(),
                MyDelegate.class.getName(),
                ProcessEngine.class.getName()),
            new DynamicTestInput(
                "should detect spring boot event handlers",
                Camunda7MigrationRules.ensureNoSpringBootEvents(),
                classes(MySpringEventSubscriber.class),
                RulesTest.class,
                "no methods that is spring event listener should have camunda bpm parameter types",
                TaskEvent.class.getName()),
            new DynamicTestInput(
                "should detect repository service invocation",
                Camunda7MigrationRules.ensureNoInvocationOfRepositoryService(),
                classes(MyProcessEngineServicesBean.class),
                RulesTest.class,
                "no classes should call method where target owner assignable to org.camunda.bpm.engine.RepositoryService",
                "org.camunda.community.migration.detector.rules.RulesTest$MyProcessEngineServicesBean.doSomethingWithTheServices()",
                "org.camunda.bpm.engine.RepositoryService.createDeployment()"))
        .map(
            dti ->
                DynamicTest.dynamicTest(
                    dti.displayName,
                    () ->
                        testCodeMigrationReport(
                            dti.archRule(),
                            dti.classes(),
                            dti.expectedClass(),
                            dti.expectedRule(),
                            dti.expectedViolationContains())));
  }

  private void testCodeMigrationReport(
      ArchRule archRule,
      JavaClasses classes,
      Class<?> expectedClass,
      String expectedRule,
      String... expectedViolationContains) {
    CodeMigrationReport report = new CodeMigrationReportBuilder(archRule, classes).build();
    try {
      String string =
          new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(report);
      System.out.println(string);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    assertThat(report.classes().isEmpty()).isFalse();
    assertThat(report.classes()).containsKey(expectedClass.getName());
    assertThat(report.classes().get(RulesTest.class.getName()).rules()).containsKey(expectedRule);
    assertThat(report.classes().get(expectedClass.getName()).rules().get(expectedRule)).hasSize(1);
    assertThat(
            report
                .classes()
                .get(expectedClass.getName())
                .rules()
                .get(expectedRule)
                .iterator()
                .next()
                .violation())
        .contains(expectedViolationContains);
  }

  private record DynamicTestInput(
      String displayName,
      ArchRule archRule,
      JavaClasses classes,
      Class<?> expectedClass,
      String expectedRule,
      String... expectedViolationContains) {}

  static class MyExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
      // do nothing
    }
  }

  static class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
      // do nothing
    }
  }

  static class MyDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
      delegateExecution.getProcessEngine().getRuntimeService().startProcessInstanceByKey("process");
    }
  }

  static class MySpringEventSubscriber {
    @EventListener
    public void handleTask(TaskEvent taskEvent) {
      // do nothing
    }
  }

  static class MyProcessEnginePlugin extends AbstractProcessEnginePlugin {}

  static class MyProcessEngineServicesBean {
    RepositoryService repositoryService;

    public void doSomethingWithTheServices() {
      repositoryService.createDeployment();
    }
  }
}
