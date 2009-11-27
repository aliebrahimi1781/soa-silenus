/**
 * 
 */
package es.silenus.soa.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

/**
 * Utiliti class to access and manage XML and DOM documents.
 * 
 * @author jose enrique garcia maciñeiras
 */
public final class DocUtil {

  /**
   * Instantiates a new doc util.
   */
  public DocUtil() {
  }

  /**
   * Serialize a Document into a string.
   * 
   * @param doc
   *          the Document
   * 
   * @return the string
   */
  public static String docToString(Document doc) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      JDOMSource source = new JDOMSource(doc.getDocument());
      StreamResult result = new StreamResult(baos);
      TransformerFactory transFactory = TransformerFactory.newInstance();
      Transformer transformer = transFactory.newTransformer();
      transformer.transform(source, result);
    } catch (Exception ex) {
      System.err.println(ex.getLocalizedMessage());
      System.err.println(ex.getStackTrace());
    }
    return new String(baos.toByteArray());
  }

  /**
   * Parse a XML String into a JDOM Document.
   * 
   * @param strDocument
   *          the XML String
   * 
   * @return the JDOM Document
   * 
   * @throws JDOMException
   *           the JDOM exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static Document stringToDoc(String strDocument) throws JDOMException, IOException {
    SAXBuilder builder = new SAXBuilder();
    Document document = builder.build(new StringReader(strDocument));
    return document;
  }

  /**
   * Parse an XML from an Input Stream into a DOM Document
   * 
   * @param strDocument
   *          the XML stream
   * 
   * @return the DOM document
   * 
   * @throws JDOMException
   *           the JDOM exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static Document stringToDoc(InputStream strDocument) throws JDOMException, IOException {
    SAXBuilder builder = new SAXBuilder();
    Document document = builder.build(strDocument);
    return document;
  }

  /**
   * Gets the node list from a DOM Document specified by a XPATH Expression.
   * 
   * @param xPathExpr
   *          the XPath expresion
   * @param doc
   *          the DOM Document
   * 
   * @return the node list
   * 
   * @throws JDOMException
   *           the JDOM exception
   */
  @SuppressWarnings("unchecked")
  public static List<Element> getNodeList(String xPathExpr, Document doc) throws JDOMException {
    List<Element> listNode = (List<Element>) XPath.selectNodes(doc, xPathExpr);
    return listNode;
  }

  /**
   * Gets an Element Node from a Document specified by a XPath Expression.
   * 
   * @param xPathExpr
   *          the XPath expr
   * @param doc
   *          the DOM Document
   * 
   * @return the node element
   * 
   * @throws JDOMException
   *           the JDOM exception
   */
  public static Element getNode(String xPathExpr, Document doc) throws JDOMException {
    return (Element) XPath.selectSingleNode(doc, xPathExpr);
  }

  /**
   * Gets the string attribute of an element. If no attribute was founded a default value will be
   * returned
   * 
   * @param node
   *          the element node
   * @param atribName
   *          the attribute name
   * @param defaultValue
   *          the default value
   * 
   * @return the String value of an attribute
   */
  public static String getStringAttrb(Element node, String atribName, String defaultValue) {
    return node.getAttributeValue(atribName, defaultValue);
  }

  /**
   * Apply xslt to dom.
   * 
   * @param doc
   *          the DOM Document
   * @param xslt
   *          the xslt to apply
   * 
   * @return the resultant DOM document
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws TransformerException
   *           the transformer exception
   * @throws JDOMException
   *           the JDOM exception
   */
  public static Document applyXSLTToDom(Document doc, InputStream xslt) throws IOException,
      TransformerException, JDOMException {

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer processor = transformerFactory.newTransformer(new StreamSource(xslt));

    JDOMSource source = new JDOMSource(doc);
    JDOMResult result = new JDOMResult();

    processor.transform(source, result);

    return result.getDocument();

  }

  /**
   * Apply xslt to xml.
   * 
   * @param doc
   *          the xml doc
   * @param xslt
   *          the xslt to apply
   * 
   * @return the resultant xml string
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws TransformerException
   *           the transformer exception
   * @throws JDOMException
   *           the JDOM exception
   */
  public static String applyXSLTToXML(String doc, InputStream xslt) throws IOException,
      TransformerException, JDOMException {

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer processor = transformerFactory.newTransformer(new StreamSource(xslt));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    StreamSource source = new StreamSource(new StringReader(doc));
    StreamResult result = new StreamResult(baos);

    processor.transform(source, result);

    return baos.toString();

  }

  /**
   * Find by xpath in xml.
   * 
   * @param doc
   *          the XML document
   * @param expression
   *          the XPath expression
   * 
   * @return the resultant node's string value
   * 
   * @throws XPathExpressionException
   *           the x path expression exception
   */
  public static String findByXpathInXML(String doc, String expression)
      throws XPathExpressionException {
    XPathFactory factory = XPathFactory.newInstance();
    javax.xml.xpath.XPath xPath = factory.newXPath();
    InputSource source = new InputSource(new StringReader(doc));
    return xPath.evaluate(expression, source);
  }
}
