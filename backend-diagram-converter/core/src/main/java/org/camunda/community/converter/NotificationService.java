package org.camunda.community.converter;

public interface NotificationService {
  void notify(Object object);

  class NoopNotificationService implements NotificationService {

    @Override
    public void notify(Object object) {
      // just do nothing
    }
  }
}
