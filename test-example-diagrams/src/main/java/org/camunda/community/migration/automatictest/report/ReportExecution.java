package org.camunda.community.migration.automatictest.report;

import org.camunda.community.migration.automatictest.testpkg.TestPkg;

public abstract class ReportExecution {

  private int nbSuccess = 0;
  private int nbErrors = 0;

  private long startCurrentExecution;
  private long cumulTimeExecution;

  protected TestPkg currentTestPkg;

  public void startExecution(TestPkg testPkg) {
    startCurrentExecution = System.currentTimeMillis();
    currentTestPkg = testPkg;
  }

  public void endExecution(boolean isSuccess, String message) {
    long executionTime = System.currentTimeMillis() - startCurrentExecution;
    reportEnd(isSuccess, message, executionTime);
    if (isSuccess) {
      nbSuccess++;
    } else {
      nbErrors++;
    }
  }

  /**
   * Abstract method: report a debug message
   *
   * @param message message to report
   */
  public abstract void reportDebug(String message);

  /**
   * report the end,
   *
   * @param isSuccess true if the execution is successful
   * @param message message to report
   * @param executionTime time of execution (in ms)
   */
  public abstract void reportEnd(boolean isSuccess, String message, long executionTime);
}
