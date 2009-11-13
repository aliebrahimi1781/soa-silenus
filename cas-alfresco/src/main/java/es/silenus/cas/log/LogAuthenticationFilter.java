package es.silenus.cas.log;

import es.silenus.cas.AuthenticationHandlerFilter;
import org.jasig.cas.authentication.principal.Credentials;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Authentication handler filter that simply logs user information.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 17:40:42
 */
public class LogAuthenticationFilter implements AuthenticationHandlerFilter {

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(LogAuthenticationFilter.class);

	/**
	 * Method to execute before authentication occurs.
	 *
	 * @param credentials the Credentials supplied
	 * @return true if authentication should continue, false otherwise.
	 */
	public boolean preAuthenticate(Credentials credentials) {
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Pre-Authenticating user %s", credentials));
		}
		return true;
	}

	/**
	 * Method to execute after authentication occurs.
	 *
	 * @param credentials	 the supplied credentials
	 * @param authenticated the result of the authentication attempt.
	 * @return true if the handler should return true, false otherwise.
	 */
	public boolean postAuthenticate(Credentials credentials, boolean authenticated) {

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Post-Authenticating user %s with result %b", credentials, authenticated));
		}

		return authenticated;
	}

	/**
	 * Initialize the object after injection of properties.
	 *
	 * @throws Exception if something goes wrong.
	 */
	public void afterPropertiesSet() throws Exception {
	}
}
