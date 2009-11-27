<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ob="http://www.openbravo.com" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <xsl:template match="Opportunity">
    <ob:Openbravo>
      <BusinessPartner>
        <xsl:attribute name="identifier">
            <xsl:value-of select="account_name" />
        </xsl:attribute>
        <id></id>
        <client id="1000001" entity-name="ADClient" identifier="Accounting Test" />
        <organization id="0" entity-name="Organization" identifier="*" />
        <active>true</active>
        <creationDate transient="true"><xsl:value-of select="date_modified" /></creationDate>
        <createdBy transient="true" id="0" entity-name="ADUser" identifier="System" />
        <updated transient="true"><xsl:value-of select="date_modified" /></updated>
        <updatedBy transient="true" id="0" entity-name="ADUser" identifier="System" />
        <searchKey>
            <xsl:value-of select="account_name" />
        </searchKey>
        <name>
             <xsl:value-of select="account_name" />
        </name>
        <name2 xsi:nil="true" />
        <description xsi:nil="true" />
        <summaryLevel>false</summaryLevel>
        <businessPartnerCategory id="1000004" entity-name="BusinessPartnerCategory" identifier="Standard" />
        <oneTimeTransaction>false</oneTimeTransaction>
        <potentialCustomer>true</potentialCustomer>
        <vendor>false</vendor>
        <customer>true</customer>
        <employee>false</employee>
        <isSalesRepresentative>false</isSalesRepresentative>
        <referenceNo xsi:nil="true" />
        <dUNS xsi:nil="true" />
        <uRL xsi:nil="true" />
        <language xsi:nil="true" />
        <taxID xsi:nil="true" />
        <taxExempt>false</taxExempt>
        <invoiceSchedule xsi:nil="true" />
        <valuation xsi:nil="true" />
        <volumeOfSales xsi:nil="true" />
        <noOfEmployees xsi:nil="true" />
        <nAICSSIC xsi:nil="true" />
        <dateOfFirstSale/>
        <acquisitionCost>0</acquisitionCost>
        <expectedLifetimeRevenue>0</expectedLifetimeRevenue>
        <lifetimeRevenueToDate>38525</lifetimeRevenueToDate>
        <share xsi:nil="true" />
        <formOfPayment xsi:nil="true" />
        <creditLimit>0</creditLimit>
        <creditUsed>1150</creditUsed>
        <paymentTerms xsi:nil="true" />
        <priceList xsi:nil="true" />
        <printDiscount>true</printDiscount>
        <orderDescription xsi:nil="true" />
        <orderReference xsi:nil="true" />
        <pOFormOfPayment xsi:nil="true" />
        <purchasePricelist xsi:nil="true" />
        <pOPaymentTerms xsi:nil="true" />
        <numberOfCopies xsi:nil="true" />
        <greeting xsi:nil="true" />
        <invoiceTerms>I</invoiceTerms>
        <deliveryTerms xsi:nil="true" />
        <deliveryMethod xsi:nil="true" />
        <salesRepresentative xsi:nil="true" />
        <partnerParent xsi:nil="true" />
        <creditStatus>O</creditStatus>
        <forcedOrg xsi:nil="true" />
        <pricesShownInOrder>true</pricesShownInOrder>
        <invoiceGrouping>000000000000000</invoiceGrouping>
        <maturityDate1 xsi:nil="true" />
        <maturityDate2 xsi:nil="true" />
        <maturityDate3 xsi:nil="true" />
        <operator>false</operator>
        <uPCEAN xsi:nil="true" />
        <salaryCategory xsi:nil="true" />
        <invoicePrintformat xsi:nil="true" />
        <consumptionDays xsi:nil="true" />
        <bankAccount xsi:nil="true" />
        <taxCategory xsi:nil="true" />
        <pOMaturityDate1 xsi:nil="true" />
        <pOMaturityDate2 xsi:nil="true" />
        <pOMaturityDate3 xsi:nil="true" />
        <transactionalBankAccount xsi:nil="true" />
        <sOBPTaxCategory xsi:nil="true" />
        <fiscalcode xsi:nil="true" />
        <isofiscalcode xsi:nil="true" />
        <employeeAccountsList>
        </employeeAccountsList>
        <customerAccountsList>
        </customerAccountsList>
        <businessPartnerBankAccountList />
        <approvedVendorList />
        <financialMgmtAssetList />
        <businessPartnerDiscountList />
        <timeAndExpenseSheetLineList />
        <invoiceList>
        </invoiceList>
        <pricingVolumeDiscountBusinessPartnerList />
        <employeeSalaryCategoryList />
        <aDUserList />
        <businessPartnerWithholdingList />
        <businessPartnerLocationList>
          <!-- BusinessPartnerLocation identifier="Lugo">
            <client id="1000001" entity-name="ADClient" identifier="Accounting Test" />
            <organization id="0" entity-name="Organization" identifier="*" />
            <active>true</active>
            <creationDate transient="true"><xsl:value-of select="date_modified" /></creationDate>
            <createdBy transient="true" id="0" entity-name="ADUser" identifier="System" />
            <updated transient="true"><xsl:value-of select="date_modified" /></updated>
            <updatedBy transient="true" id="0" entity-name="ADUser" identifier="System" />
            <name>Lugo</name>
            <invoiceToAddress>true</invoiceToAddress>
            <shipToAddress>true</shipToAddress>
            <payFromAddress>true</payFromAddress>
            <remitToAddress>true</remitToAddress>
            <phone xsi:nil="true" />
            <alternativePhone xsi:nil="true" />
            <fax xsi:nil="true" />
            <salesRegion xsi:nil="true" />
            <businessPartner entity-name="BusinessPartner">
                <xsl:attribute name="identifier">
                    <xsl:value-of select="account_name" />
                </xsl:attribute>
            </businessPartner>
            <locationAddress entity-name="Location" identifier="Lugo" />
            <taxLocation>false</taxLocation>
            <uPCEAN xsi:nil="true" />
          </BusinessPartnerLocation -->
        </businessPartnerLocationList>
        <warehouseShipperList />
        <vendorAccountsList>
        </vendorAccountsList>
      </BusinessPartner>
    </ob:Openbravo>
  </xsl:template>
</xsl:stylesheet>