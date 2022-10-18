package org.camunda.community.converter.convertible;

public class MessageConvertible implements Convertible {
  private final ZeebeSubscription zeebeSubscription = new ZeebeSubscription();

  public ZeebeSubscription getZeebeSubscription() {
    return zeebeSubscription;
  }

  public static class ZeebeSubscription {
    private String correlationKey;

    public String getCorrelationKey() {
      return correlationKey;
    }

    public void setCorrelationKey(String correlationKey) {
      this.correlationKey = correlationKey;
    }
  }
}
