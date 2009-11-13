package es.silenus.cas.alfresco;

import org.springframework.security.userdetails.UserDetails;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;

import java.util.List;

/**
 * Default alfresco user to properties mapper.
 *
 * @author Mariano Alonso
 * @since 17-sep-2009 16:30:09
 */
public class DefaultAlfrescoUserPropertiesMapper implements AlfrescoUserPropertiesMapper {

	/**
	 * Organization.
	 */
	public static final String PROP_ORGANIZATION = Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "organization");


	/**
	 * Maps source user details into Alfresco properties.
	 *
	 * @param source		 the source.
	 * @param properties the properties.
	 */
	public void map(UserDetails source, List<NamedValue> properties) {
		if(source instanceof AlfrescoUserDetails) {
			AlfrescoUserDetails details = (AlfrescoUserDetails)source;

			String temp = details.getUsername();
			if(temp != null) {
				properties.add(Utils.createNamedValue(Constants.PROP_USERNAME, temp));
			}
			temp = details.getEmail();
			if(temp != null) {
				properties.add(Utils.createNamedValue(Constants.PROP_USER_EMAIL, temp));
			}
			temp = details.getFirstName();
			if(temp != null) {
				properties.add(Utils.createNamedValue(Constants.PROP_USER_FIRSTNAME, temp));
			}
			temp = details.getMiddleName();
			if(temp != null) {
				properties.add(Utils.createNamedValue(Constants.PROP_USER_MIDDLENAME, temp));
			}
			temp = details.getLastName();
			if(temp != null) {
				properties.add(Utils.createNamedValue(Constants.PROP_USER_LASTNAME, temp));
			}
			temp = details.getOrganizationId();
			if(temp != null) {
				properties.add(Utils.createNamedValue(Constants.PROP_USER_ORGID, temp));
			}
			temp = details.getOrganizationName();
			if(temp != null) {
				properties.add(Utils.createNamedValue(PROP_ORGANIZATION, temp));
			}
			temp = details.getHomeFolder();
			if(temp != null) {
				properties.add(Utils.createNamedValue(Constants.PROP_USER_HOMEFOLDER, temp));
			}
		}
	}
}
