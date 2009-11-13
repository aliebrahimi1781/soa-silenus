package es.silenus.cas;

import org.jasig.cas.authentication.principal.Credentials;
import org.springframework.beans.factory.InitializingBean;

/**
 * CAS Authentication handler filter, that executes pre and post executions.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 13:04:00
 */
public interface AuthenticationHandlerFilter extends InitializingBean {

	/**
	 * Method to execute before authentication occurs.
	 *
	 * @param credentials the Credentials supplied
	 * @return true if authentication should continue, false otherwise.
	 */
	public boolean preAuthenticate(final Credentials credentials);

	/**
	 * Method to execute after authentication occurs.
	 *
	 * @param credentials the supplied credentials
	 * @param authenticated the result of the authentication attempt.
	 * @return true if the handler should return true, false otherwise.
	 */
	public boolean postAuthenticate(final Credentials credentials, final boolean authenticated);
}
