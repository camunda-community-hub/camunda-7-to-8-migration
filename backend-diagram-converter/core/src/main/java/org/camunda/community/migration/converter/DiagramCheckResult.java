package org.camunda.community.migration.converter;

import java.util.ArrayList;
import java.util.List;

public class DiagramCheckResult {
  private final List<ElementCheckResult> results = new ArrayList<>();
  private String filename;
  private String converterVersion;

  public String getConverterVersion() {
    return converterVersion;
  }

  public void setConverterVersion(String converterVersion) {
    this.converterVersion = converterVersion;
  }

  public List<ElementCheckResult> getResults() {
    return results;
  }

  public ElementCheckResult getResult(String elementId) {
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

  @Override
  public String toString() {
    return "DiagramCheckResult{" + "results=" + results + ", filename='" + filename + '\'' + '}';
  }

  public enum Severity {
    WARNING,
    TASK,
    REVIEW,
    INFO,
  }

  public static class ElementCheckResult {
    private final List<ElementCheckMessage> messages = new ArrayList<>();
    private final List<String> references = new ArrayList<>();
    private final List<String> referencedBy = new ArrayList<>();
    private String elementId;
    private String elementName;
    private String elementType;

    public List<ElementCheckMessage> getMessages() {
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

    public List<String> getReferences() {
      return references;
    }

    public List<String> getReferencedBy() {
      return referencedBy;
    }

    @Override
    public String toString() {
      return "ElementCheckResult{"
          + "messages="
          + messages
          + ", references="
          + references
          + ", referencedBy="
          + referencedBy
          + ", elementId='"
          + elementId
          + '\''
          + ", elementName='"
          + elementName
          + '\''
          + ", elementType='"
          + elementType
          + '\''
          + '}';
    }
  }

  public static class ElementCheckMessage {
    private Severity severity;
    private String message;
    private String link;
    private String id;

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

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return "ElementCheckMessage{"
          + "severity="
          + severity
          + ", message='"
          + message
          + '\''
          + ", link='"
          + link
          + '\''
          + ", id='"
          + id
          + '\''
          + '}';
    }
  }
}
