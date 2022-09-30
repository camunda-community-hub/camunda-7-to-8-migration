package org.camunda.community.converter.convertible;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDataMapperConvertible extends AbstractFlownodeConvertible {
  private Set<ZeebeIoMapping> zeebeIoMappings;
  private Set<ZeebeTaskHeader> zeebeTaskHeaders;

  public Set<ZeebeIoMapping> getZeebeIoMappings() {
    return zeebeIoMappings;
  }

  public void setZeebeIoMappings(Set<ZeebeIoMapping> zeebeIoMappings) {
    this.zeebeIoMappings = zeebeIoMappings;
  }

  public Set<ZeebeTaskHeader> getZeebeTaskHeaders() {
    return zeebeTaskHeaders;
  }

  public void setZeebeTaskHeaders(Set<ZeebeTaskHeader> zeebeTaskHeaders) {
    this.zeebeTaskHeaders = zeebeTaskHeaders;
  }

  public void addZeebeIoMapping(MappingDirection direction, String source, String target) {
    ZeebeIoMapping mapping = new ZeebeIoMapping();
    mapping.setDirection(direction);
    mapping.setSource(source);
    mapping.setTarget(target);
    if (zeebeIoMappings == null) {
      zeebeIoMappings = new HashSet<>();
    }
    zeebeIoMappings.add(mapping);
  }

  public void addZeebeTaskHeader(String key, String value) {
    ZeebeTaskHeader header = new ZeebeTaskHeader();
    header.setKey(key);
    header.setValue(value);
    if (zeebeTaskHeaders == null) {
      zeebeTaskHeaders = new HashSet<>();
    }
    zeebeTaskHeaders.add(header);
  }

  public enum MappingDirection {
    INPUT,
    OUTPUT
  }

  public static class ZeebeIoMapping {
    private MappingDirection direction;
    private String source;
    private String target;

    public MappingDirection getDirection() {
      return direction;
    }

    public void setDirection(MappingDirection direction) {
      this.direction = direction;
    }

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getTarget() {
      return target;
    }

    public void setTarget(String target) {
      this.target = target;
    }
  }

  public static class ZeebeTaskHeader {
    private String key;
    private String value;

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
