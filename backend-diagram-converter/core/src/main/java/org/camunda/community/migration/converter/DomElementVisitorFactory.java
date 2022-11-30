package org.camunda.community.migration.converter;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.camunda.community.migration.converter.visitor.DomElementVisitor;

public class DomElementVisitorFactory extends AbstractFactory<List<DomElementVisitor>> {
  private static final DomElementVisitorFactory INSTANCE = new DomElementVisitorFactory();

  public static DomElementVisitorFactory getInstance() {
    return INSTANCE;
  }

  @Override
  protected List<DomElementVisitor> createInstance() {
    ServiceLoader<DomElementVisitor> serviceLoader = ServiceLoader.load(DomElementVisitor.class);
    return StreamSupport.stream(serviceLoader.spliterator(), false).collect(Collectors.toList());
  }
}
