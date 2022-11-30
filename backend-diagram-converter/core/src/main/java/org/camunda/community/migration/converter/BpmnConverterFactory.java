package org.camunda.community.migration.converter;

public class BpmnConverterFactory extends AbstractFactory<BpmnConverter> {
  private static final BpmnConverterFactory INSTANCE = new BpmnConverterFactory();
  private final DomElementVisitorFactory domElementVisitorFactory =
      DomElementVisitorFactory.getInstance();
  private final ConversionFactory conversionFactory = ConversionFactory.getInstance();
  private final NotificationServiceFactory notificationServiceFactory =
      NotificationServiceFactory.getInstance();

  public static BpmnConverterFactory getInstance() {
    return INSTANCE;
  }

  @Override
  protected BpmnConverter createInstance() {
    return new BpmnConverter(
        domElementVisitorFactory.get(), conversionFactory.get(), notificationServiceFactory.get());
  }

  public DomElementVisitorFactory getDomElementVisitorFactory() {
    return domElementVisitorFactory;
  }

  public ConversionFactory getConversionFactory() {
    return conversionFactory;
  }

  public NotificationServiceFactory getNotificationServiceFactory() {
    return notificationServiceFactory;
  }
}
