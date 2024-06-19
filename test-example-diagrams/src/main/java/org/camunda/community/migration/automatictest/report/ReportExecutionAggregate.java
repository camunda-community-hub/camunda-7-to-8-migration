package org.camunda.community.migration.automatictest.report;

import java.util.ArrayList;
import java.util.List;
import org.camunda.community.migration.automatictest.testpkg.TestPkg;

public class ReportExecutionAggregate extends ReportExecution {

  private List<ReportExecution> reportExecutionList = new ArrayList<>();

  public void addReport(ReportExecution report) {
    reportExecutionList.add(report);
  }

  @Override
  public void startExecution(TestPkg testPkg) {
    for (ReportExecution report : reportExecutionList) report.startExecution(testPkg);
    super.startExecution(testPkg);
  }

  @Override
  public void reportDebug(String message) {
    for (ReportExecution report : reportExecutionList) report.reportDebug(message);
  }

  @Override
  public void reportEnd(boolean isSuccess, String message, long executionTime) {
    for (ReportExecution report : reportExecutionList)
      report.reportEnd(isSuccess, message, executionTime);
  }
}
