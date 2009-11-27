/**
 * 
 */
package es.silenus.soa.openbravo;

import org.springframework.core.io.Resource;

import es.silenus.soa.util.DocUtil;

/**
 * The Class BusinessPartnerManagementService.
 * 
 * @author josé enrique garcía
 */
public class BusinessPartnerManagementService extends BaseService {

  /** The xsl business partner. */
  private Resource xslBusinessPartner;

  /** The xml example. */
  private Resource xmlExample;

  /**
   * Gets the xml example.
   * 
   * @return the xmlExample
   */
  public Resource getXmlExample() {
    return xmlExample;
  }

  /**
   * Sets the xml example.
   * 
   * @param xmlExample
   *          the xmlExample to set
   */
  public void setXmlExample(Resource xmlExample) {
    this.xmlExample = xmlExample;
  }

  /**
   * Gets the xsl business partner.
   * 
   * @return the xslProject
   */
  public Resource getXslBusinessPartner() {
    return xslBusinessPartner;
  }

  /**
   * Sets the xsl business partner.
   * 
   * @param xslProject
   *          the xslProject to set
   */
  public void setXslBusinessPartner(Resource xslProject) {
    this.xslBusinessPartner = xslProject;
  }

  /**
   * Instantiates a new business partner management service.
   */
  public BusinessPartnerManagementService() {
  }

  /**
   * Creates the business partner.
   * 
   * @param businessPartner
   *          the business partner
   * 
   * @throws Exception
   *           the exception
   */
  public void createBusinessPartner(String businessPartner) throws Exception {
    String businessPartnerName = DocUtil.findByXpathInXML(businessPartner,
        "/Opportunity/account_name");
    if (this.getBusinessPartner(businessPartnerName) == null) {
      this.doCreateContentRequest("BusinessPartner", DocUtil.applyXSLTToXML(businessPartner,
          xslBusinessPartner.getInputStream()));
    }
  }

  /**
   * Gets the business partner.
   * 
   * @param businessPartnerName
   *          the business partner name
   * 
   * @return the business partner
   * 
   * @throws Exception
   *           the exception
   */
  public String getBusinessPartner(String businessPartnerName) throws Exception {
    // http://soa.ishamael.com/openbravo/ws/dal/BusinessPartner?where=name%20like%20%27Cuenta%20de%20Segundas%27
    StringBuilder strBuilder = new StringBuilder("BusinessPartner?where=name%20like%20%27");
    strBuilder.append(businessPartnerName.replaceAll(" ", "%20"));
    strBuilder.append("%27");
    return this.doGetContentRequest(strBuilder.toString());
  }

  /**
   * Delete business partner.
   * 
   * @param businessPartnerName
   *          the business partner name
   * 
   * @throws Exception
   *           the exception
   */
  public void deleteBusinessPartner(String businessPartnerName) throws Exception {
    if (this.getBusinessPartner(businessPartnerName) != null) {
      StringBuilder strBuilder = new StringBuilder("BusinessPartner?where=name%20like%20%27");
      strBuilder.append(businessPartnerName);
      strBuilder.append("%27");
      this.doDeleteContentRequest(strBuilder.toString());
    }
  }
  /*
   * THe main method makes a conection test against an openbravo deploy. Execute the create Business
   * Partner functión
   */
  // public static void main(String[] args) {
  //
  // BusinessPartnerManagementService proManagemetService = new BusinessPartnerManagementService();
  // proManagemetService
  // .setXmlExample(new File(
  // "/home/kszosze/silenus/memorias/repo/subvenciones-2009/soa-silenus/mule/src/main/java/es/silenus/soa/openbravo/account.xml"));
  // proManagemetService
  // .setXslAccount(new File(
  // "/home/kszosze/silenus/memorias/repo/subvenciones-2009/soa-silenus/mule/src/main/java/es/silenus/soa/openbravo/account.xsl"));
  //
  // proManagemetService.setLogin("Openbravo");
  // proManagemetService.setPass("openbravo");
  //
  // try {
  //
  // FileReader fr = new FileReader(
  // new File(
  // "/home/kszosze/silenus/memorias/repo/subvenciones-2009/soa-silenus/mule/src/main/java/es/silenus/soa/openbravo/opportunity.xml"));
  //
  // BufferedReader bf = new BufferedReader(fr);
  //
  // StringBuilder strB = new StringBuilder();
  // String strReaded = "";
  // while ((strReaded = bf.readLine()) != null) {
  // strB.append(strReaded);
  // }
  //
  // proManagemetService.createAccount(strB.toString());
  // } catch (FileNotFoundException e) {
  //
  // e.printStackTrace();
  // } catch (Exception e) {
  //
  // e.printStackTrace();
  // }
  // }
}
