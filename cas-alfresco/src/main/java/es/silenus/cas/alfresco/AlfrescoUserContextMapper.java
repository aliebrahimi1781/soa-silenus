package es.silenus.cas.alfresco;

import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.GrantedAuthority;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;

/**
 * Alfresco user context mapper.
 *
 * @author Mariano Alonso
 * @since 17-sep-2009 16:08:29
 */
public class AlfrescoUserContextMapper implements UserDetailsContextMapper {

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AlfrescoUserContextMapper.class);

	/**
	 * The object classes.
	 */
	private List<String> objectClasses;

	/**
	 * The user password attribute.
	 */
	private String userPasswordAttribute;

	/**
	 * The user first name attribute.
	 */
	private String userFirstNameAttribute;

	/**
	 * The user middle name attribute.
	 */
	private String userMiddleNameAttribute;

	/**
	 * The user last name attribute.
	 */
	private String userLastNameAttribute;

	/**
	 * The user mail attribute.
	 */
	private String userMailAttribute;

	/**
	 * The user home directory.
	 */
	private String userHomeAttribute;

	/**
	 * The organization id attribute.
	 */
	private String organizationIdAttribute;

	/**
	 * The organization name attribute.
	 */
	private String organizationNameAttribute;

	/**
	 * The home prefix.
	 */
	private String homePrefix;


	/**
	 * Constructor.
	 */
	public AlfrescoUserContextMapper() {
		objectClasses = new ArrayList<String>();
	}


	/**
	 * Sets the object classes.
	 *
	 * @param objectClasses the object classes.
	 */
	public void setObjectClasses(final List<String> objectClasses) {
		this.objectClasses = objectClasses;
	}

	/**
	 * Sets the user password attribute.
	 *
	 * @param userPasswordAttribute the user password attribute.
	 */
	public void setUserPasswordAttribute(final String userPasswordAttribute) {
		this.userPasswordAttribute = userPasswordAttribute;
	}

	/**
	 * Sets the user first name attribute.
	 *
	 * @param userFirstNameAttribute the user first name attribute.
	 */
	public void setUserFirstNameAttribute(final String userFirstNameAttribute) {
		this.userFirstNameAttribute = userFirstNameAttribute;
	}

	/**
	 * Sets the user middle name attribute.
	 *
	 * @param userMiddleNameAttribute the user middle name attribute.
	 */
	public void setUserMiddleNameAttribute(final String userMiddleNameAttribute) {
		this.userMiddleNameAttribute = userMiddleNameAttribute;
	}

	/**
	 * Sets the user last name attribute.
	 *
	 * @param userLastNameAttribute the user last name attribute.
	 */
	public void setUserLastNameAttribute(final String userLastNameAttribute) {
		this.userLastNameAttribute = userLastNameAttribute;
	}

	/**
	 * Sets the user mail attribute.
	 *
	 * @param userMailAttribute the user mail attribute.
	 */
	public void setUserMailAttribute(final String userMailAttribute) {
		this.userMailAttribute = userMailAttribute;
	}

	/**
	 * Sets the user home attribute.
	 *
	 * @param userHomeAttribute the user home attribute.
	 */
	public void setUserHomeAttribute(final String userHomeAttribute) {
		this.userHomeAttribute = userHomeAttribute;
	}

	/**
	 * Sets the organization id attribute.
	 *
	 * @param organizationIdAttribute the organization id attribute.
	 */
	public void setOrganizationIdAttribute(final String organizationIdAttribute) {
		this.organizationIdAttribute = organizationIdAttribute;
	}

	/**
	 * Sets the organization name attribute.
	 *
	 * @param organizationNameAttribute the organization name attribute.
	 */
	public void setOrganizationNameAttribute(final String organizationNameAttribute) {
		this.organizationNameAttribute = organizationNameAttribute;
	}

	/**
	 * Sets the home prefix.
	 *
	 * @param homePrefix the home prefix.
	 */
	public void setHomePrefix(final String homePrefix) {
		this.homePrefix = homePrefix;
	}

	/**
	 * Creates a fully populated UserDetails object for use by the security framework.
	 *
	 * @param ctx			 the context object which contains the user information.
	 * @param username	the user's supplied login name.
	 * @param authority the list of authorities which the user should be given.
	 * @return the user object.
	 */
	public UserDetails mapUserFromContext(final DirContextOperations ctx, final String username, final GrantedAuthority[] authority) {

		AlfrescoUserDetails user = new AlfrescoUserDetails();
		user.setUsername(username);
		user.setAuthorities(authority);

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Mapping Context To Alfresco User %s", ctx));
		}

		if(StringUtils.hasLength(userPasswordAttribute)) {
			String userPassword = new String((byte[])ctx.getObjectAttribute(userPasswordAttribute));

			user.setPassword(userPassword);
		}

		if(StringUtils.hasLength(userFirstNameAttribute)) {
			user.setFirstName(ctx.getStringAttribute(userFirstNameAttribute));
		}

		if(StringUtils.hasLength(userMiddleNameAttribute)) {
			user.setLastName(ctx.getStringAttribute(userMiddleNameAttribute));
		}

		if(StringUtils.hasLength(userLastNameAttribute)) {
			user.setLastName(ctx.getStringAttribute(userLastNameAttribute));
		}

		if(StringUtils.hasLength(userMailAttribute)) {
			user.setEmail(ctx.getStringAttribute(userMailAttribute));
		}

		if(StringUtils.hasLength(userHomeAttribute)) {
			if(homePrefix != null) {
				user.setHomeFolder(homePrefix + ctx.getStringAttribute(userHomeAttribute));
			} else {
				user.setHomeFolder(ctx.getStringAttribute(userHomeAttribute));
			}
		}

		if(StringUtils.hasLength(organizationIdAttribute)) {
			user.setOrganizationId(ctx.getStringAttribute(organizationIdAttribute));
		}

		if(StringUtils.hasLength(organizationNameAttribute)) {
			user.setOrganizationName(ctx.getStringAttribute(organizationNameAttribute));
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Mapped Context To Alfresco User %s", user));
		}

		return user;
	}

	/**
	 * Reverse of the above operation. Populates a context object from the supplied user object.
	 * Called when saving a user, for example.
	 */
	public void mapUserToContext(final UserDetails user, final DirContextAdapter ctx) {

		AlfrescoUserDetails theUser = (AlfrescoUserDetails)user;

		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Mapping User To Context %s", user));
		}


		ctx.setAttributeValues("objectClass", objectClasses.toArray());

		String temp;

		if(StringUtils.hasLength(userPasswordAttribute)) {
			temp = theUser.getPassword();
			if(StringUtils.hasLength(temp)) {
				ctx.setAttributeValue(userPasswordAttribute, temp.getBytes());
			}
		}

		if(StringUtils.hasLength(userFirstNameAttribute)) {
			temp = theUser.getFirstName();
			if(StringUtils.hasLength(temp)) {
				ctx.setAttributeValue(userFirstNameAttribute, temp);
			}
		}

		if(StringUtils.hasLength(userMiddleNameAttribute)) {
			temp = theUser.getMiddleName();
			if(StringUtils.hasLength(temp)) {
				ctx.setAttributeValue(userMiddleNameAttribute, temp);
			}
		}

		if(StringUtils.hasLength(userLastNameAttribute)) {
			temp = theUser.getLastName();
			if(StringUtils.hasLength(temp)) {
				ctx.setAttributeValue(userLastNameAttribute, temp);
			}
		}

		if(StringUtils.hasLength(userMailAttribute)) {
			temp = theUser.getEmail();
			if(StringUtils.hasLength(temp)) {
				ctx.setAttributeValue(userMailAttribute, temp);
			}
		}

		if(StringUtils.hasLength(userHomeAttribute)) {
			temp = theUser.getHomeFolder();
			if(StringUtils.hasLength(temp)) {
				ctx.setAttributeValue(userHomeAttribute, temp);
			}
		}

		if(StringUtils.hasLength(organizationIdAttribute)) {
			temp = theUser.getOrganizationId();
			if(StringUtils.hasLength(temp)) {
				ctx.setAttributeValue(organizationIdAttribute, temp);
			}
		}

		if(StringUtils.hasLength(organizationNameAttribute)) {
			temp = theUser.getOrganizationName();
			if(StringUtils.hasLength(temp)) {
				ctx.setAttributeValue(organizationNameAttribute, temp);
			}
		}

	}
}
