package org.camunda.community.migration.processInstance;

public interface ProcessConstants {
  interface BpmnProcessId {
    String ROUTER_PROCESS = "RoutedProcessInstanceMigrationProcess";
  }

  interface ErrorCode {
    String CANCEL_PROCESS_INSTANCE = "camunda7:process-instance:cancel:error";
  }

  interface JobType {
    String CAMUNDA8_CHECK_PROCESS_DEFINITION = "camunda8:process-definition:check";
    String CAMUNDA7_SUSPEND = "camunda7:process-definition:suspend";
    String CAMUNDA7_SUSPEND_JOB = "camunda7:job-definition:suspend";
    String CAMUNDA7_CONTINUE = "camunda7:process-definition:continue";
    String CAMUNDA7_CONTINUE_JOB = "camunda7:job-definition:continue";
    String CAMUNDA8_START = "camunda8:process-instance:create";
    String CAMUNDA8_CANCEL = "camunda8:process-instance:cancel";
    String CAMUNDA7_CANCEL = "camunda7:process-instance:cancel";
    String CAMUNDA7_EXTRACT = "camunda7:process-instance:get";
    String CAMUNDA7_VERSIONED_INFORMATION = "camunda7:process-definition:get";
    String CAMUNDA7_QUERY_ROUTABLE_INSTANCES = "camunda7:process-instance:query:routable";
  }

  interface Message {
    String START = "migration.camunda:start";
  }

  interface FormKey {
    String SELECT_PROCESS_INSTANCES = "select-process-instances";
    String CREATE_AND_DEPLOY_CONVERSION = "create-and-deploy-conversion";
    String SELECT_JOB_DEFINITION = "select-job-definition";
    String CANCEL_ROUTE_EXECUTION = "cancel-route-execution";
  }
}
