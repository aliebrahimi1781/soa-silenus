/*
 * 
 */
package es.silenus.soa.openbravo;

import org.springframework.core.io.Resource;

import es.silenus.soa.util.DocUtil;

/**
 * The Class ProjectManagementService.
 * 
 * @author josé enrique garcía
 */
public class ProjectManagementService extends BaseService {

  /** The xsl project. */
  private Resource xslProject;

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
   * Gets the xsl project.
   * 
   * @return the xslProject
   */
  public Resource getXslProject() {
    return xslProject;
  }

  /**
   * Sets the xsl project.
   * 
   * @param xslProject
   *          the xslProject to set
   */
  public void setXslProject(Resource xslProject) {
    this.xslProject = xslProject;
  }

  /**
   * Instantiates a new project management service.
   */
  public ProjectManagementService() {
  }

  /**
   * Creates a new project.
   * 
   * @param projectData
   *          the project data
   * 
   * @throws Exception
   *           the exception
   */
  public void createProject(String projectData) throws Exception {
    String projectName = DocUtil.findByXpathInXML(projectData, "/Opportunity/name");
    if (this.getProject(projectName) == null) {
      this.doCreateContentRequest("Project", DocUtil.applyXSLTToXML(projectData, getXslProject()
          .getInputStream()));
    }
  }

  /**
   * Gets a project.
   * 
   * @param projectName
   *          the project name
   * 
   * @return the xml's project
   * 
   * @throws Exception
   *           the exception
   */
  public String getProject(String projectName) throws Exception {
    StringBuilder strBuilder = new StringBuilder("Project?where=name%20like%20%27");
    strBuilder.append(projectName.replaceAll(" ", "%20"));
    strBuilder.append("%27");
    return this.doGetContentRequest(strBuilder.toString());
  }

  /**
   * Delete a project.
   * 
   * @param projectName
   *          the project name
   * 
   * @throws Exception
   *           the exception
   */
  public void deleteProject(String projectName) throws Exception {
    this.doDeleteContentRequest(getOpenbravoUrl().concat(projectName));
    if (this.getProject(projectName) != null) {
      StringBuilder strBuilder = new StringBuilder("Project?where=name%20like%20%27'");
      strBuilder.append(projectName);
      strBuilder.append("%27");
      this.doDeleteContentRequest(strBuilder.toString());
    }
  }

  // public static void main(String[] args) {
  //
  // ProjectManagementService proManagemetService = new ProjectManagementService();
  // proManagemetService
  // .setXmlExample(new File(
  // "/home/kszosze/silenus/memorias/repo/subvenciones-2009/soa-silenus/mule/src/main/java/es/silenus/soa/openbravo/opportunity.xml"));
  // proManagemetService
  // .setXslProject(new File(
  // "/home/kszosze/silenus/memorias/repo/subvenciones-2009/soa-silenus/mule/src/main/java/es/silenus/soa/openbravo/project.xsl"));
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
  // proManagemetService.createProject(strB.toString());
  // } catch (FileNotFoundException e) {
  //
  // e.printStackTrace();
  // } catch (Exception e) {
  //
  // e.printStackTrace();
  // }
  // }
}
