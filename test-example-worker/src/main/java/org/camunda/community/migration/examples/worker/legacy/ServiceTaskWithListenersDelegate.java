package org.camunda.community.migration.examples.worker.legacy;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static org.camunda.community.migration.examples.worker.C7WorkerWithListeners.logger;

@Component("legacyServiceTaskWithListeners")
public class ServiceTaskWithListenersDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("running: ServiceTaskWithListenersDelegate");
    }
}
