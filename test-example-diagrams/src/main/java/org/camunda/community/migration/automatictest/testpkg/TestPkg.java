package org.camunda.community.migration.automatictest.testpkg;

import java.io.File;
import org.camunda.automator.definition.Scenario;
import org.camunda.automator.engine.AutomatorException;

public class TestPkg {

  /** Automator process */
  private File automator;

  /** Camunda 7 if the process exist */
  private File camunda7;
  /** Camunda 8 if the process exist */
  private File camunda8;

  /**
   * detect a TestPck A test package must have different files, starting with the same name XXX.bpmn
   * (or XXX_C7.bpmn) is the Camunda 7 process XXX.Automator.json is the Automator scenario
   * XXX-C8.bpmn is the Camunda 8 process The detection is based on the Automator file.
   *
   * @param file
   * @return
   */
  public static TestPkg createForFile(File file) throws Exception {
    if (!file.getName().endsWith("Automator.json")) return null;
    String fileName = file.getName();
    String folderName = file.getParent();
    String prefix = fileName.substring(0, fileName.length() - "Automator.json".length());
    TestPkg testPkg = new TestPkg();
    testPkg.automator = file;
    if (new File(folderName + "/" + prefix + ".bpmn").isFile())
      testPkg.camunda7 = new File(folderName + "/" + prefix + ".bpmn");
    if (new File(folderName + "/" + prefix + "_C7.bpmn").isFile())
      testPkg.camunda7 = new File(folderName + "/" + prefix + "_C7.bpmn");

    if (new File(folderName + "/" + prefix + "_C8.bpmn").isFile())
      testPkg.camunda8 = new File(folderName + "/" + prefix + "_C8.bpmn");
    return testPkg;
  }

  public boolean checkEnvironment() {
    return automator != null
        && automator.exists()
        && camunda7 != null
        && camunda7.exists()
        && camunda8 != null
        && camunda8.exists();
  }

  /**
   * Return the incomplete status. If the test is complete, the report is empty
   *
   * @return the status
   */
  public String getIncompleteStatus() {
    StringBuilder report = new StringBuilder();
    if (automator == null || !automator.exists()) report.append("Automator scenario is missing;");
    if (camunda7 == null || !camunda7.exists()) report.append("Camunda 7 process is missing;");
    if (camunda8 == null || !camunda8.exists()) report.append("Camunda 8 process is missing;");
    return report.toString();
  }

  public Scenario getScenario() throws AutomatorException {
    if (automator.exists()) return Scenario.createFromFile(automator);
    return null;
  }

  public String getName() {
    try {
      return getScenario().getName();
    } catch (Exception e) {
      return automator.getName();
    }
  }
}
