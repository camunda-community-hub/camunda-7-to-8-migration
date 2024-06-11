package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchRule;

public class JavaClassViolationHandler
    extends AbstractCodeMigrationReportDecoratingViolationHandler<JavaClass> {

  public JavaClassViolationHandler(CodeMigrationReport codeMigrationReport, ArchRule archRule) {
    super(codeMigrationReport, archRule);
  }

  @Override
  protected JavaClass extractJavaClass(JavaClass violatingObject) {
    return violatingObject;
  }

  @Override
  protected Class<JavaClass> handledClass() {
    return JavaClass.class;
  }
}
