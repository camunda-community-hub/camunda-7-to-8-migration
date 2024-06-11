package org.camunda.community.migration.detector.rules;

import java.util.Map;
import java.util.Set;

public record CodeMigrationReport(Map<String, CodeMigrationReportForClass> classes) {
  public record CodeMigrationReportForClass(Map<String, Set<CodeMigrationReportForRule>> rules) {
    public record CodeMigrationReportForRule(String violation) {}
  }
}
