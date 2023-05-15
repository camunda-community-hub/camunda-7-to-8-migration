package org.camunda.community.migration.examples.worker.legacy;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import static org.camunda.community.migration.examples.worker.C7WorkerWithListeners.logger;

@Component("legacyEndListener")
public class OnEndExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        Integer value = (Integer) execution.getVariable("value");

        logger.info("running: OnEndExecutionListener");

        logger.info("variables: " + execution.getVariables());
        execution.setVariable("value", value * 2);
        execution.setVariable("bar", "some stuff");

    }
}
