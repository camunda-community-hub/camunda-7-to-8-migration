package org.camunda.community.migration;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SampleDelegate.class);

    public static boolean executed = false;
    public static String capturedVariable = null;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOG.info("Called from process instance {}", execution.getProcessInstanceId());

        capturedVariable = (String) execution.getVariable("someVariable");
        executed = true;
    }
}
