package org.camunda.community.migration.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.message.EmptyMessage;
import org.camunda.community.migration.converter.message.Message;

public interface DomElementVisitorContext {
  /**
   * Returns the currently handled element
   *
   * @return the currently handled element
   */
  DomElement getElement();

  /** Sets the currently handled element to remove */
  void addElementToRemove();

  /**
   * Sets the attribute on the currently handled element to remove
   *
   * @param attributeLocalName the local name of the attribute
   * @param namespaceUri the namespace uri of the attribute
   */
  void addAttributeToRemove(String attributeLocalName, String namespaceUri);

  /**
   * Adds a message to the BPMN process element of the currently handled element
   *
   * @param message the message to display
   */
  void addMessage(Message message);

  /**
   * Sets the currently handled element as BPMN process element. This element can now hold messages
   * and ${@link Convertible} objects in its context
   */
  void setAsBpmnProcessElement(Convertible convertible);

  /**
   * Adds a conversion to the BPMN process element of the currently handled element
   *
   * @param convertibleType type of the convertible (might be abstract)
   * @param operation operation to apply to the type
   * @param <T> type of the convertible (controlled by convertibleType)
   */
  <T extends Convertible> void addConversion(Class<T> convertibleType, Consumer<T> operation);

  /**
   * Notifies external units of something that happened. This will not appear in the result.
   *
   * @param object the object to notify about
   */
  void notify(Object object);

  /**
   * Returns the converter properties for the current conversion.
   *
   * @return the converter properties for the current conversion
   */
  ConverterProperties getProperties();

  /**
   * Registers a reference from the current context element to another context element
   *
   * @param referencedElementId the referenced id
   */
  void references(String referencedElementId);

  class DefaultDomElementVisitorContext implements DomElementVisitorContext {
    private final DomElement element;
    private final BpmnDiagramCheckContext context;
    private final BpmnDiagramCheckResult result;
    private final NotificationService notificationService;
    private final ConverterProperties converterProperties;

    public DefaultDomElementVisitorContext(
        DomElement element,
        BpmnDiagramCheckContext context,
        BpmnDiagramCheckResult result,
        NotificationService notificationService,
        ConverterProperties converterProperties) {
      this.element = element;
      this.context = context;
      this.result = result;
      this.notificationService = notificationService;
      this.converterProperties = converterProperties;
    }

    @Override
    public DomElement getElement() {
      return element;
    }

    @Override
    public void addElementToRemove() {
      context.getElementsToRemove().add(element);
    }

    @Override
    public void addAttributeToRemove(String attributeLocalName, String namespaceUri) {
      context.addAttributeToRemove(element, namespaceUri, attributeLocalName);
    }

    @Override
    public void addMessage(Message message) {
      addMessage(element, message);
    }

    @Override
    public void setAsBpmnProcessElement(Convertible convertible) {
      createBpmnElementCheckResult(element, convertible);
    }

    @Override
    public <T extends Convertible> void addConversion(
        Class<T> convertibleType, Consumer<T> operation) {
      addConversion(element, convertibleType, operation);
    }

    @Override
    public void notify(Object object) {
      notificationService.notify(object);
    }

    @Override
    public ConverterProperties getProperties() {
      return converterProperties;
    }

    @Override
    public void references(String referencedElementId) {
      references(element, referencedElementId);
    }

    private void references(DomElement element, String referencedElementId) {
      BpmnElementCheckResult currentElementCheckResult = findBpmnElementCheckResult(element);
      BpmnElementCheckResult referencedResult = result.getResult(referencedElementId);
      if (referencedResult != null) {
        createReference(currentElementCheckResult, referencedResult);
      } else {
        context
            .getReferencesToCreate()
            .computeIfAbsent(referencedElementId, s -> new ArrayList<>())
            .add(currentElementCheckResult);
      }
    }

    private void createReference(
        BpmnElementCheckResult references, BpmnElementCheckResult referencedBy) {
      references.getReferences().add(referencedBy.getElementId());
      referencedBy.getReferencedBy().add(references.getElementId());
    }

    private void addMessage(DomElement element, Message message) {
      if (!(message instanceof EmptyMessage)) {
        findElementMessages(element).add(createMessage(message));
      }
    }

    private BpmnElementCheckMessage createMessage(Message message) {
      BpmnElementCheckMessage m = new BpmnElementCheckMessage();
      m.setMessage(message.getMessage());
      m.setSeverity(message.getSeverity());
      m.setLink(message.getLink());
      m.setId(message.getId());
      return m;
    }

    private List<BpmnElementCheckMessage> findElementMessages(DomElement element) {
      return findBpmnElementCheckResult(element).getMessages();
    }

    private <T extends Convertible> void addConversion(
        DomElement element, Class<T> type, Consumer<T> modifier) {
      T conversion = findConvertible(element, type);
      modifier.accept(conversion);
    }

    private <T extends Convertible> T findConvertible(DomElement element, Class<T> type) {
      if (context.getConvertibles().containsKey(element)) {
        return type.cast(context.getConvertibles().get(element));
      } else {
        return findConvertible(element.getParentElement(), type);
      }
    }

    private BpmnElementCheckResult findBpmnElementCheckResult(DomElement element) {
      return result.getResults().stream()
          .filter(r -> r.getElementId().equals(extractId(element)))
          .findFirst()
          .orElseGet(() -> findBpmnElementCheckResult(element.getParentElement()));
    }

    private String extractId(DomElement element) {
      return element.getAttribute("id");
    }

    private void createBpmnElementCheckResult(DomElement element, Convertible convertible) {
      String id = extractId(element);
      Objects.requireNonNull(id);
      if (containsId(id)) {
        throw new IllegalStateException(
            "Element with id '" + id + "' is already contained in list of results");
      }
      BpmnElementCheckResult result = new BpmnElementCheckResult();
      context.addConvertible(element, convertible);
      result.setElementId(id);
      result.setElementName(element.getAttribute("name"));
      result.setElementType(element.getLocalName());
      List<BpmnElementCheckResult> bpmnElementCheckResults =
          context.getReferencesToCreate().getOrDefault(id, new ArrayList<>());
      bpmnElementCheckResults.forEach(other -> createReference(other, result));
      this.result.getResults().add(result);
    }

    private boolean containsId(String id) {
      return result.getResults().stream().anyMatch(r -> r.getElementId().equals(id));
    }
  }
}
