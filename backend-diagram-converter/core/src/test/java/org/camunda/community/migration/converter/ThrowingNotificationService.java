package org.camunda.community.migration.converter;

public class ThrowingNotificationService implements NotificationService {
  @Override
  public void notify(Object object) {
    if (object instanceof Throwable) {
      throw new RuntimeException((Throwable) object);
    }
  }
}
