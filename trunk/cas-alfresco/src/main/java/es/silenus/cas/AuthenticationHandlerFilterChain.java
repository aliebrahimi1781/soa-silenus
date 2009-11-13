package es.silenus.cas;

import org.jasig.cas.authentication.principal.Credentials;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.util.List;
import java.util.ArrayList;

/**
 * CAS Authentication handler filter, that executes pre and post executions.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 13:04:00
 */
public class AuthenticationHandlerFilterChain implements AuthenticationHandlerFilter {

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(AuthenticationHandlerFilterChain.class);

	/**
	 * The list of autnetication handler filters.
	 */
	protected List<AuthenticationHandlerFilter> filters;


	/**
	 * Constructor.
	 */
	public AuthenticationHandlerFilterChain() {
		filters = new ArrayList<AuthenticationHandlerFilter>();
	}

	/**
	 * Sets the filters.
	 *
	 * @param filters the filters.
	 */
	public void setFilters(final List<AuthenticationHandlerFilter> filters) {
		this.filters.addAll(filters);
	}

	/**
	 * Method to execute before authentication occurs.
	 *
	 * @param credentials the Credentials supplied
	 * @return true if authentication should continue, false otherwise.
	 */
	public boolean preAuthenticate(final Credentials credentials) {

		boolean result = true;

		for(final AuthenticationHandlerFilter filter: filters) {
			if(!filter.preAuthenticate(credentials)) {
				result = false;
				break;
			}
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Pre-Authenticating credentials %s returns %b", credentials, result));
		}

		return result;
	}

	/**
	 * Method to execute after authentication occurs.
	 *
	 * @param credentials the supplied credentials
	 * @param authenticated the result of the authentication attempt.
	 * @return true if the handler should return true, false otherwise.
	 */
	public boolean postAuthenticate(final Credentials credentials, final boolean authenticated) {

		boolean result = authenticated;

		for(final AuthenticationHandlerFilter filter: filters) {
			result = filter.postAuthenticate(credentials, result);
			if(!result) {
				break;
			}
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Post-Authenticating credentials %s returns %b", credentials, result));
		}

		return result;
	}

	/**
	 * Initialize the object after injection of properties.
	 *
	 * @throws Exception if something goes wrong.
	 */
	public void afterPropertiesSet() throws Exception {

	}
}