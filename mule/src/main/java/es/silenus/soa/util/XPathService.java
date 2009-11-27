package es.silenus.soa.util;

import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.InitializingBean;

/**
 * The Class XPathService.
 * 
 * @author jose enrique garcía maciñeiras
 */
public class XPathService implements InitializingBean {

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  public void afterPropertiesSet() throws Exception {
  }

  /**
   * Gets the Xpath value.
   * 
   * @param expresion
   *          the expresion
   * @param xmlDocument
   *          the xml document
   * 
   * @return the resultant string value
   */
  public String getXPathValue(String expresion, String xmlDocument) {
    String result = "";
    try {
      result = DocUtil.findByXpathInXML(xmlDocument, expresion);
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    return result;
  }
}
