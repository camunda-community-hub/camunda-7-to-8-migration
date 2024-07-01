package org.camunda.community.migration.detector.rules;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.camunda.community.migration.detector.rules.CodeMigrationReportPrinter.MustacheContext.RootContext;
import org.camunda.community.migration.detector.rules.CodeMigrationReportPrinter.MustacheContext.RootContext.ClassContext;
import org.camunda.community.migration.detector.rules.CodeMigrationReportPrinter.MustacheContext.RootContext.ClassContext.RuleContext;

public class CodeMigrationReportPrinter {
  private static final Template REPORT_TEMPLATE;

  static {
    try (InputStream in =
        CodeMigrationReportPrinter.class
            .getClassLoader()
            .getResourceAsStream("migrationReport.mustache")) {
      REPORT_TEMPLATE = Mustache.compiler().compile(new String(in.readAllBytes()));
    } catch (IOException e) {
      throw new RuntimeException("Error while loading report printer template", e);
    }
  }

  public static void print(Writer writer, CodeMigrationReport report) {
    REPORT_TEMPLATE.execute(transform(report), writer);
  }

  private static MustacheContext transform(CodeMigrationReport report) {
    List<RootContext> classes = new ArrayList<>();
    report
        .classes()
        .forEach(
            (className, reportForClass) -> {
              List<ClassContext> rules = new ArrayList<>();
              reportForClass
                  .rules()
                  .forEach(
                      (rule, reportsForRule) -> {
                        List<RuleContext> violations = new ArrayList<>();
                        reportsForRule.forEach(
                            reportForRule -> {
                              violations.add(new RuleContext(reportForRule.violation()));
                            });
                        rules.add(new ClassContext(rule, violations));
                      });
              classes.add(new RootContext(className, rules));
            });
    return new MustacheContext(classes);
  }

  record MustacheContext(List<RootContext> classes) {
    record RootContext(String className, List<ClassContext> rules) {
      record ClassContext(String rule, List<RuleContext> violations) {
        record RuleContext(String violation) {}
      }
    }
  }
}
