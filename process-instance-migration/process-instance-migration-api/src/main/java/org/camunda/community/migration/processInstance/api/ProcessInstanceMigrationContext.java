package org.camunda.community.migration.processInstance.api;

public interface ProcessInstanceMigrationContext {
  static ProcessInstanceMigrationContext create(String processInstanceId) {
    return new ProcessInstanceMigrationContextImpl(processInstanceId);
  }

  static ProcessInstanceMigrationSuccessfulContext create(
      String processInstanceId,
      long processInstanceKey,
      String processInstanceKeyVariableName,
      boolean cancelOldInstance) {

    return new ProcessInstanceMigrationSuccessfulContextImpl(
        processInstanceId, processInstanceKey, processInstanceKeyVariableName, cancelOldInstance);
  }

  String getProcessInstanceId();

  interface ProcessInstanceMigrationSuccessfulContext extends ProcessInstanceMigrationContext {
    long getProcessInstanceKey();

    String getProcessInstanceKeyVariableName();

    boolean isCancelOldInstance();
  }

  class ProcessInstanceMigrationContextImpl implements ProcessInstanceMigrationContext {
    private final String processInstanceId;

    private ProcessInstanceMigrationContextImpl(String processInstanceId) {
      this.processInstanceId = processInstanceId;
    }

    @Override
    public String getProcessInstanceId() {
      return processInstanceId;
    }
  }

  class ProcessInstanceMigrationSuccessfulContextImpl extends ProcessInstanceMigrationContextImpl
      implements ProcessInstanceMigrationSuccessfulContext {
    private final long processInstanceKey;
    private final String processInstanceKeyVariableName;
    private final boolean cancelOldInstance;

    public ProcessInstanceMigrationSuccessfulContextImpl(
        String processInstanceId,
        long processInstanceKey,
        String processInstanceKeyVariableName,
        boolean cancelOldInstance) {
      super(processInstanceId);
      this.processInstanceKey = processInstanceKey;
      this.processInstanceKeyVariableName = processInstanceKeyVariableName;
      this.cancelOldInstance = cancelOldInstance;
    }

    @Override
    public long getProcessInstanceKey() {
      return processInstanceKey;
    }

    @Override
    public String getProcessInstanceKeyVariableName() {
      return processInstanceKeyVariableName;
    }

    @Override
    public boolean isCancelOldInstance() {
      return cancelOldInstance;
    }
  }
}
