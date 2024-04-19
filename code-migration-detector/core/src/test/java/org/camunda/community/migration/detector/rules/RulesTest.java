package org.camunda.community.migration.detector.rules;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.EvaluationResult;
import java.util.HashMap;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.event.EventListener;

public class RulesTest {

  private static JavaClasses classes(Class<?>... classes) {
    return new ClassFileImporter().importClasses(classes);
  }

  private static CodeMigrationReport reportFrom(EvaluationResult result) {
    CodeMigrationReport report = new CodeMigrationReport(new HashMap<>());
    result.handleViolations(new ReportPrinter(report));
    return report;
  }

  @Test
  void shouldDetectExecutionListeners() {
    EvaluationResult result =
        Camunda7MigrationRules.ensureNoExecutionListener()
            .evaluate(new ClassFileImporter().importClasses(MyExecutionListener.class));
    assertThat(result.hasViolation()).isTrue();
    assertThat(result.getFailureReport().getDetails()).hasSize(1);
    assertThat(result.getFailureReport().getDetails().get(0))
        .contains(
            "org.camunda.community.migration.detector.rules.RulesTest$MyExecutionListener",
            "org.camunda.bpm.engine.delegate.ExecutionListener");
  }

  @Test
  void shouldDetectTaskListeners() {
    EvaluationResult result =
        Camunda7MigrationRules.ensureNoTaskListener()
            .evaluate(new ClassFileImporter().importClasses(MyTaskListener.class));
    assertThat(result.hasViolation()).isTrue();
    assertThat(result.getFailureReport().getDetails()).hasSize(1);
    assertThat(result.getFailureReport().getDetails().get(0))
        .contains(
            "org.camunda.community.migration.detector.rules.RulesTest$MyTaskListener",
            "org.camunda.bpm.engine.delegate.TaskListener");
  }

  @Test
  void shouldDetectProcessEnginePlugins() {
    EvaluationResult result =
        Camunda7MigrationRules.ensureNoProcessEnginePlugin()
            .evaluate(new ClassFileImporter().importClasses(MyProcessEnginePlugin.class));
    assertThat(result.hasViolation()).isTrue();
    assertThat(result.getFailureReport().getDetails()).hasSize(1);
    assertThat(result.getFailureReport().getDetails().get(0))
        .contains(
            "org.camunda.community.migration.detector.rules.RulesTest$MyProcessEnginePlugin",
            "org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin");
  }

  @Test
  void shouldDetectProcessEngine() {
    EvaluationResult result =
        Camunda7MigrationRules.ensureNoInvocationOfProcessEngine()
            .evaluate(new ClassFileImporter().importClasses(MyDelegate.class));
    assertThat(result.hasViolation()).isTrue();
    assertThat(result.getFailureReport().getDetails()).hasSize(1);
    assertThat(result.getFailureReport().getDetails().get(0))
        .contains(
            "org.camunda.community.migration.detector.rules.RulesTest$MyDelegate",
            "org.camunda.bpm.engine.ProcessEngine");
  }

  @Test
  void shouldDetectSpringBootEvents() {
    EvaluationResult result =
        Camunda7MigrationRules.ensureNoSpringBootEvents()
            .evaluate(new ClassFileImporter().importClasses(MySpringEventSubscriber.class));
    assertThat(result.hasViolation()).isTrue();
    assertThat(result.getFailureReport().getDetails()).hasSize(1);
    assertThat(result.getFailureReport().getDetails().get(0))
        .contains(
            "org.camunda.community.migration.detector.rules.RulesTest$MyProcessEnginePlugin",
            "org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin");
  }

  @Test
  void shouldDetectRepositoryServiceInvocation() throws JsonProcessingException {
    EvaluationResult result =
        Camunda7MigrationRules.ensureNoInvocationOfRepositoryService()
            .evaluate(classes(MyProcessEngineServicesBean.class));
    CodeMigrationReport report = new CodeMigrationReport(new HashMap<>());
    result.handleViolations(new ReportPrinter(report));
    String reportString =
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(report);
    System.out.println(reportString);
    assertThat(result.hasViolation()).isTrue();
    assertThat(result.getFailureReport().getDetails()).hasSize(1);
    assertThat(result.getFailureReport().getDetails().get(0))
        .contains(
            "org.camunda.community.migration.detector.rules.RulesTest$MyProcessEnginePlugin",
            "org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin");
  }

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
