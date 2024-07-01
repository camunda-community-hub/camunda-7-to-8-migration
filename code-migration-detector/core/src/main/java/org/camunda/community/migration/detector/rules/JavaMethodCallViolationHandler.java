package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.lang.ArchRule;

public class JavaMethodCallViolationHandler
    extends AbstractCodeMigrationReportDecoratingViolationHandler<JavaMethodCall> {

  public JavaMethodCallViolationHandler(
      CodeMigrationReport codeMigrationReport, ArchRule archRule) {
    super(codeMigrationReport, archRule);
  }

  @Override
  protected JavaClass extractJavaClass(JavaMethodCall violatingObject) {
    return violatingObject.getOriginOwner();
  }

  @Override
  protected Class<JavaMethodCall> handledClass() {
    return JavaMethodCall.class;
  }
}
