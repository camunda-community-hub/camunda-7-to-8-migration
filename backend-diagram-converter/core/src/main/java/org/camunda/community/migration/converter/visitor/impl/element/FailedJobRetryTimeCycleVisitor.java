package org.camunda.community.migration.converter.visitor.impl.element;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.RetryTimeCycleConverter;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

public class FailedJobRetryTimeCycleVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    String timecycle = context.getElement().getTextContent();
    try {
      List<String> durations = RetryTimeCycleConverter.convert(timecycle);
      context.addConversion(
          ServiceTaskConvertible.class,
          convertible -> convertible.getZeebeTaskDefinition().setRetries(durations.size()));
      return MessageFactory.failedJobRetryTimeCycle(
          context.getElement().getLocalName(),
          timecycle,
          durations.size(),
          String.join("', '", durations));
    } catch (Exception e) {
      return MessageFactory.failedJobRetryTimeCycleError(
          context.getElement().getLocalName(), timecycle);
    }
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return true;
  }

  @Override
  public String localName() {
    return "failedJobRetryTimeCycle";
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return super.canVisit(context) && isServiceTask(context);
  }

  private boolean isServiceTask(DomElementVisitorContext context) {
    AtomicReference<Convertible> ref = new AtomicReference<>();
    context.addConversion(Convertible.class, ref::set);
    return ref.get() instanceof ServiceTaskConvertible;
  }

  public static class FailedJobRetryTimeCycleRemovalVisitor extends FailedJobRetryTimeCycleVisitor {
    @Override
    protected boolean canVisit(DomElementVisitorContext context) {
      return super.canVisit(context) && isNoServiceTask(context);
    }

    private boolean isNoServiceTask(DomElementVisitorContext context) {
      AtomicReference<Convertible> ref = new AtomicReference<>();
      context.addConversion(Convertible.class, ref::set);
      return !(ref.get() instanceof ServiceTaskConvertible);
    }

    @Override
    protected Message visitCamundaElement(DomElementVisitorContext context) {
      String timecycle = context.getElement().getTextContent();
      return MessageFactory.failedJobRetryTimeCycleRemoved(
          context.getElement().getLocalName(), timecycle);
    }

    @Override
    protected Severity messageSeverity() {
      return Severity.INFO;
    }
  }
}
