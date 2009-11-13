package es.silenus.cas.alfresco;

import org.springframework.security.userdetails.UserDetails;
import org.alfresco.webservice.types.NamedValue;

import java.util.List;

/**
 * Maps user details into Alfresco user internal properties.
 *
 * @author Mariano Alonso
 * @since 17-sep-2009 16:22:51
 */
public interface AlfrescoUserPropertiesMapper {


	/**
	 * Maps source user details into Alfresco properties.
	 *
	 * @param source the source.
	 * @param properties the properties.
	 */
	public void map(final UserDetails source, final List<NamedValue> properties);

}
