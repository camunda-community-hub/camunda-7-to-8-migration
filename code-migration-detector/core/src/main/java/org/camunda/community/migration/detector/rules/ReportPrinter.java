package org.camunda.community.migration.detector.rules;

import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.lang.ViolationHandler;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.camunda.community.migration.detector.rules.CodeMigrationReport.CodeMigrationReportForClass;
import org.camunda.community.migration.detector.rules.CodeMigrationReport.CodeMigrationReportForClass.CodeMigrationReportForRule;

public class ReportPrinter implements ViolationHandler<Object> {
  private final CodeMigrationReport report;
  private final Map<Class<?>, DetailsExtractor<?>> detailsExtractors = new HashMap<>();

  public ReportPrinter(CodeMigrationReport report) {
    this.report = report;
    detailsExtractors.put(
        JavaMethodCall.class,
        new DetailsExtractor<JavaMethodCall>() {

          @Override
          public Class<?> extractClass(JavaMethodCall violatingObject) {
            return violatingObject.getOriginOwner().reflect();
          }

          @Override
          public CodeMigrationReportForRule extractReport(JavaMethodCall violatingObject) {
            return new CodeMigrationReportForRule(
                String.format(
                    "Method '%s' invoking '%s'",
                    violatingObject.getOrigin().getFullName(),
                    violatingObject.getTarget().getFullName()),
                violatingObject.getOrigin().getSourceCodeLocation().toString());
          }
        });
  }

  public CodeMigrationReport getReport() {
    return report;
  }

  @Override
  public void handle(Collection<Object> violatingObjects, String message) {
    for (Object violatingObject : violatingObjects) {
      Class<?> clazz = extractClass(violatingObject);
      CodeMigrationReportForClass codeMigrationReportForClass =
          report
              .classes()
              .computeIfAbsent(clazz, c -> new CodeMigrationReportForClass(new HashMap<>()));
      Set<CodeMigrationReportForRule> codeMigrationReportForRules =
          codeMigrationReportForClass.rules().computeIfAbsent(message, c -> new HashSet<>());
      codeMigrationReportForRules.add(extractReport(violatingObject));
    }
  }

  private Class<?> extractClass(Object violatingObject) {
    return findExtractor(violatingObject).extractClass(violatingObject);
  }

  private CodeMigrationReportForRule extractReport(Object violatingObject) {
    return findExtractor(violatingObject).extractReport(violatingObject);
  }

  private DetailsExtractor<Object> findExtractor(Object violatingObject) {
    DetailsExtractor<?> detailsExtractor = detailsExtractors.get(violatingObject.getClass());
    if (detailsExtractor == null) {
      throw new RuntimeException("No details extractor for class " + violatingObject.getClass());
    }
    return (DetailsExtractor<Object>) detailsExtractor;
  }

  interface DetailsExtractor<T> {
    Class<?> extractClass(T violatingObject);

    CodeMigrationReportForRule extractReport(T violatingObject);
  }
}
