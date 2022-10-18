package org.camunda.community.converter;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.camunda.community.converter.visitor.DomElementVisitor;

public class DomElementVisitorFactory {
  private static final DomElementVisitorFactory INSTANCE = new DomElementVisitorFactory();

  public static DomElementVisitorFactory getInstance() {
    return INSTANCE;
  }

  public Set<DomElementVisitor> get() {
    ServiceLoader<DomElementVisitor> serviceLoader = ServiceLoader.load(DomElementVisitor.class);
    return StreamSupport.stream(serviceLoader.spliterator(), false).collect(Collectors.toSet());
  }
}
