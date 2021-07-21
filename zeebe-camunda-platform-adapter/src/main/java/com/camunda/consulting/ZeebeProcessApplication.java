package com.camunda.consulting;

import java.net.URL;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.camunda.bpm.container.impl.deployment.scanning.ProcessApplicationScanningUtil;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;

/**
 * @author Daniel Meyer (Camunda)
 * @author Falko Menge (Camunda)
 */
//@Startup
//@Singleton
//@TransactionManagement(TransactionManagementType.BEAN)
//@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
//@TransactionAttribute(TransactionAttributeType.NEVER)
public class ZeebeProcessApplication {

	protected static String MODULE_NAME_PATH = "java:module/ModuleName";
	protected static String JAVA_APP_APP_NAME_PATH = "java:app/AppName";

	private ZeebeClient zeebeClient;
	private String applicationName;

	private JobWorker jobWorker;
	
	@Inject
	private JobHandler jobHandler;

	@PostConstruct
	public void start() {
		applicationName = lookupEeApplicationName();

		System.out.println("Deploying Zeebe Process Application " + applicationName);
		
		initZeebeClient();
		autoDeployProcesses();
		startJobWorker();
	}

	private void startJobWorker() {
		jobWorker = zeebeClient.newWorker()
			.jobType(applicationName)
			.handler(jobHandler)
			.name(applicationName)
			.open();
	}

	@PreDestroy
	public void stop() {
		closeJobWorker();
		closeZeebeClient();
		
		System.out.println("Undeployed Zeebe Process Application " + applicationName);
	}

	private void closeJobWorker() {
		try {
			jobWorker.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeZeebeClient() {
		try {
			zeebeClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initZeebeClient() {
		zeebeClient = ZeebeClient.newClient();
	}

	@Produces
	public ZeebeClient getZeebeClient() {
		return zeebeClient;
	}

	private void autoDeployProcesses() {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		final URL metafileUrl = contextClassLoader.getResource("META-INF/zeebe-processes.xml");
		final Map<String, byte[]> resources = ProcessApplicationScanningUtil.findResources(contextClassLoader, null,
				metafileUrl);

		resources.entrySet().stream().forEach((resource) -> {
			String resourceName = resource.getKey();
			byte[] resourceBytes = resource.getValue();

			final DeploymentEvent deploymentEvent = zeebeClient.newDeployCommand()
					.addResourceBytes(resourceBytes, resourceName).send().join();

			System.out.println("deployed: " + deploymentEvent);
		});
	}

	protected String lookupEeApplicationName() {

		try {
			InitialContext initialContext = new InitialContext();

			String appName = (String) initialContext.lookup(JAVA_APP_APP_NAME_PATH);
			String moduleName = (String) initialContext.lookup(MODULE_NAME_PATH);

			// make sure that if an EAR carries multiple PAs, they are correctly
			// identified by appName + moduleName
			if (moduleName != null && !moduleName.equals(appName)) {
				return appName + "/" + moduleName;
			} else {
				return appName;
			}
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}
