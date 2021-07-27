package org.camunda.bpm.extension.engine.delegate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.core.variable.CoreVariableInstance;
import org.camunda.bpm.engine.impl.core.variable.scope.AbstractVariableScope;
import org.camunda.bpm.engine.impl.core.variable.scope.SimpleVariableInstance;
import org.camunda.bpm.engine.impl.core.variable.scope.SimpleVariableInstance.SimpleVariableInstanceFactory;
import org.camunda.bpm.engine.impl.core.variable.scope.VariableInstanceFactory;
import org.camunda.bpm.engine.impl.core.variable.scope.VariableInstanceLifecycleListener;
import org.camunda.bpm.engine.impl.core.variable.scope.VariableStore;

/**
 * Simple VariableScope implementation that can be initialized with a Map and
 * provides all variable methods required for implementing a DelegateExecution.
 *
 * Variables can be set and modified but will not be saved back into the engine.
 * 
 * @author Falko Menge (Camunda)
 */
public abstract class SimpleVariableScope extends AbstractVariableScope {

  private static final long serialVersionUID = 1L;

  protected VariableStore<SimpleVariableInstance> variableStore = new VariableStore<SimpleVariableInstance>();

  public SimpleVariableScope() {
    super();
  }

  public SimpleVariableScope(Map<String, ? extends Object> variables) {
    super();
    setVariables(variables);
  }

  protected VariableStore<CoreVariableInstance> getVariableStore() {
    return (VariableStore) variableStore;
  }

  @Override
  protected VariableInstanceFactory<CoreVariableInstance> getVariableInstanceFactory() {
    return (VariableInstanceFactory) SimpleVariableInstanceFactory.INSTANCE;
  }

  @Override
  protected List<VariableInstanceLifecycleListener<CoreVariableInstance>> getVariableInstanceLifecycleListeners() {
    return Collections.emptyList();
  }

  @Override
  public AbstractVariableScope getParentVariableScope() {
    return null;
  }

}