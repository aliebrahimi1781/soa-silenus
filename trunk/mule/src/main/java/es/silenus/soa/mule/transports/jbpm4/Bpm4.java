package es.silenus.soa.mule.transports.jbpm4;

import org.mule.transport.bpm.BPMS;
import org.mule.transport.bpm.MessageService;
import org.mule.api.DefaultMuleException;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessInstance;
import org.jbpm.pvm.internal.env.EnvironmentFactory;
import org.jbpm.pvm.internal.env.EnvironmentImpl;
import org.springframework.beans.factory.InitializingBean;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.util.Map;
import java.util.HashMap;

/**
 * BPM component for JBoss jBPM 4.x.
 *
 * @author Mariano Alonso
 * @since 10-nov-2009 10:46:01
 */
public class Bpm4 implements BPMS, InitializingBean {

	/**
	 * Logger for this class.
 	 */
	private static final Log LOG = LogFactory.getLog(Bpm4.class);

	/**
	 * The process engine.
	 */
	protected ProcessEngine processEngine;

	/**
	 * The message service.
	 */
	private MessageService messageService;

	/**
	 * The execution service.
	 */
	private ExecutionService executionService;

	/**
	 * Sets the process engine.
	 *
	 * @param processEngine the process engine.
	 */
	public void setProcessEngine(final ProcessEngine processEngine) {
		this.processEngine = processEngine;
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
		executionService = processEngine.getExecutionService();

	}

	/**
	 * {@link org.mule.transport.bpm.MessageService} contains a callback method used to generate Mule messages from your process.
	 * This method is REQUIRED.
	 *
	 * @param msgService An interface within Mule which the BPMS may call to generate
	 *                   Mule messages.
	 */
	@Override
	public void setMessageService(final MessageService msgService) {
		messageService = msgService;
	}


	@SuppressWarnings({"unchecked"})
	protected Map<String, Object> buildProcessContext(final Map processVariables) {
		Map<String, Object> processContext = new HashMap<String, Object>();
		if(processVariables != null && processVariables.size() > 0) {
			processContext.putAll(processVariables);
		}

		/*

		// TODO: make not persistent variables ? put then in spring

		// Add more variables here
		if(messageService != null) {
			processContext.put("messageService", messageService);
		}
		*/
		return processContext;
	}


	protected EnvironmentFactory getEnvironmentFactory() {
		return ((EnvironmentFactory)processEngine);
	}


	/**
	 * Start a new process.
	 * This method is REQUIRED.
	 *
	 * @param processType			- the type of process to start
	 * @param processVariables - optional process variables/parameters to set
	 * @return an object representing the new process
	 */
	@Override
	public Object startProcess(Object processType, Object transition, Map processVariables) throws Exception {

		String processKey = (String)processType;

		EnvironmentImpl environment = null;
		ProcessInstance result = null;

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Starting process [%s] with transition [%s] and variables [%s]", processType, transition, processVariables));
		}

		try {
			environment = getEnvironmentFactory().openEnvironment();


			Map<String, Object> processContext = buildProcessContext(processVariables);
			result = executionService.startProcessInstanceByKey(processKey, processContext);

			if(!result.isEnded()) {
				if(transition != null) {
					executionService.signalExecutionById(result.getId(), (String)transition);
				} else {
					executionService.signalExecutionById(result.getId());
				}
			} else {
				if(LOG.isInfoEnabled()) {
					LOG.info(String.format("Process has ended [%s]", result));
				}
			}

		} catch(Throwable e) {
			throw new DefaultMuleException(String.format("Error starting process %s", processType), e);
		} finally {
			if(environment != null) {
				environment.close();
			}
		}

		return result;
	}

	/**
	 * Advance an already-running process.
	 * This method is REQUIRED.
	 *
	 * @param processId				- an ID which identifies the running process
	 * @param transition			 - optionally specify which transition to take from the
	 *                         current state
	 * @param processVariables - optional process variables/parameters to set
	 * @return an object representing the process in its new (i.e., advanced) state
	 */
	@Override
	public Object advanceProcess(Object processId, Object transition, Map processVariables) throws Exception {


		EnvironmentImpl environment = null;
		ProcessInstance result = null;

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Advancing process [%s] with transition [%s] and variables [%s]", processId, transition, processVariables));
		}

		try {
			environment = getEnvironmentFactory().openEnvironment();


			result = executionService.findProcessInstanceById((String)processId);

			if(result != null && !result.isEnded()) {

				Map<String, Object> processContext = buildProcessContext(processVariables);
				executionService.setVariables(result.getId(), processContext);

				if(transition != null) {
					executionService.signalExecutionById(result.getId(), (String)transition);
				} else {
					executionService.signalExecutionById(result.getId());
				}
			} else {
				if(LOG.isInfoEnabled()) {
					LOG.info(String.format("Process not found or has ended [%s]", result));
				}
			}
		} finally {
			if(environment != null) {
				environment.close();
			}
		}

		return result;
	}

	/**
	 * Update the variables/parameters for an already-running process.
	 * This method is OPTIONAL.
	 *
	 * @param processId				- an ID which identifies the running process
	 * @param processVariables - process variables/parameters to set
	 * @return an object representing the process in its new (i.e., updated) state
	 */
	@Override
	public Object updateProcess(Object processId, Map processVariables) throws Exception {

		EnvironmentImpl environment = null;
		ProcessInstance result = null;

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Updating process [%s] with variables [%s]", processId, processVariables));
		}

		try {
			environment = getEnvironmentFactory().openEnvironment();

			result = executionService.findProcessInstanceById((String)processId);

			if(result != null) {
				Map<String, Object> processContext = buildProcessContext(processVariables);
				executionService.setVariables(result.getId(), processContext);
			}

		} finally {
			if(environment != null) {
				environment.close();
			}
		}

		return result;
	}

	/**
	 * Abort a running process (end abnormally).
	 * This method is OPTIONAL.
	 *
	 * @param processId - an ID which identifies the running process
	 */
	@Override
	public void abortProcess(Object processId) throws Exception {

		EnvironmentImpl environment = null;
		ProcessInstance result = null;

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Aborting process [%s]", processId));
		}

		try {
			environment = getEnvironmentFactory().openEnvironment();

			result = executionService.findProcessInstanceById((String)processId);
			if(result != null) {
				executionService.deleteProcessInstanceCascade(result.getId());
			}

		} finally {
			if(environment != null) {
				environment.close();
			}
		}
	}

	/**
	 * Looks up an already-running process.
	 * This method is OPTIONAL.
	 *
	 * @return an object representing the process
	 */
	@Override
	public Object lookupProcess(Object processId) throws Exception {

		EnvironmentImpl environment = null;
		ProcessInstance result = null;

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Looked up process [%s]", processId));
		}

		try {
			environment = getEnvironmentFactory().openEnvironment();

			result = executionService.findProcessInstanceById((String)processId);
		} finally {
			if(environment != null) {
				environment.close();
			}
		}

		return result;
	}

	/**
	 * @return an ID which identifies the given process.
	 *         This method is OPTIONAL.
	 */
	@Override
	public Object getId(Object process) throws Exception {

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Retrieving process id [%s]", process));
		}

		ProcessInstance result = (ProcessInstance)process;
		if(result != null) {
			return result.getId();
		}
		return null;
	}

	/**
	 * @return the current state of the given process.
	 *         This method is OPTIONAL.
	 */
	@Override
	public Object getState(Object process) throws Exception {

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Retrieving process state [%s]", process));
		}

		ProcessInstance result = (ProcessInstance)process;
		if(result != null) {

			return result.getState();
		}

		return null;
	}

	/**
	 * @return true if the given process has ended.
	 *         This method is OPTIONAL.
	 */
	@Override
	public boolean hasEnded(Object process) throws Exception {
		ProcessInstance result = (ProcessInstance)process;
		return result == null || result.isEnded();
	}

	/**
	 * @return true if the object is a valid process
	 *         This method is OPTIONAL.
	 */
	@Override
	public boolean isProcess(Object obj) throws Exception {
		return obj instanceof ProcessInstance;
	}


}
