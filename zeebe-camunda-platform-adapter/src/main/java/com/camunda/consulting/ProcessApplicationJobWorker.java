package com.camunda.consulting;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnDecisionResultEntries;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

/**
 * @author Daniel Meyer (Camunda)
 * @author Falko Menge (Camunda)
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessApplicationJobWorker implements JobHandler {

	private static final Object JAVA_DELEGATE_CLASS_NAME = "JAVA_DELEGATE";
	private static final Object DECISION_REF = "DECISION_REF";
	
	@Override
	public void handle(JobClient client, ActivatedJob job) {
		
		System.out.println("Executing Job " + job);
		
		try {
			Map<String, String> jobHeaders = job.getCustomHeaders();
			String delegateName = (String) jobHeaders.get(JAVA_DELEGATE_CLASS_NAME);
			String decisionRef = (String) jobHeaders.get(DECISION_REF);
			
			if (delegateName == null && decisionRef == null) {
				throw new RuntimeException("Either java delegate or decision ref must be specified for job :" + job);
			}

			Map<String, Object> resultPayload = null;
			
			if (delegateName != null) {
				
				DelegateExecution execution = wrapDelegateExecution(job);
				JavaDelegate loadJavaDelegate = loadJavaDelegate(delegateName);
				loadJavaDelegate.execute(execution);
				resultPayload = execution.getVariables();
			}
			else {
				DecisionService decisionService = BpmPlatform.getDefaultProcessEngine()
					.getDecisionService();
				DmnDecisionResult decisionResult = decisionService.evaluateDecisionByKey(decisionRef)
					.variables(job.getVariablesAsMap())
					.evaluate();

				// TODO: implement other result mappings
				DmnDecisionResultEntries singleResult = decisionResult.getSingleResult();
				resultPayload = new HashMap<>();
				String resultVariableName = (String) jobHeaders.get("resultVariable");
				resultPayload.put(resultVariableName, singleResult.getEntryMap());
			}
			
			client.newCompleteCommand(job.getKey())
				.variables(resultPayload)
				.send()
				.join();				
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			client.newFailCommand(job.getKey())
				.retries(0)
				.send()
				.join();
		}
	}

	private DelegateExecution wrapDelegateExecution(ActivatedJob job) {
		return new ZeebeJobDelegateExecution(job);
	}

	private JavaDelegate loadJavaDelegate(String delegateName) {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Class<? extends JavaDelegate> clazz = (Class<? extends JavaDelegate>) contextClassLoader.loadClass(delegateName);
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
