package org.camunda.community.converter;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.camunda.community.converter.conversion.Conversion;

public class ConversionFactory extends AbstractFactory<List<Conversion>> {
  private static final ConversionFactory INSTANCE = new ConversionFactory();

  public static ConversionFactory getInstance() {
    return INSTANCE;
  }

  @Override
  protected List<Conversion> createInstance() {
    ServiceLoader<Conversion> serviceLoader = ServiceLoader.load(Conversion.class);
    return StreamSupport.stream(serviceLoader.spliterator(), false).collect(Collectors.toList());
  }
}
