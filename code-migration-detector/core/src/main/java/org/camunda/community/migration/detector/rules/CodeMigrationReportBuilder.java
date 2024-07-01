package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.ViolationHandler;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class CodeMigrationReportBuilder {
  private final JavaClasses javaClasses;
  private final CodeMigrationReport codeMigrationReport = new CodeMigrationReport(new HashMap<>());
  private final AtomicBoolean built = new AtomicBoolean(false);

  public CodeMigrationReportBuilder(JavaClasses javaClasses) {
    this.javaClasses = javaClasses;
  }

  public CodeMigrationReportBuilder withArchRule(ArchRule archRule) {
    EvaluationResult evaluationResult = archRule.evaluate(javaClasses);
    getViolationHandlers(archRule)
        .forEach(violationHandler -> evaluationResult.handleViolations(violationHandler));
    return this;
  }

  public CodeMigrationReport build() {
    return codeMigrationReport;
  }

  private Set<ViolationHandler<?>> getViolationHandlers(ArchRule archRule) {
    return Set.of(
        javaClassViolationHandler(archRule),
        javaMethodViolationHandler(archRule),
        javaMethodCallViolationHandler(archRule));
  }

  private ViolationHandler<JavaClass> javaClassViolationHandler(ArchRule archRule) {
    return new JavaClassViolationHandler(codeMigrationReport, archRule);
  }

  private ViolationHandler<JavaMethod> javaMethodViolationHandler(ArchRule archRule) {
    return new JavaMethodViolationHandler(codeMigrationReport, archRule);
  }

  private ViolationHandler<JavaMethodCall> javaMethodCallViolationHandler(ArchRule archRule) {
    return new JavaMethodCallViolationHandler(codeMigrationReport, archRule);
  }
}
