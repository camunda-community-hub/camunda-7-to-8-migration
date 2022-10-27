package org.camunda.community.converter;

public class BpmnConverterFactory extends AbstractFactory<BpmnConverter> {
  private static final BpmnConverterFactory INSTANCE = new BpmnConverterFactory();

  public static BpmnConverterFactory getInstance() {
    return INSTANCE;
  }

  @Override
  protected BpmnConverter createInstance() {
    return new BpmnConverter(
        DomElementVisitorFactory.getInstance().get(), ConversionFactory.getInstance().get());
  }
}
