package es.silenus.soa.openbravo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.springframework.beans.factory.InitializingBean;

import es.silenus.soa.util.DocUtil;

/**
 * Project service.
 * 
 * @author Jose Enrique García
 * @since 11-nov-2009 14:05:58
 */
public class BaseService implements InitializingBean {

  /** Logger for this class. */
  // private static final Log LOG = LogFactory.getLog(BaseService.class);
  private String openbravoUrl = "http://soa.ishamael.com/openbravo/ws/dal";

  /** The login. */
  private String login = "Openbravo";

  /** The pass. */
  private String pass = "openbravo";

  /** The CREATE. */
  private final String CREATE = "POST";

  /** The UPDATE. */
  private final String UPDATE = "PUT";

  /** The DELETE. */
  private final String DELETE = "DELETE";

  /** The GET. */
  private final String GET = "GET";

  /** The CHARSET. */
  private final String CHARSET = "UTF-8";

  /**
   * Sets the login.
   * 
   * @param login
   *          the new login
   */
  public void setLogin(String login) {
    this.login = login;
  }

  /**
   * Sets the pass.
   * 
   * @param pass
   *          the new pass
   */
  public void setPass(String pass) {
    this.pass = pass;
  }

  /**
   * Gets the openbravo url.
   * 
   * @return the openbravo url
   */
  public String getOpenbravoUrl() {
    return openbravoUrl;
  }

  /**
   * Sets the openbravo url.
   * 
   * @param openbravoUrl
   *          the new openbravo url
   */
  public void setOpenbravoUrl(String openbravoUrl) {
    if (!openbravoUrl.isEmpty()) {
      this.openbravoUrl = openbravoUrl;
    }
  }

  /**
   * Gets the login.
   * 
   * @return the login
   */
  public String getLogin() {
    return login;
  }

  /**
   * Gets the pass.
   * 
   * @return the pass
   */
  public String getPass() {
    return pass;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  public void afterPropertiesSet() throws Exception {
    // TODO Auto-generated method stub

  }

  /**
   * Execute a REST webservice HTTP request which posts/puts content and returns a XML result.
   * 
   * @param wsPart
   *          the actual webservice part of the url, is appended to the openbravo url (
   *          {@link #getOpenbravoURL()}), includes any query parameters
   * @param content
   *          the content (XML) to post or put
   * @param expectedResponse
   *          the expected HTTP response code
   * @param expectedContent
   *          the system check that the returned content contains this expectedContent
   * @param method
   *          POST or PUT
   * 
   * @return the XML result
   * 
   * @throws Exception
   *           the exception
   */

  private String doContentRequest(String wsPart, String content, int expectedResponse,
      String expectedContent, String method) throws Exception {

    final HttpURLConnection hc = createConnection(wsPart, method);
    final OutputStream os = hc.getOutputStream();
    System.out.println(content);
    os.write(content.getBytes(CHARSET));
    os.flush();
    os.close();
    hc.connect();

    if (expectedResponse == 500) {
      // no content available anyway
      return "";

    }
    InputStream is = hc.getInputStream();
    String retContent = DocUtil.docToString(DocUtil.stringToDoc(is));
    if ((expectedContent != null) && (retContent.indexOf(expectedContent) == -1)) {
      // LOG.debug(retContent);
      retContent = "ERROR";
    }

    return retContent;
  }

  /**
   * Create a request to create content into an openbravo deploy.It need two parameters, the
   * Business Object to create and the Data.
   * 
   * @param wsPart
   *          the actual webservice part of the url, is appended to the openbravo url (
   *          {@link #getOpenbravoURL()}), includes any query parameters. E.x. : Project?where=name
   *          like 'Bazaar'
   * @param content
   *          the content to create the Business Object
   * 
   * @return the xml resultant about the operation
   * 
   * @throws Exception
   *           the exception
   */
  public String doCreateContentRequest(String wsPart, String content) throws Exception {
    return this.doContentRequest(wsPart, content, 0, null, this.CREATE);
  }

  /**
   * Makes an Business Object update into an openbravo deploy.It need two parameters, the Business
   * Object to update and the Data.
   * 
   * @param wsPart
   *          the actual webservice part of the url, is appended to the openbravo url (
   *          {@link #getOpenbravoURL()}), includes any query parameters. E.x. : Project?where=name
   *          like 'Bazaar'
   * @param content
   *          the content to update the business object
   * 
   * @return the string
   * 
   * @throws Exception
   *           the exception
   */
  protected String doUpdateContentRequest(String wsPart, String content) throws Exception {
    return this.doContentRequest(wsPart, content, 0, null, this.UPDATE);
  }

  /**
   * Executes a DELETE HTTP request, the wsPart is appended to the {@link #getOpenbravoURL()}.
   * 
   * @param wsPart
   *          the actual webservice part of the url, is appended to the openbravo url (
   *          {@link #getOpenbravoURL()}), includes any query parameters. E.x. : Project?where=name
   *          like 'Bazaar'
   * @param expectedResponse
   *          the expected HTTP response code
   * 
   * @throws Exception
   *           the exception
   */

  private void doDirectDeleteRequest(String wsPart, int expectedResponse) throws Exception {

    final HttpURLConnection hc = createConnection(wsPart, this.DELETE);

    hc.connect();

  }

  /**
   * Make a delete content request to an openbravo deploy.
   * 
   * @param wsPart
   *          the actual webservice part of the url, is appended to the openbravo url (
   *          {@link #getOpenbravoURL()}), includes any query parameters. E.x. : Project?where=name
   *          like 'Bazaar'
   * 
   * @throws Exception
   *           the exception
   */
  public void doDeleteContentRequest(String wsPart) throws Exception {
    this.doDirectDeleteRequest(wsPart, 0);
  }

  /**
   * Executes a GET request.
   * 
   * @param wsPart
   *          the actual webservice part of the url, is appended to the openbravo url (
   *          {@link #getOpenbravoURL()}), includes any query parameters
   * @param responseCode
   *          the expected HTTP response code
   * 
   * @return the content returned from the GET request
   * 
   * @throws Exception
   *           the exception
   */

  private String doGetRequest(String wsPart, int responseCode) throws Exception {

    final HttpURLConnection hc = createConnection(wsPart, this.GET);

    hc.connect();

    final InputStream is = hc.getInputStream();
    final String content = is.toString();
    is.close();
    return content;

  }

  /**
   * Do get content request.
   * 
   * @param wsPart
   *          the actual webservice part of the url, is appended to the openbravo url (
   *          {@link #getOpenbravoURL()}), includes any query parameters
   * @return the string
   * 
   * @throws Exception
   *           the exception
   */
  public String doGetContentRequest(String wsPart) throws Exception {
    return this.doGetRequest(wsPart, 0);
  }

  /**
   * Creates a HTTP connection against a webservice of an openbravo deploy.
   * 
   * @param wsPart
   *          the actual webservice part of the url, is appended to the openbravo url (
   *          {@link #getOpenbravoURL()}), includes any query parameters
   * @param method
   *          POST, PUT, GET or DELETE
   * 
   * @return the created connection
   * 
   * @throws Exception
   *           the exception
   */

  protected HttpURLConnection createConnection(String wsPart, String method) throws Exception {
    Authenticator.setDefault(new Authenticator() {

      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(login, pass.toCharArray());
      }

    });

    // LOG.debug(method + ": " + getOpenbravoUrl() + wsPart);
    final URL url = new URL(getOpenbravoUrl().concat(wsPart));

    final HttpURLConnection hc = (HttpURLConnection) url.openConnection();
    hc.setRequestMethod(method);
    hc.setAllowUserInteraction(false);
    hc.setDefaultUseCaches(false);
    hc.setDoOutput(true);
    hc.setDoInput(true);
    hc.setInstanceFollowRedirects(true);
    hc.setUseCaches(false);
    hc.setRequestProperty("Content-Type", "text/xml");
    return hc;
  }

}
