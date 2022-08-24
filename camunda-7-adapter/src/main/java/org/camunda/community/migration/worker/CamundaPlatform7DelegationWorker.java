package org.camunda.community.migration.worker;

import org.camunda.bpm.engine.ArtifactFactory;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.community.migration.execution.ZeebeJobDelegateExecution;
import org.camunda.community.migration.juel.JuelExpressionResolver;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CamundaPlatform7DelegationWorker {

  private final JuelExpressionResolver expressionResolver;
  private final ArtifactFactory artifactFactory;

  public CamundaPlatform7DelegationWorker(
      JuelExpressionResolver expressionResolver, ArtifactFactory artifactFactory
  ) {
    this.expressionResolver = expressionResolver;
    this.artifactFactory = artifactFactory;
  }

  @ZeebeWorker(type = "camunda-7-adapter")
  public void delegateToCamundaPlatformCode(final JobClient client, final ActivatedJob job) throws Exception {
    // Read config
    String delegateClass = job
        .getCustomHeaders()
        .get("class");
    String delegateExpression = job
        .getCustomHeaders()
        .get("delegateExpression");
    String expression = job
        .getCustomHeaders()
        .get("expression");
    String resultVariable = job
        .getCustomHeaders()
        .get("resultVariable");

    // and delegate depending on exact way of implementation
    Map<String, Object> resultPayload = null;
    DelegateExecution execution = wrapDelegateExecution(job);
    // this is required as we add the execution to the variables scope for expression evaluation
    VariableScope variableScope = wrapDelegateExecution(job);
    if (delegateClass != null) {
      JavaDelegate javaDelegate = loadJavaDelegate(delegateClass);
      javaDelegate.execute(execution);
      resultPayload = execution.getVariables();
    } else if (delegateExpression != null) {
      JavaDelegate javaDelegate = (JavaDelegate) expressionResolver.evaluate(
          delegateExpression,
          variableScope,
          execution
      );
      javaDelegate.execute(execution);
      resultPayload = execution.getVariables();
    } else if (expression != null) {
      Object result = expressionResolver.evaluate(expression, variableScope, execution);
      if (resultVariable != null) {
        resultPayload = new HashMap<>();
        resultPayload.put(resultVariable, result);
      }
    } else {
      throw new RuntimeException(
          "Either 'class' or 'delegateExpression' or 'expression' must be specified in task headers for job :" + job);
    }

    CompleteJobCommandStep1 completeCommand = client.newCompleteCommand(job.getKey());
    if (resultPayload != null) {
      completeCommand.variables(resultPayload);
    }
    completeCommand
        .send()
        .join();

  }

  private DelegateExecution wrapDelegateExecution(ActivatedJob job) {
    return new ZeebeJobDelegateExecution(job);
  }

  private JavaDelegate loadJavaDelegate(String delegateName) {
    final ClassLoader contextClassLoader = Thread
        .currentThread()
        .getContextClassLoader();
    try {
      Class<? extends JavaDelegate> clazz = (Class<? extends JavaDelegate>) contextClassLoader.loadClass(delegateName);
      return artifactFactory.getArtifact(clazz);
    } catch (Exception e) {
      throw new RuntimeException("Could not load delegation class '" + delegateName + "': " + e.getMessage(), e);
    }
  }

}