package org.camunda.community.converter.cli;

import org.camunda.community.converter.NotificationService;

public class PrintNotificationServiceImpl implements NotificationService {
  @Override
  public void notify(Object object) {
    if (object instanceof Exception) {
      System.err.println(((Exception) object).getMessage());
    } else {
      System.out.println(object);
    }
  }
}
