package org.camunda.community.migration.converter.convertible;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractExecutionListenerConvertible
    extends AbstractProcessElementConvertible {
  private List<ZeebeExecutionListener> zeebeExecutionListeners = new ArrayList<>();

  public List<ZeebeExecutionListener> getZeebeExecutionListeners() {
    return zeebeExecutionListeners;
  }

  public void setZeebeExecutionListeners(List<ZeebeExecutionListener> zeebeExecutionListeners) {
    this.zeebeExecutionListeners = zeebeExecutionListeners;
  }

  public void addZeebeExecutionListener(ZeebeExecutionListener zeebeExecutionListener) {
    zeebeExecutionListeners.add(zeebeExecutionListener);
  }

  public static class ZeebeExecutionListener {
    private String listenerType;
    private String retries;
    private EventType eventType;

    public String getListenerType() {
      return listenerType;
    }

    public void setListenerType(String listenerType) {
      this.listenerType = listenerType;
    }

    public String getRetries() {
      return retries;
    }

    public void setRetries(String retries) {
      this.retries = retries;
    }

    public EventType getEventType() {
      return eventType;
    }

    public void setEventType(EventType eventType) {
      this.eventType = eventType;
    }

    public enum EventType {
      start,
      end
    }
  }
}
