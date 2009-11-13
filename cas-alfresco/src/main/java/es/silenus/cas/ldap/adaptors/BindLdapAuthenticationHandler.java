package es.silenus.cas.ldap.adaptors;

import org.jasig.cas.authentication.principal.Credentials;
import es.silenus.cas.AuthenticationHandlerFilterChain;
import es.silenus.cas.AuthenticationHandlerFilter;

import java.util.List;

/**
 * Handler to do LDAP bind.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 13:00:41
 */
public class BindLdapAuthenticationHandler extends org.jasig.cas.adaptors.ldap.BindLdapAuthenticationHandler {

	/**
	 * The chain.
	 */
	private AuthenticationHandlerFilterChain chain;

	/**
	 * Constructor.
	 */
	public BindLdapAuthenticationHandler() {
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

}
