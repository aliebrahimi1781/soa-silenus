package es.silenus.cas.alfresco;

/**
 * Alfresco Authority.
 *
 * @author Mariano Alonso
 * @since 16-sep-2009 17:00:09
 */
public enum AlfrescoAuthorityType {

	/**
	 * Group.
	 */
	GROUP("GROUP_"),

	/**
	 * Role.
	 */
	ROLE("ROLE_");

	/**
	 * Prefix.
	 */
	private final String prefix;

	/**
	 * Constructor.
	 *
	 * @param prefix the prefix.
	 */
	AlfrescoAuthorityType(final String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Retrieves the prefix.
	 *
	 * @return the prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Checks if the value has this authority type prefix.
	 *
	 * @param value the value.
	 * @return true if has the prefix, false otherwose.
	 */
	public boolean hasPrefix(final String value) {
		return value != null && value.indexOf(this.prefix) == 0;
	}

	/**
	 * Removes the prefix.
	 *
	 * @param value the value.
	 * @return the value without the stripped prefix.
	 */
	public String removePrefix(final String value) {
		if(hasPrefix(value)) {
			return value.substring(this.prefix.length());
		}
		return value;
	}
}
