package es.silenus.cas.ldap.adaptors;

import org.jasig.cas.adaptors.ldap.AbstractLdapUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.util.LdapUtils;
import org.springframework.ldap.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.directory.DirContext;

import es.silenus.cas.AuthenticationHandlerFilterChain;
import es.silenus.cas.AuthenticationHandlerFilter;

import java.util.List;

/**
 * Implementation of an LDAP handler to do a "fast bind." A fast bind skips the
 * normal two step binding process to determine validity by providing before
 * hand the path to the uid.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 12:59:09
 */
public class FastBindLdapAuthenticationHandler extends AbstractLdapUsernamePasswordAuthenticationHandler {

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(FastBindLdapAuthenticationHandler.class);

	/**
	 * The chain.
	 */
	private AuthenticationHandlerFilterChain chain;

	/**
	 * Constructor.
	 */
	public FastBindLdapAuthenticationHandler() {
		super();
		chain = new AuthenticationHandlerFilterChain();
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
	 *                    presented to CAS
	 * @return true if the credentials are authentic, false otherwise.
	 * @throws org.jasig.cas.authentication.handler.AuthenticationException
	 *          if authenticity cannot be determined.
	 */
	protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) throws AuthenticationException {
		DirContext dirContext = null;
		String bindDn = null;
		try {
			bindDn = LdapUtils.getFilterWithValues(getFilter(), credentials.getUsername());

			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("Performing LDAP bind with DN %s and user %s", bindDn, credentials.getUsername()));
			}

			dirContext = this.getContextSource().getContext(bindDn, credentials.getPassword());

			return true;

		} catch (NamingException e) {

			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("Cound not bind DN %s for user %s", bindDn, credentials.getUsername()), e);
			}

			return false;

		} finally {
			if (dirContext != null) {
				LdapUtils.closeContext(dirContext);
			}
		}
	}
}
