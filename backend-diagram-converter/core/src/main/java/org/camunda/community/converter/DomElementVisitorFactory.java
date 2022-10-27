package org.camunda.community.converter;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.camunda.community.converter.visitor.DomElementVisitor;

public class DomElementVisitorFactory extends AbstractFactory<Set<DomElementVisitor>> {
  private static final DomElementVisitorFactory INSTANCE = new DomElementVisitorFactory();

  public static DomElementVisitorFactory getInstance() {
    return INSTANCE;
  }

  @Override
  protected Set<DomElementVisitor> createInstance() {
    ServiceLoader<DomElementVisitor> serviceLoader = ServiceLoader.load(DomElementVisitor.class);
    return StreamSupport.stream(serviceLoader.spliterator(), false).collect(Collectors.toSet());
  }
}
