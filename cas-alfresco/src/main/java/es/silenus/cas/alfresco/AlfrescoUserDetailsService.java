package es.silenus.cas.alfresco;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.accesscontrol.SiblingAuthorityFilter;
import org.alfresco.webservice.accesscontrol.AccessControlServiceSoapPort;
import org.alfresco.webservice.administration.AdministrationServiceSoapPort;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.security.userdetails.User;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

import java.util.Map;
import java.util.HashMap;
import java.rmi.RemoteException;

/**
 * User details service for CAS.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 16:49:19
 */
public class AlfrescoUserDetailsService implements UserDetailsService {

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AlfrescoUserDetailsService.class);

	/**
	 * The organization name.
	 */
	public static final String PROP_USER_ORG = Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "organization");

	/**
	 * No groups.
	 */
	public static final String[] NO_GROUPS = new String[0];

	/**
	 * The endpoint address.
	 */
	private String endpointAddress;

	/**
	 * The user.
	 */
	private String endpointUser;

	/**
	 * The password.
	 */
	private String endpointPassword;

	/**
	 * The authority type.
	 */
	private AlfrescoAuthorityType authorityType;

	/**
	 * The role mapping.
	 */
	private Map<String, String> roleMapping;

	/**
	 * The administration service.
	 */
	private AdministrationServiceSoapPort administrationService;

	/**
	 * The access control service.
	 */
	private AccessControlServiceSoapPort accessControlService;


	/**
	 * Constructor.
	 */
	public AlfrescoUserDetailsService() {
		roleMapping = new HashMap<String, String>();
		authorityType = AlfrescoAuthorityType.GROUP;
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
	 * Sets the user.
	 *
	 * @param endpointUser the user.
	 */
	public void setEndpointUser(final String endpointUser) {
		this.endpointUser = endpointUser;
	}

	/**
	 * Sets the password.
	 *
	 * @param endpointPassword the endpoint password.
	 */
	public void setEndpointPassword(final String endpointPassword) {
		this.endpointPassword = endpointPassword;
	}

	/**
	 * Sets the role mapping.
	 *
	 * @param roleMapping the role mapping.
	 */
	public void setRoleMapping(final Map<String, String> roleMapping) {
		this.roleMapping = roleMapping;
	}

	/**
	 * Sets the authority type.
	 *
	 * @param authorityType the authority type.
	 */
	public void setAuthorityType(final String authorityType) {
		this.authorityType = AlfrescoAuthorityType.valueOf(authorityType);
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

		administrationService = WebServiceFactory.getAdministrationService();
		accessControlService = WebServiceFactory.getAccessControlService();
	}

	/**
	 * Locates the user based on the username. In the actual implementation, the search may possibly be case
	 * insensitive, or case insensitive depending on how the implementaion instance is configured. In this case, the
	 * <code>UserDetails</code> object that comes back may have a username that is of a different case than what was
	 * actually requested..
	 *
	 * @param username the username presented to the {@link org.springframework.security.providers.dao.DaoAuthenticationProvider}
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws org.springframework.security.userdetails.UsernameNotFoundException
	 *          if the user could not be found or the user has no GrantedAuthority
	 * @throws org.springframework.dao.DataAccessException
	 *          if user could not be found for a repository-specific reason
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		if(AuthenticationUtils.getTicket() == null) {
			refreshSession();
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Loading user: %s", username));
		}

		try {
			org.alfresco.webservice.administration.UserDetails details = administrationService.getUser(username);

			if(details == null) {
				throw new UsernameNotFoundException(String.format("User \"%s\" not found", username));
			}

			return mapToUser(details);

		} catch (Throwable e) {
			throw new UsernameNotFoundException(String.format("User \"%s\" not found", username), e);
		}
	}

	/**
	 * Refresh web service connection.
	 */
	protected void refreshSession() {
		try {
			AuthenticationUtils.endSession();
			AuthenticationUtils.startSession(endpointUser, endpointPassword);
		} catch (Throwable e) {
			LOG.error(String.format("Error refreshing session at %s with user %s", endpointAddress,
					endpointUser), e);
		}
	}


	/**
	 * Maps an alfresco user to an internal one.
	 *
	 * @param alfrescoUser the alfresco user.
	 * @return the internal user.
	 */
	protected UserDetails mapToUser(final org.alfresco.webservice.administration.UserDetails alfrescoUser) {

		String[] authorityNames;

		switch(authorityType) {
			case GROUP:
				authorityNames = getUserGroups(alfrescoUser.getUserName());
				break;
			case ROLE:
				authorityNames = getUserRoles(alfrescoUser.getUserName());
				break;
			default:
				authorityNames = new String[0];
				break;
		}

		GrantedAuthority[] authorities = new GrantedAuthority[authorityNames.length];

		String alfrescoAuthority;
		String role;

		for(int i = 0; i < authorityNames.length; ++i) {
			alfrescoAuthority = authorityNames[i];

			if(authorityType.hasPrefix(alfrescoAuthority)) {
				alfrescoAuthority = authorityType.removePrefix(alfrescoAuthority);
			}

			role = roleMapping.get(alfrescoAuthority);
			if(role == null) {
				role = alfrescoAuthority;
			}
			authorities[i] = new GrantedAuthorityImpl(role);
		}

		String password = "";
		boolean enabled = true;

		return new User(alfrescoUser.getUserName(), password, enabled, enabled, enabled, enabled, authorities);
	}



	/**
	 * Retrieves the user groups.
	 *
	 * @param username the user name.
	 * @return the groups.
	 */
	public String[] getUserGroups(final String username) {
		if(AuthenticationUtils.getTicket() == null) {
			refreshSession();
		}

		SiblingAuthorityFilter filter = new SiblingAuthorityFilter();
		filter.setAuthorityType("GROUP");
		filter.setImmediate(true);

		try {
			return accessControlService.getParentAuthorities(username, filter);
		} catch (RemoteException e) {
			LOG.error(String.format("Error retrieving groups of user \"%s\"", username), e);
		}

		return NO_GROUPS;
	}

	/**
	 * Retrieves the user groups.
	 *
	 * @param username the user name.
	 * @return the groups.
	 */
	public String[] getUserRoles(final String username) {
		if(AuthenticationUtils.getTicket() == null) {
			refreshSession();
		}

		SiblingAuthorityFilter filter = new SiblingAuthorityFilter();
		filter.setAuthorityType("ROLE");
		filter.setImmediate(true);

		try {
			return accessControlService.getParentAuthorities(username, filter);
		} catch (RemoteException e) {
			LOG.error(String.format("Error retrieving roles of user \"%s\"", username), e);
		}

		return NO_GROUPS;
	}
}
