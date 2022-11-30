package org.camunda.community.migration.converter.convertible;

public abstract class AbstractActivityConvertible extends AbstractDataMapperConvertible {
  private BpmnMultiInstanceLoopCharacteristics bpmnMultiInstanceLoopCharacteristics;

  public BpmnMultiInstanceLoopCharacteristics getBpmnMultiInstanceLoopCharacteristics() {
    return bpmnMultiInstanceLoopCharacteristics;
  }

  public boolean wasLoopCharacteristicsInitialized() {
    return bpmnMultiInstanceLoopCharacteristics != null;
  }

  public void initializeLoopCharacteristics() {
    if (bpmnMultiInstanceLoopCharacteristics == null) {
      bpmnMultiInstanceLoopCharacteristics = new BpmnMultiInstanceLoopCharacteristics();
    }
  }

  public static final class BpmnMultiInstanceLoopCharacteristics {
    private final ZeebeLoopCharacteristics zeebeLoopCharacteristics =
        new ZeebeLoopCharacteristics();
    private String completionCondition;
    private boolean isSequential = false;

    public String getCompletionCondition() {
      return completionCondition;
    }

    public void setCompletionCondition(String completionCondition) {
      this.completionCondition = completionCondition;
    }

    public ZeebeLoopCharacteristics getZeebeLoopCharacteristics() {
      return zeebeLoopCharacteristics;
    }

    public boolean isSequential() {
      return isSequential;
    }

    public void setSequential(boolean sequential) {
      isSequential = sequential;
    }
  }

  public static final class ZeebeLoopCharacteristics {
    private String inputCollection;
    private String inputElement;
    private String outputCollection;
    private String outputElement;

    public String getInputCollection() {
      return inputCollection;
    }

    public void setInputCollection(String inputCollection) {
      this.inputCollection = inputCollection;
    }

    public String getInputElement() {
      return inputElement;
    }

    public void setInputElement(String inputElement) {
      this.inputElement = inputElement;
    }

    public String getOutputCollection() {
      return outputCollection;
    }

    public void setOutputCollection(String outputCollection) {
      this.outputCollection = outputCollection;
    }

    public String getOutputElement() {
      return outputElement;
    }

    public void setOutputElement(String outputElement) {
      this.outputElement = outputElement;
    }
  }
}
