package org.camunda.community.converter;

import java.util.ArrayList;
import java.util.List;

public class BpmnDiagramCheckResult {
  private final List<BpmnElementCheckResult> results = new ArrayList<>();

  public List<BpmnElementCheckResult> getResults() {
    return results;
  }

  public enum Severity {
    WARNING,
    TASK,
    INFO,
  }

  public static class BpmnElementCheckResult {
    private final List<BpmnElementCheckMessage> messages = new ArrayList<>();
    private String elementId;
    private String elementName;
    private String elementType;

    public List<BpmnElementCheckMessage> getMessages() {
      return messages;
    }

    public String getElementId() {
      return elementId;
    }

    public void setElementId(String elementId) {
      this.elementId = elementId;
    }

    public String getElementName() {
      return elementName;
    }

    public void setElementName(String elementName) {
      this.elementName = elementName;
    }

    public String getElementType() {
      return elementType;
    }

    public void setElementType(String elementType) {
      this.elementType = elementType;
    }
  }

  public static class BpmnElementCheckMessage {
    private Severity severity;
    private String message;

    public Severity getSeverity() {
      return severity;
    }

    public void setSeverity(Severity severity) {
      this.severity = severity;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
}
