package org.camunda.community.converter;

import java.util.ServiceLoader;

public class NotificationServiceFactory extends AbstractFactory<NotificationService> {
  private static final NotificationServiceFactory INSTANCE = new NotificationServiceFactory();

  public static NotificationServiceFactory getInstance() {
    return INSTANCE;
  }

  @Override
  protected NotificationService createInstance() {
    return ServiceLoader.load(NotificationService.class).iterator().next();
  }
}
