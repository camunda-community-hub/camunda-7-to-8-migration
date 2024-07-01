package org.camunda.community.migration.detector.rules;

import static org.assertj.core.api.Assertions.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import java.io.IOException;
import java.io.StringWriter;
import org.camunda.community.migration.detector.rules.test.MyDelegate;
import org.camunda.community.migration.detector.rules.test.MyExecutionListener;
import org.camunda.community.migration.detector.rules.test.MyProcessEnginePlugin;
import org.camunda.community.migration.detector.rules.test.MyProcessEngineServicesBean;
import org.camunda.community.migration.detector.rules.test.MySpringEventSubscriber;
import org.camunda.community.migration.detector.rules.test.MyTaskListener;

@AnalyzeClasses(packages = "org.camunda.community.migration.detector.rules.test")
public class ReportTest {
  @ArchTest
  void checkAll(JavaClasses classes) throws IOException {
    CodeMigrationReport report =
        new CodeMigrationReportBuilder(classes)
            .withArchRule(Camunda7MigrationRules.ensureNoImplementationOfCamunda7Interfaces())
            .withArchRule(Camunda7MigrationRules.ensureNoInvocationOfCamunda7Api())
            .withArchRule(Camunda7MigrationRules.ensureNoSpringBootEvents())
            .build();
    assertThat(report.classes())
        .containsKeys(
            MyDelegate.class.getName(),
            MyExecutionListener.class.getName(),
            MyProcessEnginePlugin.class.getName(),
            MyProcessEngineServicesBean.class.getName(),
            MySpringEventSubscriber.class.getName(),
            MyTaskListener.class.getName())
        .anySatisfy(
            (clazz, reportForClass) -> assertThat(reportForClass.rules()).hasSizeGreaterThan(1));
    StringWriter writer = new StringWriter();
    CodeMigrationReportPrinter.print(writer, report);
    System.out.println(writer);
  }
}
