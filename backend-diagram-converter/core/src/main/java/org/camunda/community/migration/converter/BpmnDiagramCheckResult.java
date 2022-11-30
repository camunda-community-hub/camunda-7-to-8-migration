package org.camunda.community.migration.converter;

import java.util.ArrayList;
import java.util.List;

public class BpmnDiagramCheckResult {
  private final List<BpmnElementCheckResult> results = new ArrayList<>();
  private String filename;

  public List<BpmnElementCheckResult> getResults() {
    return results;
  }

  public BpmnElementCheckResult getResult(String elementId) {
    return getResults().stream()
        .filter(element -> element.getElementId().equals(elementId))
        .findFirst()
        .orElse(null);
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
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
    private String link;

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

    public String getLink() {
      return link;
    }

    public void setLink(String link) {
      this.link = link;
    }
  }
}
