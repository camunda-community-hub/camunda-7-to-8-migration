package org.camunda.community.migration.converter;

import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.converter.TestUtil.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BpmnElementSupportTest {
  private static final Logger LOG = LoggerFactory.getLogger(BpmnElementSupportTest.class);

  private Stream<DynamicTest> eventTypeTest(
      String eventType,
      Executable startEventAssertion,
      Executable intermediateThrowEventAssertion,
      Executable intermediateCatchEventAssertion,
      Executable endEventAssertion,
      Executable boundaryEventAssertion,
      Executable nonInterruptingBoundaryEventAssertion,
      Executable eventSubProcessAssertion,
      Executable nonInterruptingEventSubProcessAssertion) {
    return Stream.of(
            ofNullable(startEventAssertion).map(fromExecutable(eventType + " Start Event")),
            ofNullable(intermediateThrowEventAssertion)
                .map(fromExecutable(eventType + " Intermediate Throw Event")),
            ofNullable(intermediateCatchEventAssertion)
                .map(fromExecutable(eventType + " Intermediate Catch Event")),
            ofNullable(endEventAssertion).map(fromExecutable(eventType + " End Event")),
            ofNullable(boundaryEventAssertion).map(fromExecutable(eventType + " Boundary Event")),
            ofNullable(nonInterruptingBoundaryEventAssertion)
                .map(fromExecutable(eventType + " Non-interrupting Boundary Event")),
            ofNullable(eventSubProcessAssertion)
                .map(fromExecutable(eventType + " Event Sub Process")),
            ofNullable(nonInterruptingEventSubProcessAssertion)
                .map(fromExecutable(eventType + " Non-interrupting Event Sub Process")))
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  private Function<Executable, DynamicTest> fromExecutable(String name) {
    return e -> dynamicTest(name, e);
  }

  private Executable isSupported(BpmnDiagramCheckResult result, String elementId) {
    return () -> {
      LOG.info("Complete Result: {}", result);
      LOG.info("Element Result: {}", result.getResult(elementId));
      assertThat(result.getResult(elementId))
          .isNotNull()
          .extracting(BpmnElementCheckResult::getMessages)
          .asList()
          .isEmpty();
    };
  }

  private Executable isNotSupported(
      BpmnDiagramCheckResult result, String elementId, String elementType) {
    return () -> {
      LOG.info("Complete Result: {}", result);
      LOG.info("Element Result: {}", result.getResult(elementId));
      assertThat(result.getResult(elementId))
          .isNotNull()
          .extracting(BpmnElementCheckResult::getMessages)
          .matches(l -> l.size() == 1, "Has one entry")
          .extracting(l -> l.get(0))
          .extracting(BpmnElementCheckMessage::getMessage)
          .isEqualTo(
              "Element '"
                  + elementType
                  + "' is not supported in Zeebe version '"
                  + ConverterPropertiesFactory.getInstance().get().getPlatformVersion()
                  + "'. Please review.");
    };
  }

  @TestFactory
  Stream<DynamicTest> testMessageEvents() {
    BpmnDiagramCheckResult result = loadAndCheck("message-events.bpmn");
    return eventTypeTest(
        "Message",
        isSupported(result, "MessageStartStartEvent"),
        isSupported(result, "MessageThrowEvent"),
        isSupported(result, "MessageCatchEvent"),
        isSupported(result, "MessageEndEndEvent"),
        isSupported(result, "MessageAttachedBoundaryEvent"),
        isSupported(result, "MessageAttachedNoninterruptingBoundaryEvent"),
        isSupported(result, "MessageEventSubprocesStartStartEvent"),
        isSupported(result, "MessageEventSubprocessStartNoninterruptingStartEvent"));
  }

  @TestFactory
  Stream<DynamicTest> testTimerEvents() {
    BpmnDiagramCheckResult result = loadAndCheck("timer-events.bpmn");
    return eventTypeTest(
        "Timer",
        isSupported(result, "TimerStartStartEvent"),
        null,
        isSupported(result, "TimerCatchEvent"),
        null,
        isSupported(result, "TimerAttachedBoundaryEvent"),
        isSupported(result, "TimerAttachedNoninterruptingBoundaryEvent"),
        isSupported(result, "TimerEventSubprocesStartStartEvent"),
        isSupported(result, "TimerEventSubprocessStartNoninterruptingStartEvent"));
  }

  @TestFactory
  Stream<DynamicTest> testSignalEvents() {
    BpmnDiagramCheckResult result = loadAndCheck("signal-events.bpmn");
    return eventTypeTest(
        "Signal",
        isSupported(result, "SignalStartStartEvent"),
        isNotSupported(result, "SignalThrowEvent", "Signal Intermediate Throw Event"),
        isNotSupported(result, "SignalCatchEvent", "Signal Intermediate Catch Event"),
        isNotSupported(result, "SignalEndEndEvent", "Signal End Event"),
        isNotSupported(result, "SignalAttachedBoundaryEvent", "Signal Boundary Event"),
        isNotSupported(
            result,
            "SignalAttachedNoninterruptingBoundaryEvent",
            "Non-interrupting Signal Boundary Event"),
        isNotSupported(
            result, "SignalEventSubprocesStartStartEvent", "Event Sub Process Signal Start Event"),
        isNotSupported(
            result,
            "SignalEventSubprocessStartNoninterruptingStartEvent",
            "Non-interrupting Event Sub Process Signal Start Event"));
  }

  @TestFactory
  Stream<DynamicTest> testErrorEvents() {
    BpmnDiagramCheckResult result = loadAndCheck("error-events.bpmn");
    return eventTypeTest(
        "Error",
        null,
        null,
        null,
        isSupported(result, "ErrorCatchEndEvent"),
        isSupported(result, "ErrorAttachedBoundaryEvent"),
        null,
        isSupported(result, "ErrorEventSubprocesStartStartEvent"),
        null);
  }

  @TestFactory
  Stream<DynamicTest> testConditionalEvents() {
    BpmnDiagramCheckResult result = loadAndCheck("conditional-events.bpmn");
    return eventTypeTest(
        "Conditional",
        isNotSupported(result, "ConditionalStartStartEvent", "Conditional Start Event"),
        null,
        isNotSupported(result, "ConditionalCatchEvent", "Conditional Intermediate Catch Event"),
        null,
        isNotSupported(result, "ConditionalAttachedBoundaryEvent", "Conditional Boundary Event"),
        isNotSupported(
            result,
            "ConditionalAttachedNoninterruptingBoundaryEvent",
            "Non-interrupting Conditional Boundary Event"),
        isNotSupported(
            result,
            "ConditionalEventSubprocesStartStartEvent",
            "Event Sub Process Conditional Start Event"),
        isNotSupported(
            result,
            "ConditionalEventSubprocessStartNoninterruptingStartEvent",
            "Non-interrupting Event Sub Process Conditional Start Event"));
  }

  @TestFactory
  Stream<DynamicTest> testEscalationEvents() {
    BpmnDiagramCheckResult result = loadAndCheck("escalation-events.bpmn");
    return eventTypeTest(
        "Escalation",
        null,
        isSupported(result, "EscalationThrowEvent"),
        null,
        isSupported(result, "EscalationEndEndEvent"),
        isSupported(result, "EscalationAttachedBoundaryEvent"),
        isSupported(result, "EscalationAttachedNoninterruptingBoundaryEvent"),
        isSupported(result, "EscalationEventSubprocesStartStartEvent"),
        isSupported(result, "EscalationEventSubprocessStartNoninterruptingStartEvent"));
  }

  @TestFactory
  Stream<DynamicTest> testCompensationEvents() {
    BpmnDiagramCheckResult result = loadAndCheck("compensation-events.bpmn");
    return eventTypeTest(
        "Compensation",
        null,
        isNotSupported(result, "CompensationThrowEvent", "Compensate Intermediate Throw Event"),
        null,
        isNotSupported(result, "CompensationEndEndEvent", "Compensate End Event"),
        isNotSupported(result, "CompensationAttachedBoundaryEvent", "Compensate Boundary Event"),
        null,
        isNotSupported(
            result,
            "CompensationEventSubprocesStartStartEvent",
            "Event Sub Process Compensate Start Event"),
        null);
  }

  @TestFactory
  Stream<DynamicTest> testCancelEvent() {
    BpmnDiagramCheckResult result = loadAndCheck("cancel-events.bpmn");
    return eventTypeTest(
        "Cancel",
        null,
        null,
        null,
        isNotSupported(result, "CancelEndEvent", "Cancel End Event"),
        isNotSupported(result, "CancelBoundaryEvent", "Cancel Boundary Event"),
        null,
        null,
        null);
  }

  @TestFactory
  Stream<DynamicTest> testTerminateEvent() {
    BpmnDiagramCheckResult result = loadAndCheck("terminate-events.bpmn");
    return eventTypeTest(
        "Terminate",
        null,
        null,
        null,
        isSupported(result, "TerminateEndEvent"),
        null,
        null,
        null,
        null);
  }

  @TestFactory
  Stream<DynamicTest> testLinkEvent() {
    BpmnDiagramCheckResult result = loadAndCheck("link-events.bpmn");
    return eventTypeTest(
        "Link",
        null,
        isSupported(result, "LinkThrowEvent"),
        isSupported(result, "LinkCatchEvent"),
        null,
        null,
        null,
        null,
        null);
  }

  @TestFactory
  Stream<DynamicTest> testMarkers() {
    BpmnDiagramCheckResult result = loadAndCheck("markers.bpmn");
    return Stream.of(
        dynamicTest("Parallel Task", isSupported(result, "ParallelTask")),
        dynamicTest("Sequential Task", isSupported(result, "SequentialTask")),
        dynamicTest(
            "Loop Task", isNotSupported(result, "LoopTask", "Standard Loop Characteristics")),
        dynamicTest(
            "Compensation Task", isNotSupported(result, "CompensationTask", "Compensation Task")));
  }

  @TestFactory
  Stream<DynamicTest> testGateways() {
    BpmnDiagramCheckResult result = loadAndCheck("gateways.bpmn");
    return Stream.of(
        dynamicTest("Exclusive Gateway", isSupported(result, "ExclusiveGateway")),
        dynamicTest("Parallel Gateway", isSupported(result, "ParallelGateway")),
        dynamicTest("Inclusive Gateway", isSupported(result, "InclusiveInclusiveGateway")),
        dynamicTest("Complex Gateway", isNotSupported(result, "ComplexGateway", "Complex Gateway")),
        dynamicTest("Event-Based Gateway", isSupported(result, "EventBasedGateway")));
  }

  @TestFactory
  Stream<DynamicTest> testTasks() {
    BpmnDiagramCheckResult result = loadAndCheck("tasks.bpmn");
    return Stream.of(
        dynamicTest("Undefined Task", isSupported(result, "UndefinedTask")),
        dynamicTest("Service Task", isSupported(result, "ServiceTask")),
        dynamicTest("User Task", isSupported(result, "UserTask")),
        dynamicTest("Receive Task", isSupported(result, "ReceiveTask")),
        dynamicTest("Send Task", isSupported(result, "SendTask")),
        dynamicTest("Business Rule Task", isSupported(result, "BusinessRuleTask")),
        dynamicTest("Script Task", isSupported(result, "ScriptTask")),
        dynamicTest("Manual Task", isSupported(result, "ManualTask")),
        dynamicTest(
            "Receive Task (instantiated)",
            isNotSupported(result, "ReceiveInstantiatedTask", "Receive Task")));
  }

  @TestFactory
  Stream<DynamicTest> testSubprocesses() {
    BpmnDiagramCheckResult result = loadAndCheck("subprocesses.bpmn");
    return Stream.of(
        dynamicTest("Subprocess", isSupported(result, "SubprocessSubProcess")),
        dynamicTest("Call Activity", isSupported(result, "CallActivityCallActivity")),
        dynamicTest("Event Subprocess", isSupported(result, "EventSubprocessSubProcess")),
        dynamicTest(
            "Transaction", isNotSupported(result, "TransactionTransaction", "Transaction")));
  }

  @TestFactory
  Stream<DynamicTest> testParticipants() {
    BpmnDiagramCheckResult result = loadAndCheck("participants.bpmn");
    return Stream.of(
        dynamicTest("Pool", isSupported(result, "PoolParticipant")),
        dynamicTest("Lane", isSupported(result, "LaneLane")));
  }
}
