package org.camunda.community.migration.adapter.execution.variable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SingleVariableTypingRule implements VariableTypingRule {
  private static final Logger LOG = LoggerFactory.getLogger(SingleVariableTypingRule.class);
  private final ObjectMapper objectMapper;
  private final Class<?> targetType;

  protected SingleVariableTypingRule(ObjectMapper objectMapper, Class<?> targetType) {
    this.objectMapper = objectMapper;
    this.targetType = targetType;
  }

  @Override
  public final void handle(VariableTypingContext context) {
    if (bpmnProcessIdMatches(context) && variableNameMatches(context)) {
      LOG.debug(
          "Converting variable {} of process {} from {} to {}",
          context.getVariableName(),
          context.getBpmnProcessId(),
          context.getVariableValue().getClass(),
          targetType);
      Object newVariableValue = objectMapper.convertValue(context.getVariableValue(), targetType);
      context.setVariableValue(newVariableValue);
    }
  }

  private boolean bpmnProcessIdMatches(VariableTypingContext context) {
    return getBpmnProcessId() == null || getBpmnProcessId().equals(context.getBpmnProcessId());
  }

  private boolean variableNameMatches(VariableTypingContext context) {
    return getVariableName() == null || getVariableName().equals(context.getVariableName());
  }

  protected abstract String getBpmnProcessId();

  protected abstract String getVariableName();

  public static class SimpleVariableTypingRule extends SingleVariableTypingRule {
    private final String bpmnProcessId;
    private final String variableName;

    public SimpleVariableTypingRule(
        String bpmnProcessId, String variableName, ObjectMapper objectMapper, Class<?> targetType) {
      super(objectMapper, targetType);
      this.bpmnProcessId = bpmnProcessId;
      this.variableName = variableName;
    }

    @Override
    protected String getBpmnProcessId() {
      return bpmnProcessId;
    }

    @Override
    protected String getVariableName() {
      return variableName;
    }
  }
}
