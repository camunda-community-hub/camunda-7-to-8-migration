package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchRule;

public class JavaMethodViolationHandler
    extends AbstractCodeMigrationReportDecoratingViolationHandler<JavaMethod> {

  public JavaMethodViolationHandler(CodeMigrationReport codeMigrationReport, ArchRule archRule) {
    super(codeMigrationReport, archRule);
  }

  @Override
  protected JavaClass extractJavaClass(JavaMethod violatingObject) {
    return violatingObject.getOwner();
  }

  @Override
  protected Class<JavaMethod> handledClass() {
    return JavaMethod.class;
  }
}
