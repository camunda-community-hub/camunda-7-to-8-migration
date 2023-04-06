package org.camunda.community.migration.automatictest.report;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportExecutionSynthesis extends ReportExecution {
  static Logger logger = LoggerFactory.getLogger(ReportExecutionLog.class);

  private final List<String> reportLine = new ArrayList<>();

  @Override
  public void reportDebug(String message) {
    logger.info(message);
  }

  @Override
  public void reportEnd(boolean isSuccess, String message, long executionTime) {

    String messageLine = "| " + completeBlank(currentTestPkg.getName(), 40, false);
    messageLine += " | " + completeBlank(isSuccess ? " CORRECT " : " FAIL ", 9, false);
    messageLine += " | " + completeBlank(executionTime + " ms", 10, true);
    messageLine += " | " + completeBlank(message, 300, false) + " |";
    reportLine.add(messageLine);
  }

  private String completeBlank(String info, int lengthExpected, boolean before) {
    if (before) {
      while (info.length() < lengthExpected) {
        info = "                   " + info;
      }
      return info.substring(info.length() - lengthExpected);
    } else {
      while (info.length() < lengthExpected) {
        info += "                   ";
      }

      return info.substring(0, lengthExpected);
    }
  }

  public void logSynthesis() {
    for (String line : reportLine) {
      logger.info(line);
    }
  }
}
