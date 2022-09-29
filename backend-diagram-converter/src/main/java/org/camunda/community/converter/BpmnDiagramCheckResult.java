package org.camunda.community.converter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.camunda.community.converter.convertible.Convertible;

public class BpmnDiagramCheckResult {
  private final Set<BpmnElementCheckResult> results = new HashSet<>();
  private String bpmnXml;

  public String getBpmnXml() {
    return bpmnXml;
  }

  public void setBpmnXml(String bpmnXml) {
    this.bpmnXml = bpmnXml;
  }

  public Set<BpmnElementCheckResult> getResults() {
    return results;
  }

  public enum Severity {
    INFO,
    ERROR,
    TASK
  }

  public static class BpmnElementCheckResult {
    private final Map<Severity, Set<String>> messages = new HashMap<>();
    private String elementId;
    private String elementName;

    @JsonTypeInfo(use = Id.CLASS, property = "_convertibleType", include = As.EXTERNAL_PROPERTY)
    private Convertible convertible;

    public Map<Severity, Set<String>> getMessages() {
      return messages;
    }

    public Convertible getConvertible() {
      return convertible;
    }

    public void setConvertible(Convertible convertible) {
      this.convertible = convertible;
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
  }
}
