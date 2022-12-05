package org.camunda.community.migration.processInstance.core;

public interface ProcessConstants {

  interface JobType {
    String CAMUNDA8_CHECK_PROCESS_DEFINITION = "camunda8:checkProcessDefinition";
    String CAMUNDA7_SUSPEND = "camunda7:suspend";
    String CAMUNDA7_CONTINUE = "camunda7:continue";
    String CAMUNDA8_START = "camunda8:start";
    String CAMUNDA7_CANCEL = "camunda7:cancel";
    String CAMUNDA7_EXTRACT = "camunda7:extract";
  }
}
