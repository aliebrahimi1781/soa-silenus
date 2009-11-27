<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ob="http://www.openbravo.com" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <xsl:template match="Opportunity">
    <ob:Openbravo>
      <Project>
        <xsl:attribute name="identifier">
            <xsl:value-of select="name" />
        </xsl:attribute>
        <id></id>
        <client entity-name="ADClient" identifier="BigBazaar" />
        <organization entity-name="Organization" identifier="California" />
        <active>true</active>
        <creationDate transient="true">
          <xsl:value-of select="date_modified" />
        </creationDate>
        <createdBy transient="true" id="100" entity-name="ADUser" identifier="Openbravo" />
        <updated transient="true">
          <xsl:value-of select="date_modified" />
        </updated>
        <updatedBy transient="true" id="100" entity-name="ADUser" identifier="Openbravo" />
        <searchKey>
          <xsl:value-of select="name" />
        </searchKey>
        <name>
          <xsl:value-of select="name" />
        </name>
        <description xsi:nil="true" />
        <comments xsi:nil="true" />
        <summaryLevel>false</summaryLevel>
        <userContact xsi:nil="true" />
        <businessPartner>
          <xsl:attribute name="entity-name">
            <xsl:value-of select="account_name" />
        </xsl:attribute>
          <xsl:attribute name="identifier">
            <xsl:value-of select="account_name" />
        </xsl:attribute>
        </businessPartner>
        <partnerAddress entity-name="BusinessPartnerLocation" identifier="Street nºRF (Technological park), ME (EE.UU)" />
        <orderReference xsi:nil="true" />
        <paymentTerms entity-name="FinancialMgmtPaymentTerm" identifier="60days" />
        <currency id="102" entity-name="Currency" identifier="EUR" />
        <createTemporaryPriceList>true</createTemporaryPriceList>
        <priceListVersion xsi:nil="true" />
        <salesCampaign xsi:nil="true" />
        <legallyBindingContract>false</legallyBindingContract>
        <plannedAmount>0.00</plannedAmount>
        <plannedQuantity>0</plannedQuantity>
        <plannedMargin>0.00</plannedMargin>
        <contractAmount>0.00</contractAmount>
        <contractDate></contractDate>
        <endingDate></endingDate>
        <generateTo>false</generateTo>
        <processed>false</processed>
        <salesRepresentative entity-name="ADUser" identifier="Rachel" />
        <copyFrom>false</copyFrom>
        <projectType xsi:nil="true" />
        <contractQuantity>0</contractQuantity>
        <invoiceAmount>0.00</invoiceAmount>
        <invoiceQuantity>0</invoiceQuantity>
        <projectBalance>0.00</projectBalance>
        <standardPhase xsi:nil="true" />
        <projectPhase xsi:nil="true" />
        <priceCeiling>false</priceCeiling>
        <warehouse entity-name="Warehouse" identifier="Main Warehouse" />
        <projectCategory>S</projectCategory>
        <processNow>false</processNow>
        <initiativeType xsi:nil="true" />
        <projectStatus>OP</projectStatus>
        <workType xsi:nil="true" />
        <invoiceAddress entity-name="BusinessPartnerLocation"
          identifier="Street nº A (Industrial polygon(zone)), ME (EE.UU)" />
        <phase xsi:nil="true" />
        <generateOrder>false</generateOrder>
        <changeProjectStatus>N</changeProjectStatus>
        <locationAddress xsi:nil="true" />
        <priceList entity-name="PricingPriceList" identifier="Special nº4" />
        <formOfPayment>1</formOfPayment>
        <invoiceToProject>false</invoiceToProject>
        <plannedPoAmount xsi:nil="true" />
        <lastPlannedProposalDate xsi:nil="true" />
        <numberOfCopies>1</numberOfCopies>
        <accountNo xsi:nil="true" />
        <plannedExpenses xsi:nil="true" />
        <expensesMargin xsi:nil="true" />
        <reinvoicedExpenses xsi:nil="true" />
        <personInCharge entity-name="BusinessPartner" identifier="Giollanaebhin" />
        <serviceCost xsi:nil="true" />
        <serviceMargin xsi:nil="true" />
        <serviceRevenue xsi:nil="true" />
        <setProjectType>false</setProjectType>
        <startingDate></startingDate>
        <projectVendorList />
        <projectPhaseList />
        <projectAccountsList></projectAccountsList>
        <projectProposalList />
        <projectLineList />
        <projectIssueList />
      </Project>
    </ob:Openbravo>
  </xsl:template>
</xsl:stylesheet>