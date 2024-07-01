package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ViolationHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.camunda.community.migration.detector.rules.CodeMigrationReport.CodeMigrationReportForClass;
import org.camunda.community.migration.detector.rules.CodeMigrationReport.CodeMigrationReportForClass.CodeMigrationReportForRule;

public abstract class AbstractCodeMigrationReportDecoratingViolationHandler<T>
    implements ViolationHandler<T> {
  private final CodeMigrationReport codeMigrationReport;
  private final ArchRule archRule;

  public AbstractCodeMigrationReportDecoratingViolationHandler(
      CodeMigrationReport codeMigrationReport, ArchRule archRule) {
    this.codeMigrationReport = codeMigrationReport;
    this.archRule = archRule;
  }

  private JavaClass getFileClass(JavaClass clazz) {
    return clazz.getEnclosingClass().map(this::getFileClass).orElse(clazz);
  }

  @Override
  public void handle(Collection<T> violatingObjects, String message) {
    violatingObjects.forEach(
        violatingObject -> {
          if (handledClass().isAssignableFrom(violatingObject.getClass())) {
            CodeMigrationReportForClass codeMigrationReportForClass =
                codeMigrationReport
                    .classes()
                    .computeIfAbsent(
                        getFileClass(extractJavaClass(violatingObject)).getFullName(),
                        c -> new CodeMigrationReportForClass(new HashMap<>()));
            List<CodeMigrationReportForRule> codeMigrationReportForRules =
                codeMigrationReportForClass
                    .rules()
                    .computeIfAbsent(archRule.getDescription(), c -> new ArrayList<>());
            codeMigrationReportForRules.add(new CodeMigrationReportForRule(message));
          }
        });
  }

  protected abstract JavaClass extractJavaClass(T violatingObject);

  protected abstract Class<T> handledClass();
}
