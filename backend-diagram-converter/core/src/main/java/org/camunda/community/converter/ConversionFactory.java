package org.camunda.community.converter;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.camunda.community.converter.conversion.Conversion;

public class ConversionFactory {
  private static final ConversionFactory INSTANCE = new ConversionFactory();

  public static ConversionFactory getInstance() {
    return INSTANCE;
  }

  public Set<Conversion> get() {
    ServiceLoader<Conversion> serviceLoader = ServiceLoader.load(Conversion.class);
    return StreamSupport.stream(serviceLoader.spliterator(), false).collect(Collectors.toSet());
  }
}
