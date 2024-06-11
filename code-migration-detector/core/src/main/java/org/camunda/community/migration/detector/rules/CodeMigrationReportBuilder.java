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
  private final ArchRule archRule;
  private final EvaluationResult evaluationResult;
  private final CodeMigrationReport codeMigrationReport = new CodeMigrationReport(new HashMap<>());
  private final AtomicBoolean built = new AtomicBoolean(false);

  public CodeMigrationReportBuilder(ArchRule archRule, JavaClasses javaClasses) {
    this.archRule = archRule;
    this.evaluationResult = archRule.evaluate(javaClasses);
  }

  public CodeMigrationReport build() {
    if (!built.get()) {
      getViolationHandlers()
          .forEach(violationHandler -> evaluationResult.handleViolations(violationHandler));
      built.set(true);
    }
    return codeMigrationReport;
  }

  private Set<ViolationHandler<?>> getViolationHandlers() {
    return Set.of(
        javaClassViolationHandler(),
        javaMethodViolationHandler(),
        javaMethodCallViolationHandler());
  }

  private JavaClass getFileClass(JavaClass clazz) {
    return clazz.getEnclosingClass().map(this::getFileClass).orElse(clazz);
  }

  private ViolationHandler<JavaClass> javaClassViolationHandler() {
    return new JavaClassViolationHandler(codeMigrationReport, archRule);
  }

  private ViolationHandler<JavaMethod> javaMethodViolationHandler() {
    return new JavaMethodViolationHandler(codeMigrationReport, archRule);
  }

  private ViolationHandler<JavaMethodCall> javaMethodCallViolationHandler() {
    return new JavaMethodCallViolationHandler(codeMigrationReport, archRule);
  }
}
