package es.silenus.alfresco.authentication.ldap;

import org.alfresco.repo.security.authentication.*;
import org.alfresco.repo.security.authentication.ldap.LDAPInitialDirContextFactory;
import org.alfresco.repo.management.subsystems.ActivateableBean;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.naming.directory.InitialDirContext;
import javax.naming.NamingException;

/**
 * LDAP Authentication component.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 12:33:11
 */
public class AuthenticationComponentImpl extends AbstractAuthenticationComponent implements ActivateableBean {

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AuthenticationComponentImpl.class);

	/**
	 * Escape commas during bind.
	 */
	private boolean escapeCommasInBind;

	/**
	 * Escape commas in uid.
	 */
	private boolean escapeCommasInUid;

	/**
	 * Activatable flag.
	 */
	private boolean active;

	/**
	 * The user name format.
	 */
	private String userNameFormat;

	/**
	 * The initial context factory.
	 */
	private LDAPInitialDirContextFactory ldapInitialContextFactory;


	/**
	 * The authentication DAO.
	 */
	private MutableAuthenticationDao authenticationDao;


	/**
	 * Constructor.
	 */
	public AuthenticationComponentImpl() {
		super();
		escapeCommasInBind = false;
		escapeCommasInUid = false;
		active = true;
	}

	/**
	 * Sets the LDAP iinitial directory context factory.
	 *
	 * @param ldapInitialDirContextFactory  the LDAP iinitial directory context factory.
	 */
	public void setLDAPInitialDirContextFactory(final LDAPInitialDirContextFactory ldapInitialDirContextFactory) {
		this.ldapInitialContextFactory = ldapInitialDirContextFactory;
	}

	/**
	 * Sets the user name format.
	 *
	 * @param userNameFormat the user name format.
	 */
	public void setUserNameFormat(final String userNameFormat) {
		this.userNameFormat = userNameFormat;
	}

	/**
	 * Set the escape commas in bind flag.
	 *
	 * @param escapeCommasInBind the escape commas in bind flag.
	 */
	public void setEscapeCommasInBind(boolean escapeCommasInBind) {
		this.escapeCommasInBind = escapeCommasInBind;
	}

	/**
	 * Set the escape commas in uid flag.
	 *
	 * @param escapeCommasInUid the escape commas in uid flag.
	 */
	public void setEscapeCommasInUid(boolean escapeCommasInUid) {
		this.escapeCommasInUid = escapeCommasInUid;
	}

	/**
	 * Sets the active flag.
	 *
	 * @param active the active flag.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Sets the authentication DAO.
	 *
	 * @param authenticationDao the authentication DAO.
	 */
	public void setAuthenticationDao(final MutableAuthenticationDao authenticationDao) {
		this.authenticationDao = authenticationDao;
	}

	/**
     * Determines whether this bean is active.
     *
     * @return <code>true</code> if this bean is active
     */
	public boolean isActive() {
		return this.active;
	}


	/**
	 * Implement the authentication method
	 */
	@SuppressWarnings({"ThrowFromFinallyBlock"})
	protected void authenticateImpl(String userName, char[] password) throws AuthenticationException {
		InitialDirContext ctx = null;
		try {

			final String principal = String.format(userNameFormat, escapeUserName(userName, escapeCommasInBind));

			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("Authenticating user %s [%s] ...", userName, principal));
			}

			ctx = ldapInitialContextFactory.getInitialDirContext(principal, new String(password));

			String espapedUserName = escapeUserName(userName, escapeCommasInUid);

			// Create user if not exists and update its password
			createIfNotExists(espapedUserName, password);

			// Authentication has been successful.
			// Set the current user, they are now authenticated.
			setCurrentUser(espapedUserName);

		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					clearCurrentSecurityContext();
					throw new AuthenticationException("Failed to close connection", e);
				}
			}
		}
	}

	/**
	 * Creates a user, internal alfresco one, when the given user is just an external one.
	 *
	 * @param userName the user name.
	 * @param password the password.
	 */
	private void createIfNotExists(String userName, char[] password) {
		try  {
			// Act as part of the system
			setCurrentUser(AuthenticationUtil.SYSTEM_USER_NAME);

			if(!authenticationDao.userExists(userName)) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(String.format("User %s does not exist... we will create it", userName));
				}
				authenticationDao.createUser(userName, password);
			} else {
				if(LOG.isDebugEnabled()) {
					LOG.debug(String.format("User %s exists... we will update the password", userName));
				}
				//save the password in the alfresco user in MD4
				authenticationDao.updateUser(userName, password);
			}
		} catch(Throwable t) {
			LOG.error(String.format("Caught error during user creation: %s", userName), t);
		}
	}

	/**
	 * Escapes user name.
	 *
	 * @param userName the user name.
	 * @param escape the escape flag.
	 * @return the escaped user name.
	 */
	private static String escapeUserName(String userName, boolean escape) {
		if (escape) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < userName.length(); i++) {
				char c = userName.charAt(i);
				if (c == ',') {
					sb.append('\\');
				}
				sb.append(c);
			}
			return sb.toString();

		} else {
			return userName;
		}

	}

	/**
	 * Allow guest login?.
	 *
	 * @return true if allowed, false otherwise.
	 */
	@Override
	@SuppressWarnings({"ThrowFromFinallyBlock"})
	protected boolean implementationAllowsGuestLogin() {
		InitialDirContext ctx = null;
		try {
			ctx = ldapInitialContextFactory.getDefaultIntialDirContext();
			return true;

		} catch (Exception e) {
			return false;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					throw new AuthenticationException("Failed to close connection", e);
				}
			}
		}
	}

}
