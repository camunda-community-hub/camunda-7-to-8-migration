package org.camunda.community.migration.automatictest.service;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.camunda.automator.AutomatorAPI;
import org.camunda.automator.bpmnengine.BpmnEngine;
import org.camunda.automator.engine.AutomatorException;
import org.camunda.automator.engine.RunParameters;
import org.camunda.automator.engine.RunResult;
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

@Service
@ConfigurationProperties(prefix = "automatictest.testfolder")
public class ExecuteTest {

  static Logger logger = LoggerFactory.getLogger(ExecuteTest.class);

  @Autowired ResourceLoader resourceLoader;

  @Autowired Configuration configuration;

  @Value("${automatictest.testfolder}")
  public Optional<String> testFolder;

  @Value("${automatictest.forceListTestsName}")
  public Optional<String> forceListTestsName;

  @Value("${automatictest.exitAfterExecution}")
  public Optional<Boolean> exitAfterExecution;

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
    if (exitAfterExecution.isPresent() && exitAfterExecution.get()) {
      System.exit(0);
    }
  }

  public void detectAndExecute(File folder) {
    Detection detection = new Detection(folder);
    List<TestPkg> listTestPck = detection.detectTestPackage();
    if (forceListTestsName.isPresent()) {
      Set<String> setForceTestSet = new HashSet<>();
      StringTokenizer st = new StringTokenizer(forceListTestsName.get(), ",");
      while (st.hasMoreTokens()) setForceTestSet.add(st.nextToken());
      listTestPck =
          listTestPck.stream().filter(t -> setForceTestSet.contains(t.getName())).toList();
    }
    ReportExecution reportExecutionLog = new ReportExecutionLog();
    ReportExecutionSynthesis reportExecutionSynthesis = new ReportExecutionSynthesis();
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
      BpmnEngine bpmnEngine8 = configuration.getCamunda8BpmnEngine();

      if (!testPkg.checkEnvironment()) {
        reportExecution.endExecution(false, testPkg.getIncompleteStatus());
        return;
      }
      // Deploy Camunda 7 Process
      RunResult runResult = deployProcessCamundaEngine(testPkg, automatorAPI, bpmnEngine7);

      // Deploy Camunda 8 Process
      if (runResult.isSuccess())
        runResult.add(deployProcessCamundaEngine(testPkg, automatorAPI, bpmnEngine8));

      // run Automator on Camunda 7
      if (runResult.isSuccess())
        runResult.add(runAutomatorCamundaEngine(testPkg, automatorAPI, bpmnEngine7));

      // Run migration
      // reportExecution.endExecution(false, "Can't run migration ");

      // This should not exist - only for test at this moment
      if (runResult.isSuccess()) {
        runResult.add(runAutomatorCamundaEngine(testPkg, automatorAPI, bpmnEngine8));
        // Wait 30 s to get Camunda 8 push everything to Optimize
        try {
          Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
          // Do nothing
        }
      }

      // Check Automator in Camunda 8
      if (runResult.isSuccess())
        runResult.add(verifyAutomatorCamundaEngine(testPkg, automatorAPI, bpmnEngine8));

      String messageError =
          runResult.getListErrors().stream()
              .map(t -> t.explanation)
              .collect(Collectors.joining(", "));

      String messageVerification =
          runResult.getListVerifications().stream()
              .filter(t -> !t.isSuccess)
              .map(t -> t.message)
              .collect(Collectors.joining(", "));

      reportExecution.endExecution(
          runResult.isSuccess(),
          (messageError.isEmpty() ? "" : "Err.: " + messageError)
              + " "
              + (messageVerification.isEmpty() ? "" : "Veri.: " + messageVerification));

      // the end
    } catch (AutomatorException e) {
      reportExecution.endExecution(false, "Error " + e.getMessage());
    }
  }

  private RunResult deployProcessCamundaEngine(
      TestPkg testPkg, AutomatorAPI automatorAPI, BpmnEngine bpmnEngine) throws AutomatorException {
    RunParameters runParameters = new RunParameters();

    return automatorAPI.deployProcess(bpmnEngine, runParameters, testPkg.getScenario());
  }

  private RunResult runAutomatorCamundaEngine(
      TestPkg testPkg, AutomatorAPI automatorAPI, BpmnEngine bpmnEngine) throws AutomatorException {
    RunParameters runParameters = new RunParameters();
    runParameters.verification = true;
    runParameters.execution = true;
    runParameters.allowDeployment = false;
    runParameters.clearAllAfter = false; //  we want to keep everything for analysis
    return automatorAPI.executeScenario(bpmnEngine, runParameters, testPkg.getScenario());
  }

  // Check Automator in Camunda 8
  private RunResult verifyAutomatorCamundaEngine(
      TestPkg testPkg, AutomatorAPI automatorAPI, BpmnEngine bpmnEngine) throws AutomatorException {

    RunParameters runParameters = new RunParameters();
    runParameters.verification = true;
    runParameters.execution = false;
    runParameters.allowDeployment = false; // already done
    runParameters.clearAllAfter = false; // we want to keep everything for analysis
    return automatorAPI.executeScenario(bpmnEngine, runParameters, testPkg.getScenario());
  }
}
