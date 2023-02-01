package org.camunda.community.migration.processInstance.core;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class TestUtilExecutionListener implements TestExecutionListener {

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    TestUtil.zeebeClient = testContext.getApplicationContext().getBean(ZeebeClient.class);
  }

  @Override
  public void afterTestMethod(TestContext testContext) throws Exception {
    TestUtil.zeebeClient = null;
  }
}
