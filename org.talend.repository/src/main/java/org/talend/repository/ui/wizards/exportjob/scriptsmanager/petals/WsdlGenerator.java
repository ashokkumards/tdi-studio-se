// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//   
// ============================================================================
package org.talend.repository.ui.wizards.exportjob.scriptsmanager.petals;

/**
 * Copyright (c) 2010, EBM WebSourcing TODO: specify the license (EPL if not specified anywhere else).
 */

/**
 * This class was generated by EMF JET. <b>DO NOT MODIFY IT!</b>
 * 
 * @author Vincent Zurczak - EBM WebSourcing
 */
public class WsdlGenerator {

    protected static String nl;

    public static synchronized WsdlGenerator create(String lineSeparator) {
        nl = lineSeparator;
        WsdlGenerator result = new WsdlGenerator();
        nl = null;
        return result;
    }

    public final String pNL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;

    protected final String pTEXT1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + pNL + "<wsdl:definitions " + pNL + "\tname=\"";

    protected final String pTEXT2 = "Service\"" + pNL + "\ttargetNamespace=\"http://petals.ow2.org/talend/\" " + pNL
            + "\txmlns:tns=\"http://petals.ow2.org/talend/\"" + pNL + "\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " + pNL
            + "\txmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"" + pNL
            + "\txmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\">" + pNL + "\t" + pNL + "\t<wsdl:types>" + pNL
            + "\t\t<xs:schema " + pNL + "\t\t\txmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" + pNL
            + "\t\t\txmlns:ns1=\"http://jaxb.dev.java.net/array\" " + pNL + "\t\t\txmlns:tns=\"http://petals.ow2.org/talend/\""
            + pNL + "\t\t\tattributeFormDefault=\"unqualified\" " + pNL + "\t\t\telementFormDefault=\"qualified\"" + pNL
            + "\t\t\ttargetNamespace=\"http://petals.ow2.org/talend/\">" + pNL + "\t\t\t" + pNL
            + "\t\t\t<xs:import namespace=\"http://jaxb.dev.java.net/array\" />" + pNL + "\t\t\t" + pNL
            + "\t\t\t<xs:complexType name=\"attachment\">" + pNL + "\t\t\t\t<xs:sequence>" + pNL + "\t\t\t\t\t<xs:element " + pNL
            + "\t\t\t\t\t\txmlns:ns2=\"http://www.w3.org/2005/05/xmlmime\"" + pNL
            + "\t\t\t\t\t\tminOccurs=\"0\" name=\"fileContent\" " + pNL
            + "\t\t\t\t\t\tns2:expectedContentTypes=\"application/octet-stream\"" + pNL
            + "\t\t\t\t\t\ttype=\"xs:base64Binary\" />" + pNL + "\t\t\t\t</xs:sequence>" + pNL + "\t\t\t</xs:complexType>" + pNL
            + "\t\t\t" + pNL + "\t\t\t" + pNL + "\t\t\t<!-- The input message -->" + pNL
            + "\t\t\t<xs:element name=\"executeJob\" type=\"tns:executeJob\" />" + pNL
            + "\t\t\t<xs:complexType name=\"executeJob\">" + pNL + "\t\t\t\t<xs:sequence>" + pNL
            + "\t\t\t\t\t<xs:element minOccurs=\"0\" name=\"contexts\" type=\"tns:talendContexts\" />" + pNL
            + "\t\t\t\t\t<xs:element minOccurs=\"0\" name=\"in-attachments\" type=\"tns:inAttachments\" />" + pNL
            + "\t\t\t\t\t<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"in-data-bean\" type=\"tns:inRow\" />" + pNL
            + "\t\t\t\t\t<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"talend-option\" type=\"xs:string\" />" + pNL
            + "\t\t\t\t</xs:sequence>" + pNL + "\t\t\t</xs:complexType>" + pNL + "\t\t\t" + pNL
            + "\t\t\t<xs:complexType name=\"talendContexts\">" + pNL + "\t\t\t\t<xs:sequence>";

    protected final String pTEXT3 = pNL + "\t\t\t\t\t\t<xs:element name=\"";

    protected final String pTEXT4 = "\" type=\"";

    protected final String pTEXT5 = "\" ";

    protected final String pTEXT6 = "minOccurs=\"0\" default=\"";

    protected final String pTEXT7 = "\"";

    protected final String pTEXT8 = " />";

    protected final String pTEXT9 = pNL + "\t\t\t\t</xs:sequence>" + pNL + "\t\t\t</xs:complexType>" + pNL + "\t\t\t" + pNL
            + "\t\t\t<xs:complexType name=\"inAttachments\">" + pNL + "\t\t\t\t<xs:sequence>";

    protected final String ppTEXT10 = pNL + "\t\t\t\t\t\t<xs:element name=\"";

    protected final String ppTEXT11 = "\" nillable=\"true\" type=\"tns:attachment\" />";

    protected final String ppTEXT12 = pNL + "\t\t\t\t</xs:sequence>" + pNL + "\t\t\t</xs:complexType>" + pNL + "\t\t\t" + pNL
            + "\t\t\t<xs:complexType name=\"inRow\">" + pNL + "\t\t\t\t<xs:sequence>";

    protected final String ppTEXT13 = pNL + "\t\t\t\t\t<xs:element name=\"";

    protected final String ppTEXT14 = "\" type=\"";

    protected final String ppTEXT15 = "\" ";

    protected final String ppTEXT16 = "default=\"";

    protected final String ppTEXT17 = "\"";

    protected final String ppTEXT18 = "nillable=\"true\" ";

    protected final String ppTEXT19 = " />";

    protected final String pTEXT20 = pNL
            + "\t\t\t\t</xs:sequence>"
            + pNL
            + "\t\t\t</xs:complexType>"
            + pNL
            + "\t\t\t"
            + pNL
            + "\t\t\t"
            + pNL
            + "\t\t\t<!-- The output message -->"
            + pNL
            + "\t\t\t<xs:element name=\"executeJobResponse\" type=\"tns:executeJobResponse\" />"
            + pNL
            + "\t\t\t<xs:complexType name=\"executeJobResponse\">"
            + pNL
            + "\t\t\t\t<xs:sequence>"
            + pNL
            + "\t\t\t\t\t<xs:element minOccurs=\"0\" name=\"talend-job-output\" type=\"tns:talendJobOutput\" />"
            + pNL
            + "\t\t\t\t</xs:sequence>"
            + pNL
            + "\t\t\t</xs:complexType>"
            + pNL
            + "\t\t\t"
            + pNL
            + "\t\t\t<xs:complexType name=\"talendJobOutput\">"
            + pNL
            + "\t\t\t\t<xs:sequence>"
            + pNL
            + "\t\t\t\t\t<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"executionResult\" nillable=\"true\" type=\"ns1:stringArray\" />"
            + pNL
            + "\t\t\t\t\t<xs:element minOccurs=\"0\" name=\"outAttachment\" type=\"tns:outAttachments\" />"
            + pNL
            + "\t\t\t\t\t<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"outDataBean\" nillable=\"true\" type=\"tns:outRow\" />"
            + pNL + "\t\t\t\t</xs:sequence>" + pNL + "\t\t\t</xs:complexType>" + pNL + "\t\t\t" + pNL
            + "\t\t\t<xs:complexType name=\"outAttachments\">" + pNL + "\t\t\t\t<xs:sequence>";

    protected final String pTEXT21 = pNL + "\t\t\t\t\t\t<xs:element name=\"";

    protected final String pTEXT22 = "\" nillable=\"true\" type=\"tns:attachment\" />";

    protected final String pTEXT23 = pNL + "\t\t\t\t</xs:sequence>" + pNL + "\t\t\t</xs:complexType>" + pNL + "\t\t\t" + pNL
            + "\t\t\t<xs:complexType name=\"outRow\">" + pNL + "\t\t\t\t<xs:sequence>";

    protected final String pTEXT24 = pNL + "\t\t\t\t\t<xs:element name=\"";

    protected final String pTEXT25 = "\" type=\"";

    protected final String pTEXT26 = "\" ";

    protected final String pTEXT27 = " default=\"";

    protected final String pTEXT28 = "\" ";

    protected final String pTEXT29 = "nillable=\"true\" ";

    protected final String pTEXT30 = "minOccurs=\"";

    protected final String pTEXT31 = "\" ";

    protected final String pTEXT32 = "maxOccurs=\"";

    protected final String pTEXT33 = "\"";

    protected final String pTEXT34 = " />";

    protected final String pTEXT35 = pNL
            + "\t\t\t\t</xs:sequence>"
            + pNL
            + "\t\t\t</xs:complexType>"
            + pNL
            + "\t\t\t"
            + pNL
            + "\t\t\t"
            + pNL
            + "\t\t\t<!-- The possible faults -->"
            + pNL
            + "\t\t\t<xs:element name=\"TalendBusinessException\" type=\"tns:TalendBusinessException\" />"
            + pNL
            + "\t\t\t<xs:complexType name=\"TalendBusinessException\">"
            + pNL
            + "\t\t\t\t<xs:sequence />"
            + pNL
            + "\t\t\t</xs:complexType>"
            + pNL
            + "\t\t\t"
            + pNL
            + "\t\t\t<xs:element name=\"TalendTechnicalException\" type=\"tns:TalendTechnicalException\" />"
            + pNL
            + "\t\t\t<xs:complexType name=\"TalendTechnicalException\">"
            + pNL
            + "\t\t\t\t<xs:sequence />"
            + pNL
            + "\t\t\t</xs:complexType>"
            + pNL
            + "\t\t</xs:schema>"
            + pNL
            + "\t\t"
            + pNL
            + "\t\t<xs:schema "
            + pNL
            + "\t\t\txmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
            + pNL
            + "\t\t\ttargetNamespace=\"http://jaxb.dev.java.net/array\" "
            + pNL
            + "\t\t\tattributeFormDefault=\"unqualified\" "
            + pNL
            + "\t\t\telementFormDefault=\"qualified\""
            + pNL
            + "\t\t\tversion=\"1.0\">"
            + pNL
            + "\t\t\t"
            + pNL
            + "\t\t\t<xs:complexType final=\"#all\" name=\"stringArray\">"
            + pNL
            + "\t\t\t\t<xs:sequence>"
            + pNL
            + "\t\t\t\t\t<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"item\" nillable=\"true\" type=\"xs:string\" />"
            + pNL + "\t\t\t\t</xs:sequence>" + pNL + "\t\t\t</xs:complexType>" + pNL + "\t\t</xs:schema>" + pNL
            + "\t</wsdl:types>" + pNL + "\t" + pNL + "\t<wsdl:message name=\"executeJob\">" + pNL
            + "\t\t<wsdl:part name=\"parameters\" element=\"tns:executeJob\" />" + pNL + "\t</wsdl:message>" + pNL + "\t" + pNL
            + "\t<wsdl:message name=\"TalendBusinessException\">" + pNL
            + "\t\t<wsdl:part name=\"TalendBusinessException\" element=\"tns:TalendBusinessException\" />" + pNL
            + "\t</wsdl:message>" + pNL + "\t" + pNL + "\t<wsdl:message name=\"TalendTechnicalException\">" + pNL
            + "\t\t<wsdl:part name=\"TalendTechnicalException\" element=\"tns:TalendTechnicalException\" />" + pNL
            + "\t</wsdl:message>" + pNL + "\t" + pNL + "\t<wsdl:message name=\"executeJobResponse\">" + pNL
            + "\t\t<wsdl:part name=\"parameters\" element=\"tns:executeJobResponse\" />" + pNL + "\t</wsdl:message>" + pNL + "\t"
            + pNL + "\t<wsdl:portType name=\"";

    protected final String pTEXT36 = "ServicePortType\">" + pNL + "\t\t<wsdl:operation name=\"executeJob\">" + pNL
            + "\t\t\t<wsdl:input name=\"executeJob\" message=\"tns:executeJob\" />" + pNL
            + "\t\t\t<wsdl:output name=\"executeJobResponse\" message=\"tns:executeJobResponse\" />" + pNL
            + "\t\t\t<wsdl:fault name=\"TalendBusinessException\" message=\"tns:TalendBusinessException\" />" + pNL
            + "\t\t\t<wsdl:fault name=\"TalendTechnicalException\" message=\"tns:TalendTechnicalException\" />" + pNL
            + "\t\t</wsdl:operation>" + pNL + "\t</wsdl:portType>" + pNL + "\t" + pNL + "\t<wsdl:binding name=\"";

    protected final String pTEXT37 = "ServiceSoapBinding\" type=\"tns:";

    protected final String pTEXT38 = "ServicePortType\">" + pNL
            + "\t\t<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\" />" + pNL
            + "\t\t<wsdl:operation name=\"executeJob\">" + pNL + "\t\t\t<soap:operation soapAction=\"\" style=\"document\" />"
            + pNL + "\t\t\t" + pNL + "\t\t\t<wsdl:input name=\"executeJob\">" + pNL + "\t\t\t\t<soap:body use=\"literal\" />"
            + pNL + "\t\t\t</wsdl:input>" + pNL + "\t\t\t" + pNL + "\t\t\t<wsdl:output name=\"executeJobResponse\">" + pNL
            + "\t\t\t\t<soap:body use=\"literal\" />" + pNL + "\t\t\t</wsdl:output>" + pNL + "\t\t\t" + pNL
            + "\t\t\t<wsdl:fault name=\"TalendBusinessException\">" + pNL
            + "\t\t\t\t<soap:fault name=\"TalendBusinessException\" use=\"literal\" />" + pNL + "\t\t\t</wsdl:fault>" + pNL
            + "\t\t\t" + pNL + "\t\t\t<wsdl:fault name=\"TalendTechnicalException\">" + pNL
            + "\t\t\t\t<soap:fault name=\"TalendTechnicalException\" use=\"literal\" />" + pNL + "\t\t\t</wsdl:fault>" + pNL
            + "\t\t\t" + pNL + "\t\t</wsdl:operation>" + pNL + "\t</wsdl:binding>" + pNL + "\t" + pNL + "\t<wsdl:service name=\"";

    protected final String pTEXT39 = "Service\">" + pNL + "\t\t<wsdl:port name=\"";

    protected final String pTEXT40 = "\" binding=\"tns:";

    protected final String pTEXT41 = "ServiceSoapBinding\">" + pNL
            + "\t\t\t<soap:address location=\"http://localhost:9090/petals.does.not.use.it\" />" + pNL + "\t\t</wsdl:port>" + pNL
            + "\t</wsdl:service>" + pNL + "</wsdl:definitions>";

    protected final String pTEXT42 = pNL;

    public String generate(Object argument) {
        final StringBuffer stringBuffer = new StringBuffer();

        PetalsWsdlBean bean = (PetalsWsdlBean) argument;

        stringBuffer.append(pTEXT1);
        stringBuffer.append(bean.jobName);
        stringBuffer.append(pTEXT2);

        for (ContextTypeDefinition def : bean.contextDefinitions) {
            if (def.exportType == ContextExportType.PARAMETER || def.exportType == ContextExportType.PARAMETER_AND_OUT_ATTACHMENT) {

                stringBuffer.append(pTEXT3);
                stringBuffer.append(def.definition.name);
                stringBuffer.append(pTEXT4);
                stringBuffer.append(def.definition.type);
                stringBuffer.append(pTEXT5);

                if (def.definition.defaultValue != null && def.definition.defaultValue.trim().length() > 0) {

                    stringBuffer.append(pTEXT6);
                    stringBuffer.append(def.definition.defaultValue);
                    stringBuffer.append(pTEXT7);

                }

                stringBuffer.append(pTEXT8);

            }
        }

        stringBuffer.append(pTEXT9);

        for (ContextTypeDefinition def : bean.contextDefinitions) {
            if (def.exportType == ContextExportType.IN_ATTACHMENT) {

                stringBuffer.append(ppTEXT10);
                stringBuffer.append(def.definition.name);
                stringBuffer.append(ppTEXT11);

            }
        }

        stringBuffer.append(ppTEXT12);

        for (ElementTypeDefinition def : bean.tPetalsInputSchema) {

            stringBuffer.append(ppTEXT13);
            stringBuffer.append(def.name);
            stringBuffer.append(ppTEXT14);
            stringBuffer.append(def.type);
            stringBuffer.append(ppTEXT15);

            if (def.defaultValue != null) {
                stringBuffer.append(ppTEXT16);
                stringBuffer.append(def.defaultValue);
                stringBuffer.append(ppTEXT17);
            } else if (def.nillable) {
                stringBuffer.append(ppTEXT18);
            }
            stringBuffer.append(ppTEXT19);

        } // End of "for" loop

        stringBuffer.append(pTEXT20);

        for (ContextTypeDefinition def : bean.contextDefinitions) {
            if (def.exportType == ContextExportType.OUT_ATTACHMENT
                    || def.exportType == ContextExportType.PARAMETER_AND_OUT_ATTACHMENT) {

                stringBuffer.append(pTEXT21);
                stringBuffer.append(def.definition.name);
                stringBuffer.append(pTEXT22);

            }
        }

        stringBuffer.append(pTEXT23);

        for (ElementTypeDefinition def : bean.tPetalsOutputSchema) {

            stringBuffer.append(pTEXT24);
            stringBuffer.append(def.name);
            stringBuffer.append(pTEXT25);
            stringBuffer.append(def.type);
            stringBuffer.append(pTEXT26);

            if (def.defaultValue != null) {
                stringBuffer.append(pTEXT27);
                stringBuffer.append(def.defaultValue);
                stringBuffer.append(pTEXT28);
            } else if (def.nillable) {
                stringBuffer.append(pTEXT29);
            } else {
                stringBuffer.append(pTEXT30);
                stringBuffer.append(def.minOccurs);
                stringBuffer.append(pTEXT31);
            }
            if (def.maxOccurs > 1) {
                stringBuffer.append(pTEXT32);
                stringBuffer.append(def.maxOccurs);
                stringBuffer.append(pTEXT33);
            }
            stringBuffer.append(pTEXT34);

        } // end of "for" loop

        stringBuffer.append(pTEXT35);
        stringBuffer.append(bean.jobName);
        stringBuffer.append(pTEXT36);
        stringBuffer.append(bean.jobName);
        stringBuffer.append(pTEXT37);
        stringBuffer.append(bean.jobName);
        stringBuffer.append(pTEXT38);
        stringBuffer.append(bean.jobName);
        stringBuffer.append(pTEXT39);
        stringBuffer.append(bean.autogenerate ? "autogenerate" : (bean.jobName + "Endpoint"));
        stringBuffer.append(pTEXT40);
        stringBuffer.append(bean.jobName);
        stringBuffer.append(pTEXT41);
        stringBuffer.append(pTEXT42);
        return stringBuffer.toString();
    }
}
