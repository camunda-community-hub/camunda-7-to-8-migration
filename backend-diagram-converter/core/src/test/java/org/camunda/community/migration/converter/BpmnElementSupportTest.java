package org.camunda.community.migration.converter;

import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.converter.TestUtil.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckMessage;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckResult;
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

  private Executable isSupported(DiagramCheckResult result, String elementId) {
    return () -> {
      LOG.info("Complete Result: {}", result);
      LOG.info("Element Result: {}", result.getResult(elementId));
      assertThat(result.getResult(elementId))
          .isNotNull()
          .extracting(ElementCheckResult::getMessages)
          .asList()
          .isEmpty();
    };
  }

  private Executable isNotSupported(
      DiagramCheckResult result, String elementId, String elementType) {
    return () -> {
      LOG.info("Complete Result: {}", result);
      LOG.info("Element Result: {}", result.getResult(elementId));
      assertThat(result.getResult(elementId))
          .isNotNull()
          .extracting(ElementCheckResult::getMessages)
          .matches(l -> l.size() == 1, "Has one entry")
          .extracting(l -> l.get(0))
          .extracting(ElementCheckMessage::getMessage)
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
    DiagramCheckResult result = loadAndCheck("message-events.bpmn");
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
    DiagramCheckResult result = loadAndCheck("timer-events.bpmn");
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
    DiagramCheckResult result = loadAndCheck("signal-events.bpmn");
    return eventTypeTest(
        "Signal",
        isSupported(result, "SignalStartStartEvent"),
        isSupported(result, "SignalThrowEvent"),
        isSupported(result, "SignalCatchEvent"),
        isSupported(result, "SignalEndEndEvent"),
        isSupported(result, "SignalAttachedBoundaryEvent"),
        isSupported(result, "SignalAttachedNoninterruptingBoundaryEvent"),
        isSupported(result, "SignalEventSubprocesStartStartEvent"),
        isSupported(result, "SignalEventSubprocessStartNoninterruptingStartEvent"));
  }

  @TestFactory
  Stream<DynamicTest> testErrorEvents() {
    DiagramCheckResult result = loadAndCheck("error-events.bpmn");
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
    DiagramCheckResult result = loadAndCheck("conditional-events.bpmn");
    return eventTypeTest(
        "Conditional",
        isNotSupported(result, "ConditionalStartStartEvent", "Conditional Start Event"),
        null,
        isNotSupported(result, "ConditionalCatchEvent", "Conditional Intermediate Catch Event"),
        null,
        isNotSupported(
            result,
            "ConditionalAttachedBoundaryEvent",
            "Conditional Boundary Event attached to Task"),
        isNotSupported(
            result,
            "ConditionalAttachedNoninterruptingBoundaryEvent",
            "Non-interrupting Conditional Boundary Event attached to Task"),
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
    DiagramCheckResult result = loadAndCheck("escalation-events.bpmn");
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
    DiagramCheckResult result = loadAndCheck("compensation-events.bpmn");
    return eventTypeTest(
        "Compensation",
        null,
        isSupported(result, "CompensationThrowEvent"),
        null,
        isSupported(result, "CompensationEndEndEvent"),
        isSupported(result, "CompensationAttachedBoundaryEvent"),
        null,
        isNotSupported(
            result,
            "CompensationEventSubprocesStartStartEvent",
            "Event Sub Process Compensate Start Event"),
        null);
  }

  @TestFactory
  Stream<DynamicTest> testCancelEvent() {
    DiagramCheckResult result = loadAndCheck("cancel-events.bpmn");
    return eventTypeTest(
        "Cancel",
        null,
        null,
        null,
        isNotSupported(result, "CancelEndEvent", "Cancel End Event"),
        isNotSupported(
            result, "CancelBoundaryEvent", "Cancel Boundary Event attached to Transaction"),
        null,
        null,
        null);
  }

  @TestFactory
  Stream<DynamicTest> testTerminateEvent() {
    DiagramCheckResult result = loadAndCheck("terminate-events.bpmn");
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
    DiagramCheckResult result = loadAndCheck("link-events.bpmn");
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
    DiagramCheckResult result = loadAndCheck("markers.bpmn");
    return Stream.of(
        dynamicTest("Parallel Task", isSupported(result, "ParallelTask")),
        dynamicTest("Sequential Task", isSupported(result, "SequentialTask")),
        dynamicTest(
            "Loop Task", isNotSupported(result, "LoopTask", "Standard Loop Characteristics")),
        dynamicTest("Compensation Task", isSupported(result, "CompensationTask")));
  }

  @TestFactory
  Stream<DynamicTest> testGateways() {
    DiagramCheckResult result = loadAndCheck("gateways.bpmn");
    return Stream.of(
        dynamicTest("Exclusive Gateway", isSupported(result, "ExclusiveGateway")),
        dynamicTest("Parallel Gateway", isSupported(result, "ParallelGateway")),
        dynamicTest("Inclusive Gateway", isSupported(result, "InclusiveInclusiveGateway")),
        dynamicTest("Complex Gateway", isNotSupported(result, "ComplexGateway", "Complex Gateway")),
        dynamicTest("Event-Based Gateway", isSupported(result, "EventBasedGateway")));
  }

  @TestFactory
  Stream<DynamicTest> testTasks() {
    DiagramCheckResult result = loadAndCheck("tasks.bpmn");
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
    DiagramCheckResult result = loadAndCheck("subprocesses.bpmn");
    return Stream.of(
        dynamicTest("Subprocess", isSupported(result, "SubprocessSubProcess")),
        dynamicTest("Call Activity", isSupported(result, "CallActivityCallActivity")),
        dynamicTest("Event Subprocess", isSupported(result, "EventSubprocessSubProcess")),
        dynamicTest(
            "Transaction", isNotSupported(result, "TransactionTransaction", "Transaction")));
  }

  @TestFactory
  Stream<DynamicTest> testParticipants() {
    DiagramCheckResult result = loadAndCheck("participants.bpmn");
    return Stream.of(
        dynamicTest("Pool", isSupported(result, "PoolParticipant")),
        dynamicTest("Lane", isSupported(result, "LaneLane")));
  }
}
