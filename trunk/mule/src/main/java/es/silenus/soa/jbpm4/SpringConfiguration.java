package es.silenus.soa.jbpm4;

import org.jbpm.pvm.internal.cfg.ProcessEngineImpl;
import org.jbpm.pvm.internal.env.EnvironmentFactory;
import org.jbpm.pvm.internal.env.EnvironmentImpl;
import org.jbpm.pvm.internal.env.PvmEnvironment;
import org.jbpm.pvm.internal.env.SpringContext;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.ProcessDefinition;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.util.List;
import java.util.Map;

/**
 * The spring JBPM configuration.
 *
 * @author Mariano Alonso
 * @since 10-nov-2009 11:18:12
 */
public class SpringConfiguration extends ProcessEngineImpl implements EnvironmentFactory, ProcessEngine, ApplicationContextAware, InitializingBean {

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SpringConfiguration.class);

	/**
	 * The application context.
	 */
	protected ApplicationContext applicationContext;

	/**
	 * Configuration resource.
	 */
	protected Resource configuration;


	/**
	 * The process engine.
	 */
	private ProcessEngine processEngine;


	/**
	 * Process definitions.
	 */
	private Map<String, Resource> processDefinitions;

	/**
	 * Flag to check the previous process or not when deploying new processes.
	 */
	private boolean clearPreviousProcess;


	/**
	 * Constructor.
	 */
	public SpringConfiguration() {
		clearPreviousProcess = false;
	}


	/**
	 * Set the ApplicationContext that this object runs in.
	 * Normally this call will be used to initialize the object.
	 * <p>Invoked after population of normal bean properties but before an init callback such
	 * as {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet()}
	 * or a custom init-method. Invoked after {@link org.springframework.context.ResourceLoaderAware#setResourceLoader},
	 * {@link org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher} and
	 * {@link org.springframework.context.MessageSourceAware}, if applicable.
	 *
	 * @param applicationContext the ApplicationContext object to be used by this object
	 * @throws org.springframework.context.ApplicationContextException
	 *          in case of context initialization errors
	 * @throws org.springframework.beans.BeansException
	 *          if thrown by application context methods
	 * @see org.springframework.beans.factory.BeanInitializationException
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Sets the configuration.
	 *
	 * @param configuration the configuration.
	 */
	public void setConfiguration(final Resource configuration) {
		this.configuration = configuration;
	}

	/**
	 * Sets the process definitions.
	 *
	 * @param processDefinitions the process definitions.
	 */
	public void setProcessDefinitions(final Map<String, Resource> processDefinitions) {
		this.processDefinitions = processDefinitions;
	}


	/**
	 * Sets the clear previous process flag.
	 *
	 * @param clearPreviousProcess the clear previous process flag.
	 */
	public void setClearPreviousProcess(boolean clearPreviousProcess) {
		this.clearPreviousProcess = clearPreviousProcess;
	}

	/**
	 * Invoked by a BeanFactory after it has set all bean properties supplied
	 * (and satisfied BeanFactoryAware and ApplicationContextAware).
	 * <p>This method allows the bean instance to perform initialization only
	 * possible when all bean properties have been set and to throw an
	 * exception in the event of misconfiguration.
	 *
	 * @throws Exception in the event of misconfiguration (such
	 *                   as failure to set an essential property) or if initialization fails.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if(configuration == null) {
			throw new Exception("Cannot instantiate JBPM service, no resource specified as configuration");
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("Starting Spring JBPM configuration...");
		}

		// Configure
		setInputStream(configuration.getInputStream());

		if(LOG.isDebugEnabled()) {
			LOG.debug("Building process engine...");
		}

		// Build engine
		processEngine = super.buildProcessEngine();

		if(processDefinitions != null && processDefinitions.size() > 0) {
			RepositoryService repositoryService = super.getRepositoryService();

			List<ProcessDefinition> deployedProcesses;

			for(Map.Entry<String, Resource> e: processDefinitions.entrySet()) {
				//
				deployedProcesses = repositoryService
						.createProcessDefinitionQuery()
						.processDefinitionKey(e.getKey())
						.list();

				if(clearPreviousProcess) {

					for(ProcessDefinition p: deployedProcesses) {
						if(LOG.isDebugEnabled()) {
							LOG.debug(String.format("Undeploying process [%s,%s,%d]...", p.getName(), p.getKey(), p.getVersion()));
						}
				 		repositoryService.deleteDeploymentCascade(p.getDeploymentId());
					}

					if(LOG.isDebugEnabled()) {
						LOG.debug(String.format("Deploying process [%s, %s]...", e.getKey(), e.getValue().getFilename()));
					}

					repositoryService
							.createDeployment()
							.setName(e.getKey())
							.addResourceFromInputStream(e.getValue().getFilename(), e.getValue().getInputStream())
							.deploy();
				} else {
					if(processDefinitions.size() == 0) {

						if(LOG.isDebugEnabled()) {
							LOG.debug(String.format("Deploying process [%s, %s]...", e.getKey(), e.getValue().getFilename()));
						}

						repositoryService
								.createDeployment()
								.setName(e.getKey())
								.addResourceFromInputStream(e.getValue().getFilename(), e.getValue().getInputStream())
								.deploy();

					}

				}




			}
		}


	}


	@Override
	public ProcessEngine buildProcessEngine() {
		if(processEngine == null) {
			processEngine = super.buildProcessEngine();
		}
		return processEngine;
	}

	public EnvironmentImpl openEnvironment() {
		PvmEnvironment environment = new PvmEnvironment(this);

		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("opening jbpm-spring %s" + environment));
		}

		environment.setContext(new SpringContext(applicationContext));

		installAuthenticatedUserId(environment);
		installProcessEngineContext(environment);
		installTransactionContext(environment);

		return environment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> type) {
		String[] names = applicationContext.getBeanNamesForType(type);
		if (names.length == 1) {
			return (T) applicationContext.getBean(names[0]);
		}

		return super.get(type);
	}

	@Override
	public Object get(String key) {
		if (applicationContext.containsBean(key)) {
			return applicationContext.getBean(key);
		}

		return super.get(key);
	}
}
