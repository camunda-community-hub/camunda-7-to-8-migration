package org.camunda.community.migration.detector.rules;

import java.util.List;
import java.util.Map;

public record CodeMigrationReport(Map<String, CodeMigrationReportForClass> classes) {
  public record CodeMigrationReportForClass(Map<String, List<CodeMigrationReportForRule>> rules) {
    public record CodeMigrationReportForRule(String violation) {}
  }
}
