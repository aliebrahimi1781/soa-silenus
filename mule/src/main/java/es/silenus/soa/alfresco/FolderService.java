package es.silenus.soa.alfresco;

import org.springframework.beans.factory.InitializingBean;
import org.alfresco.webservice.util.*;
import org.alfresco.webservice.repository.RepositoryServiceSoapPort;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;

/**
 * Folder service.
 *
 * @author Mariano Alonso
 * @since 11-nov-2009 14:05:58
 */
public class FolderService implements InitializingBean {

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(FolderService.class);

	/**
	 * The store.
	 */
	public static final Store STORE = new Store(Constants.WORKSPACE_STORE, "SpacesStore");

	/**
	 * The endpoint URL.
	 */
	private String endpointUrl;

	/**
	 * The endpoint user.
	 */
	private String endpointUser;

	/**
	 * The endpoint password.
	 */
	private String endpointPassword;

	/**
	 * The repository service.
	 */
	private RepositoryServiceSoapPort repositoryService;


	/**
	 * Sets the endpoint URL.
	 *
	 * @param endpointUrl the endpoint URL.
	 */
	public void setEndpointUrl(final String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	/**
	 * Sets the endpoint user.
	 *
	 * @param endpointUser the endpoint user.
	 */
	public void setEndpointUser(final String endpointUser) {
		this.endpointUser = endpointUser;
	}

	/**
	 * Sets the endpoint password.
	 *
	 * @param endpointPassword the endpoint password.
	 */
	public void setEndpointPassword(final String endpointPassword) {
		this.endpointPassword = endpointPassword;
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
	@Override
	public void afterPropertiesSet() throws Exception {

		WebServiceFactory.setEndpointAddress(endpointUrl);
		repositoryService = WebServiceFactory.getRepositoryService();
		
	}

	/**
	 * Refresh web service connection.
	 */
	private void refreshSession() {
		try {
			AuthenticationUtils.endSession();
			AuthenticationUtils.startSession(endpointUser, endpointPassword);
		} catch(Throwable e) {
			LOG.error("Error refreshing session", e);
		}
	}


	/**
	 * Parses a path.
	 *
	 * @param folderPath the path (Unix path).
	 *
	 * @return the converted alfresco path.
	 */
	protected Reference makeFolder(final String folderPath) {
		if(AuthenticationUtils.getTicket() == null) {
			refreshSession();
		}

		StringBuilder pathBuffer = new StringBuilder("/app:company_home");
		Reference folder = null;
		String currentFolder;
		String parentPath;
		String childPath;

		String[] parts = folderPath.split("/");
		boolean createdOne = false;

		for(String part : parts) {
			currentFolder = part.trim();

			if(currentFolder.length() > 0) {
				parentPath = pathBuffer.toString();

				pathBuffer.append("/cm:").append(ISO9075.encode(currentFolder));

				childPath = pathBuffer.toString();

				if(!createdOne) {
					folder = queryNode(childPath);
				} else {
					folder = null;
				}

				if(folder == null) {
					folder = createFolder(parentPath, currentFolder);
					createdOne = true;
				}
			}
		}

		return folder;
	}

	/**
	 * Convert a Unix like path to an Alfresco like path.
	 *
	 * @param path the Unix like path.
	 * @return the Alfresco path.
	 */
	protected StringBuilder convertPath(final String path) {
		StringBuilder pathBuffer = new StringBuilder("/app:company_home");
		String[] parts = path.split("/");

		String subpath;

		for(String part : parts) {
			subpath = part.trim();

			if(subpath.length() > 0) {
				pathBuffer.append("/cm:").append(ISO9075.encode(subpath));
			}
		}

		return pathBuffer;
	}

	/**
	 * Retrieves a node reference.
	 *
	 * @param nodePath the node path (Alfresco path).
	 *
	 * @return the node reference or null.
	 */
	protected Reference queryNode(final String nodePath) {

		try {
			Query query = new Query(Constants.QUERY_LANG_LUCENE, String.format("PATH:\"%s\"", nodePath));

			Node[] result = repositoryService.get(new Predicate(null, STORE, query));
			if((result != null) && (result.length == 1)) {
				return result[0].getReference();
			}

		} catch (RemoteException e) {
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("Failed to query node: \"%s\"", nodePath), e);
			}
		}


		return null;
	}

	/**
	 * Creates a new folder.
	 *
	 * @param parentPath the parent path (Alfresco path).
	 * @param folderName the folder name (Alfresco path).
	 *
	 * @return the reference or null.
	 */
	protected Reference createFolder(final String parentPath, final String folderName) {
		ParentReference parentReference = new ParentReference(
				STORE, null, parentPath, Constants.ASSOC_CONTAINS,
				Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, folderName));

		NamedValue[] properties = new NamedValue[] { Utils.createNamedValue(Constants.PROP_NAME, folderName) };
		CMLCreate create = new CMLCreate("1", parentReference, null, null, null, Constants.TYPE_FOLDER, properties);
		CML cml = new CML();

		cml.setCreate(new CMLCreate[] { create });

		try {
			UpdateResult[] results = repositoryService.update(cml);

			return results[0].getDestination();
		} catch(RemoteException e) {
			LOG.error(String.format("Error creating folder: %s in path: %s", folderName, parentPath));
		}

		return null;
	}

	/**
	 * Make directories.
	 *
	 * @param path the full path starting with Unix slash ("/").
	 *
	 * @return true if successful, false otherwise.
	 */
	public boolean createFolder(final String path) {
		if(AuthenticationUtils.getTicket() == null) {
			refreshSession();
		}

		return makeFolder(path) != null;
	}


	/**
	 * Creates a subfolder.
	 *
	 * @param parentPath the parent path starting with Unix slash ("/").
	 * @param folderName the folder name.
	 * @return true if sucessful, false otherwise.
	 */
	public boolean createSubFolder(final String parentPath, final String folderName) {
		return createFolder(String.format("%s/%s", parentPath, folderName));
	}


	/**
	 * TODO: later this
	 *
	 * Uploads a file to alfresco.
	 *
	 * @param file the file.
	 * @param mimeType the mime type.
	 * @param encoding the encoding.
	 * @param remoteFolderPath the remote file path.
	 *
	 * @return true if upload was successful, false otherwise.

	@SuppressWarnings({"UnusedAssignment"})
	public boolean uploadFile(File file, final String mimeType, final String encoding, String remoteFolderPath) {
		if(AuthenticationUtils.getTicket() == null) {
			refreshSession();
		}

		Reference contentNode = null;
		Content content = null;

		Reference folderReference = makeFolder(remoteFolderPath);

		if(folderReference != null) {
			String nodePath = convertPath(remoteFolderPath + "/" + file.getName()).toString();

			contentNode = queryNode(nodePath);

			if(contentNode == null) {
				// First create the content node
				ParentReference parentReference = new ParentReference(
						STORE, null, folderReference.getPath(), Constants.ASSOC_CONTAINS,
						Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, file.getName()));

				NamedValue[] properties = new NamedValue[] { new NamedValue(Constants.PROP_NAME, false, file.getName(), null) };
				CMLCreate create = new CMLCreate(
						"1", parentReference, null, null, Constants.ASSOC_CONTAINS, Constants.TYPE_CONTENT, properties);
				CML cml = new CML();

				cml.setCreate(new CMLCreate[] { create });

				try {
					UpdateResult[] result = repositoryService.update(cml);

					if((result != null) && (result.length == 1)) {
						contentNode = result[0].getDestination();
					}
				} catch(Throwable e) {
					LOG.error("Error creating content: " + file.getName() + " at path: " + remoteFolderPath, e);

					return false;
				}
			}

			// Then write the content
			if(contentNode != null) {
				try {
					byte[] bytes = FileUtils.readFileToByteArray(file);

					content = contentService.write(
							contentNode, Constants.PROP_CONTENT, bytes, new ContentFormat(mimeType, encoding));
				} catch(Throwable e) {
					LOG.error("Error writing content : " + file.getName() + " at path: " + remoteFolderPath, e);

					return false;
				}
			}
		}

		return true;
	} */

}
