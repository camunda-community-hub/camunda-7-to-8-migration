package org.camunda.community.migration.automatictest.service;

import org.camunda.automator.AutomatorAPI;
import org.camunda.automator.bpmnengine.BpmnEngine;
import org.camunda.automator.engine.AutomatorException;
import org.camunda.automator.engine.RunParameters;
import org.camunda.community.migration.automatictest.detection.Detection;
import org.camunda.community.migration.automatictest.report.ReportExecution;
import org.camunda.community.migration.automatictest.report.ReportExecutionAggregate;
import org.camunda.community.migration.automatictest.report.ReportExecutionLog;
import org.camunda.community.migration.automatictest.report.ReportExecutionSynthesis;
import org.camunda.community.migration.automatictest.testpkg.TestPkg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@ConfigurationProperties(prefix = "automatictest.testfolder")

public class ExecuteTest {

  static Logger logger = LoggerFactory.getLogger(ExecuteTest.class);

  @Autowired
  ResourceLoader resourceLoader;

  @Autowired
  Configuration configuration;

  @Value("${automatictest.testfolder}")
  public Optional<String> testFolder;

  @PostConstruct
  public void init() {
    if (testFolder.isEmpty() || testFolder.get().trim().isEmpty()) {
      Resource file = resourceLoader.getResource("classpath:application.yaml");
      if (file != null)
        try {
          testFolder = Optional.ofNullable(file.getFile().getParent());
        } catch (Exception e) {
          logger.error("Can't access resource application.yaml");
        }
    }
    if (!testFolder.isPresent() || testFolder.isEmpty() || testFolder.get().trim().isEmpty())
      logger.error("Test Folder is not specified, and no application.yaml is detected");
    detectAndExecute(new File(testFolder.get()));
  }

  public void detectAndExecute(File folder) {
    Detection detection = new Detection(folder);
    List<TestPkg> listTestPck = detection.detectTestPackage();
    ReportExecution reportExecutionLog = new ReportExecutionLog();
    ReportExecutionSynthesis reportExecutionSynthesis  =new ReportExecutionSynthesis();
    ReportExecutionAggregate reportExecutionAggregate = new ReportExecutionAggregate();
    reportExecutionAggregate.addReport(reportExecutionSynthesis);
    reportExecutionAggregate.addReport(reportExecutionLog);


    for (TestPkg testPkg : listTestPck) {
      execute(testPkg, reportExecutionAggregate);
    }
    reportExecutionSynthesis.logSynthesis();
  }

  private void execute(TestPkg testPkg, ReportExecution reportExecution) {
    reportExecution.startExecution(testPkg);
    try {
      AutomatorAPI automatorAPI = new AutomatorAPI();
      BpmnEngine bpmnEngine7 = configuration.getCamunda7BpmnEngine();
      if (!testPkg.isComplete()) {
        reportExecution.endExecution(false, testPkg.getIncompleteStatus());
        return;
      }
      // Deploy Camunda 7 Process
      deployProcessCamunda7(testPkg, automatorAPI, bpmnEngine7);

      // run Automator on Camunda 7
      runAutomatorCamunda7(testPkg, automatorAPI, bpmnEngine7);
      // Check Automator in Camunda 7

      // Run migration
      reportExecution.endExecution(false, "Can't run migration ");

      // Check Automator in Camunda 8

      // the end
      // reportExecution.endExecution(true, "OK!");
    } catch (AutomatorException e) {
      reportExecution.endExecution(false, "Error " + e.getMessage());
    }
  }

  private void deployProcessCamunda7(TestPkg testPkg, AutomatorAPI automatorAPI, BpmnEngine bpmnEngine)
      throws AutomatorException {
    automatorAPI.deployProcess(bpmnEngine, testPkg.getScenario());

  }

  private void runAutomatorCamunda7(TestPkg testPkg, AutomatorAPI automatorAPI, BpmnEngine bpmnEngine)
      throws AutomatorException {
    RunParameters runParameters = new RunParameters();
    runParameters.verification = true;
    runParameters.execute = true;
    runParameters.allowDeployment = false; // already done
    automatorAPI.executeScenario(bpmnEngine, runParameters, testPkg.getScenario());

  }
}
