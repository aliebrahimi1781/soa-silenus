package es.silenus.cas.alfresco;

import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.handler.BadUsernameOrPasswordAuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.authentication.principal.Credentials;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.alfresco.webservice.util.WebServiceFactory;
import org.alfresco.webservice.authentication.AuthenticationResult;
import org.alfresco.webservice.authentication.AuthenticationServiceSoapPort;
import org.alfresco.webservice.authentication.AuthenticationFault;

import java.rmi.RemoteException;
import java.util.List;

import es.silenus.cas.AuthenticationHandlerFilterChain;
import es.silenus.cas.AuthenticationHandlerFilter;


/**
 * Authentication handler that relies on alfresco internal authentication system,
 * accessed through a WS.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 16:47:49
 */
public class AlfrescoAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler implements InitializingBean {

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AlfrescoAuthenticationHandler.class);

	/**
	 * The endpoint address.
	 */
	private String endpointAddress;

	/**
	 * The user details service.
	 */
	private UserDetailsService userDetailsService;

	/**
	 * The authentication service.
	 */
	private AuthenticationServiceSoapPort authenticationService;

	/**
	 * The chain.
	 */
	private AuthenticationHandlerFilterChain chain;

	/**
	 * Constructor.
	 */
	public AlfrescoAuthenticationHandler() {
		chain = new AuthenticationHandlerFilterChain();
	}

	/**
	 * Sets the endpoint address.
	 *
	 * @param endpointAddress the endpoint address.
	 */
	public void setEndpointAddress(final String endpointAddress) {
		this.endpointAddress = endpointAddress;
	}

	/**
	 * Sets the user details service.
	 *
	 * @param userDetailsService  the user details service.
	 */
	public void setUserDetailsService(final UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Sets the filters.
	 *
	 * @param filters the filters.
	 */
	public void setFilters(final List<AuthenticationHandlerFilter> filters) {
		this.chain.setFilters(filters);
	}

	/**
	 * Method to execute before authentication occurs.
	 *
	 * @param credentials the Credentials supplied
	 * @return true if authentication should continue, false otherwise.
	 */
	protected boolean preAuthenticate(final Credentials credentials) {
		return chain.preAuthenticate(credentials);
	}

	/**
	 * Method to execute after authentication occurs.
	 *
	 * @param credentials the supplied credentials
	 * @param authenticated the result of the authentication attempt.
	 * @return true if the handler should return true, false otherwise.
	 */
	protected boolean postAuthenticate(final Credentials credentials, final boolean authenticated) {
		return chain.postAuthenticate(credentials, authenticated);
	}

	/**
	 * Abstract convenience method that assumes the credentials passed in are a
	 * subclass of UsernamePasswordCredentials.
	 *
	 * @param credentials the credentials representing the Username and Password
	 * presented to CAS
	 * @return true if the credentials are authentic, false otherwise.
	 */
	public boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) throws BadUsernameOrPasswordAuthenticationException {
		AuthenticationResult result = null;

		try {
			String userName = credentials.getUsername();

			UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
			if(userDetails != null) {
				result = authenticationService.startSession(userName, credentials.getPassword());

				if(LOG.isDebugEnabled()) {
					LOG.debug(String.format("Authenticated user %s with ticket %s and session %s",
							userName,
							result.getTicket(),
							result.getSessionid()));
				}

				return result.getTicket() != null;
			}
		} catch(UsernameNotFoundException e) {
			// hide this
		} catch(AuthenticationFault e) {
			// hide this
		} catch (Throwable e) {
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("Error authenticating user: %s", credentials.getUsername()), e);
			}
		} finally {
			if(result != null) {
				try {
					authenticationService.endSession(result.getTicket());
				} catch (RemoteException e) {
					// Do nothing
				}
			}
		}


		return false;
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
	public void afterPropertiesSet() throws Exception {
		WebServiceFactory.setEndpointAddress(endpointAddress);
		authenticationService = WebServiceFactory.getAuthenticationService();
	}
}
