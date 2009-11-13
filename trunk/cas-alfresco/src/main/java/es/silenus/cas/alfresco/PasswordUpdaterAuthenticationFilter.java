package es.silenus.cas.alfresco;

import es.silenus.cas.AuthenticationHandlerFilter;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.alfresco.webservice.util.WebServiceFactory;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.administration.AdministrationServiceSoapPort;
import org.alfresco.webservice.administration.NewUserDetails;
import org.alfresco.webservice.types.NamedValue;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UserDetails;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Password updater filter that updates internal alfresco user password to guarantee CIFS access.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 13:18:37
 */
public class PasswordUpdaterAuthenticationFilter implements AuthenticationHandlerFilter {

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(PasswordUpdaterAuthenticationFilter.class);

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
	 * The administration service.
	 */
	private AdministrationServiceSoapPort administrationService;

	/**
	 * The user details service.
	 */
	private UserDetailsService userDetailsService;

	/**
	 * The alfresco user to properties mapper.
	 */
	private AlfrescoUserPropertiesMapper alfrescoUserMapper;


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
	 * Sets the user defails service.
	 *
	 * @param userDetailsService the user defails service.
	 */
	public void setUserDetailsService(final UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Sets the alfresco user mapper.
	 *
	 * @param alfrescoUserMapper the alfresco user mapper.
	 */
	public void setAlfrescoUserMapper(final AlfrescoUserPropertiesMapper alfrescoUserMapper) {
		this.alfrescoUserMapper = alfrescoUserMapper;
	}

	/**
	 * Initialize the object after injection of properties.
	 *
	 * @throws Exception if something goes wrong.
	 */
	public void afterPropertiesSet() throws Exception {
		WebServiceFactory.setEndpointAddress(this.endpointAddress);

		administrationService = WebServiceFactory.getAdministrationService();
	}


	/**
	 * Method to execute before authentication occurs.
	 *
	 * @param credentials the Credentials supplied
	 * @return true if authentication should continue, false otherwise.
	 */
	public boolean preAuthenticate(Credentials credentials) {
		if(AuthenticationUtils.getTicket() == null) {
			refreshSession();
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
		if(AuthenticationUtils.getTicket() == null) {
			refreshSession();
		}

		if((credentials instanceof UsernamePasswordCredentials) && authenticated) {

			final UsernamePasswordCredentials userCredentials = (UsernamePasswordCredentials)credentials;

			org.alfresco.webservice.administration.UserDetails user = getUser(userCredentials.getUsername());
			if(user != null) {
				changePassword(userCredentials);
			} else {
				createUser(userCredentials);
			}

		}
		return authenticated;
	}


	/**
	 * Refresh web service connection.
	 */
	private  void refreshSession() {
		try {
			AuthenticationUtils.endSession();
			AuthenticationUtils.startSession(endpointUser, endpointPassword);
		} catch (Throwable e) {
			LOG.error(String.format("Error refreshing session at %s with user %s", endpointAddress,
					endpointUser), e);
		}
	}


	/**
	 * Retrieves an internal alfresco user by its name.
	 *
	 * @param userName the user name.
	 * @return the user details.
	 */
	protected org.alfresco.webservice.administration.UserDetails getUser(final String userName) {
		try {
			return administrationService.getUser(userName);
		} catch (RemoteException e) {
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("User %s not found", userName), e);
			}
		}
		return null;
	}


	/**
	 * Changes user password.
	 *
	 * @param credentials the username and password as credentials.
	 * @return true for success, false for failure.
	 */
	protected boolean changePassword(final UsernamePasswordCredentials credentials) {
		try {
			administrationService.changePassword(credentials.getUsername(), null, credentials.getPassword());

			return true;
		} catch (RemoteException e) {
			LOG.error(String.format("Error changing user %s password", credentials), e);
		}

		return false;
	}


	protected NamedValue[] toProperties(final UsernamePasswordCredentials credentials) {
		ArrayList<NamedValue> properties = new ArrayList<NamedValue>();

		properties.add( Utils.createNamedValue(Constants.PROP_USERNAME, credentials.getUsername()) );

		if(userDetailsService != null && alfrescoUserMapper != null) {
			try {

				UserDetails user = userDetailsService.loadUserByUsername(credentials.getUsername());

				alfrescoUserMapper.map(user, properties);

			} catch(Throwable e) {
				LOG.error(String.format("Error mapping external user \"%s\" to alfresco internal properties", credentials.getUsername()), e);
			}
		}

		return properties.toArray(new NamedValue[properties.size()]);
	}


	/**
	 * Creates a new user.
	 *
	 * @param credentials  the username and password as credentials.
	 * @return the user or null.
	 */
	protected org.alfresco.webservice.administration.UserDetails createUser(final UsernamePasswordCredentials credentials) {

		final NewUserDetails user = new NewUserDetails(credentials.getUsername(), credentials.getPassword(), toProperties(credentials));
		final NewUserDetails[] users = { user };

		try {
			org.alfresco.webservice.administration.UserDetails[] result = administrationService.createUsers(users);

			return result[0];

		} catch (RemoteException e) {
			LOG.error(String.format("Error creating user %s", credentials.getUsername()), e);
		}

		return null;
	}

}
