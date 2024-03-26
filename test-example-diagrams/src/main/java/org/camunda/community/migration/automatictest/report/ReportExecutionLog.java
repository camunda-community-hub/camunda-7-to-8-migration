package org.camunda.community.migration.automatictest.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportExecutionLog extends ReportExecution {
  static Logger logger = LoggerFactory.getLogger(ReportExecutionLog.class);

  @Override
  public void reportDebug(String message) {
    logger.info(message);
  }

  @Override
  public void reportEnd(boolean isSuccess, String message, long executionTime) {
    if (isSuccess) logger.info(message);
    else logger.error(message);
  }
}
