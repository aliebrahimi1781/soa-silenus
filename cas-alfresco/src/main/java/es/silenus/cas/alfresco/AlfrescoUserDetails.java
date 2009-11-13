package es.silenus.cas.alfresco;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import java.util.Arrays;

/**
 * Alfresco user details.
 *
 * @author Mariano Alonso
 * @since 17-sep-2009 16:40:32
 */
public class AlfrescoUserDetails implements UserDetails {

	/**
	 * The user name.
	 */
	private String username;

	/**
	 * The password.
	 */
	private String password;

	/**
	 * The first name.
	 */
	private String firstName;

	/**
	 * The middle name.
	 */
	private String middleName;

	/**
	 * The last name.
	 */
	private String lastName;

	/**
	 * The email.
	 */
	private String email;

	/**
	 * The home folder.
	 */
	private String homeFolder;

	/**
	 * The organization id.
	 */
	private String organizationId;

	/**
	 * The organization name.
	 */
	private String organizationName;

	/**
	 * The authorities.
	 */
	private GrantedAuthority[] authorities;

	/**
	 * The non expired flag.
	 */
	private boolean accountNonExpired;

	/**
	 * The non locked flag.
	 */
	private boolean accountNonLocked;

	/**
	 * The non expired flag.
	 */
	private boolean credentialsNonExpired;

	/**
	 * The enabled flag.
	 */
	private boolean enabled;


	/**
	 * Constructor.
	 */
	public AlfrescoUserDetails() {
		authorities = new GrantedAuthority[0];
		accountNonExpired = true;
		accountNonLocked = true;
		credentialsNonExpired = true;
		enabled = true;
		username = "";
		password = "";
	}


	/**
	 * Returns the authorities granted to the user. Cannot return <code>null</code>.
	 *
	 * @return the authorities, sorted by natural key (never <code>null</code>)
	 */
	public GrantedAuthority[] getAuthorities() {
		return authorities;
	}

	/**
	 * Sets the authorities granted to the user.
	 *
	 * @param authorities the authorities granted to the user.
	 */
	public void setAuthorities(final GrantedAuthority[] authorities) {
		this.authorities = authorities;
	}

	/**
	 * Returns the password used to authenticate the user. Cannot return <code>null</code>.
	 *
	 * @return the password (never <code>null</code>)
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the password.
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Returns the username used to authenticate the user. Cannot return <code>null</code>.
	 *
	 * @return the username (never <code>null</code>)
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the user name.
	 *
	 * @param username the user name.
	 */
	public void setUsername(final String username) {
		this.username = username;
	}


	/**
	 * Indicates whether the user's account has expired. An expired account cannot be authenticated.
	 *
	 * @return <code>true</code> if the user's account is valid (ie non-expired), <code>false</code> if no longer valid
	 *         (ie expired)
	 */
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * Sets the non expired account flag.
	 *
	 * @param accountNonExpired  the non expired account flag.
	 */
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
	 *
	 * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
	 */
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * Sets the non locked account flag.
	 *
	 * @param accountNonLocked the non locked account flag.
	 */
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	/**
	 * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
	 * authentication.
	 *
	 * @return <code>true</code> if the user's credentials are valid (ie non-expired), <code>false</code> if no longer
	 *         valid (ie expired)
	 */
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 * Sets the non expired credentials flag.
	 *
	 * @param credentialsNonExpired the non expired credentials flag.
	 */
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}


	/**
	 * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
	 *
	 * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
	 */
	public boolean isEnabled() {
		return enabled;

	}

	/**
	 * Sets the enabled flag.
	 *
	 * @param enabled the enabled flag.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Retrieves the first name.
	 *
	 * @return the first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the first name.
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Retrieves the middle name.
	 *
	 * @return the middle name.
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Sets the middle name.
	 *
	 * @param middleName the middle name.
	 */
	public void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	/**
	 * Retrieves the last name.
	 *
	 * @return the last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the last name.
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Retrieves the email.
	 *
	 * @return the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the email.
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Retrieves the home folder.
	 *
	 * @return the home folder.
	 */
	public String getHomeFolder() {
		return homeFolder;
	}

	/**
	 * Sets the home folder.
	 *
	 * @param homeFolder the home folder.
	 */
	public void setHomeFolder(final String homeFolder) {
		this.homeFolder = homeFolder;
	}

	/**
	 * Retrieves the organization id.
	 *
	 * @return the organization id.
	 */
	public String getOrganizationId() {
		return organizationId;
	}

	/**
	 * Sets the organization id.
	 *
	 * @param organizationId the organization id.
	 */
	public void setOrganizationId(final String organizationId) {
		this.organizationId = organizationId;
	}

	/**
	 * Retrieves the organization name.
	 *
	 * @return the organization name.
	 */
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * Sets the organization name.
	 *
	 * @param organizationName the organization name.
	 */
	public void setOrganizationName(final String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * Checks if this object is equal to other one.
	 *
	 * @param o the object.
	 * @return true if equal, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AlfrescoUserDetails)) return false;

		AlfrescoUserDetails that = (AlfrescoUserDetails) o;

		return !(username != null ? !username.equals(that.username) : that.username != null);

	}

	/**
	 * Calculates the hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		return username != null ? username.hashCode() : 0;
	}

	/**
	 * Retrieves a string representation of this object.
	 *
	 * @return a string representation of this object.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("AlfrescoUserDetails{");
		builder.append("username='").append(username).append("'");

		builder.append("password='").append(password).append("'");
		builder.append("firstName='").append(firstName).append("'");
		builder.append("middleName='").append(middleName).append("'");
		builder.append("lastName='").append(lastName).append("'");

		builder.append("email='").append(email).append("'");


		builder.append("homeFolder='").append(homeFolder).append("'");
		builder.append("organizationId='").append(organizationId).append("'");
		builder.append("organizationName='").append(organizationName).append("'");
		builder.append("authorities='").append((authorities == null ? null : Arrays.asList(authorities))).append("'");
		builder.append("accountNonExpired='").append(accountNonExpired).append("'");
		builder.append("accountNonLocked='").append(accountNonLocked).append("'");
		builder.append("credentialsNonExpired='").append(credentialsNonExpired).append("'");
		builder.append("enabled='").append(enabled).append("'");

		return builder.toString();
	}
}
