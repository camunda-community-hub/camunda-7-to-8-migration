package io.berndruecker.converter.delegate;

import io.berndruecker.converter.delegate.execution.ZeebeJobDelegateExecution;
import io.berndruecker.converter.delegate.juel.JuelExpressionResolver;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CamundaPlatformDelegationWorker {

    @Autowired
    private JuelExpressionResolver expressionResolver;

    @ZeebeWorker(type="camunda-platform-to-cloud-migration")
    public void delegateToCamundaPlatformCode(final JobClient client, final ActivatedJob job) throws Exception {
        // Read config
        String delegateClass = job.getCustomHeaders().get("class");
        String delegateExpression = job.getCustomHeaders().get("delegateExpression");
        String expression = job.getCustomHeaders().get("expression");
        String resultVariable = job.getCustomHeaders().get("resultVariable");

        // and delegate depending on exact way of implementation
        Map<String, Object> resultPayload = null;
        DelegateExecution execution = wrapDelegateExecution(job);
        if (delegateClass != null) {
            JavaDelegate javaDelegate = loadJavaDelegate(delegateClass);
            javaDelegate.execute(execution);
            resultPayload = execution.getVariables();
        }
        else if (delegateExpression != null) {
            JavaDelegate javaDelegate = (JavaDelegate) expressionResolver.evaluate(delegateExpression, execution);
            javaDelegate.execute(execution);
            resultPayload = execution.getVariables();
        }
        else if (expression!=null) {
            Object result = expressionResolver.evaluate(expression, execution);
            if (resultVariable!=null) {
                resultPayload = new HashMap<>();
                resultPayload.put(resultVariable, result);
            }
        }
        else {
            throw new RuntimeException("Either 'class' or 'delegateExpression' or 'expression' must be specified in task headers for job :" + job);
        }

        CompleteJobCommandStep1 completeCommand = client.newCompleteCommand(job.getKey());
        if (resultPayload!=null) {
           completeCommand.variables(resultPayload);
        }
        completeCommand.send().join();
   }

    private DelegateExecution wrapDelegateExecution(ActivatedJob job) {
        return new ZeebeJobDelegateExecution(job);
    }

    private JavaDelegate loadJavaDelegate(String delegateName) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class<? extends JavaDelegate> clazz = (Class<? extends JavaDelegate>) contextClassLoader.loadClass(delegateName);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not load delegation class '" + delegateName + "': " + e.getMessage(), e);
        }
    }


}