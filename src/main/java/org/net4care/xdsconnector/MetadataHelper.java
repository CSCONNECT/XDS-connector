package org.net4care.xdsconnector;

import org.net4care.xdsconnector.service.*;
//import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import org.springframework.util.StringUtils;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class MetadataHelper {
  private static SimpleDateFormat xdsDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  private static ObjectFactory rimObjectFactory = new ObjectFactory();

  private String repositoryUniqueId;
  private XPathFactory xpathFactory = XPathFactory.newInstance();
  private XPath xpath = xpathFactory.newXPath();

  String className = "ExampleDocument";

  //
  // getter and setters
  //
  public String getClassName() { return className; }
  public void setClassName(String className) { this.className = className; }

  //
  // public methods
  //
  public boolean buildCda(String xml, String homeCommunityId) {
    Document cda;
    byte[] bytes;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      bytes = xml.getBytes();
      cda = builder.parse(new ByteArrayInputStream(bytes));
    }
    catch (Exception ex) {
      return false;
    }

    // Follows XDS metadata specification
    // XPath code is optimized for readability, not performance

    // 2.2.1.1 authorInstitution, mandatory
    String organizationName = getString(cda, "ClinicalDocument/author/assignedAuthor/representedOrganization/name");
    String authorCodeSystem = getString(cda, "ClinicalDocument/author/assignedAuthor/id/@root");
    String authorCode = getString(cda, "ClinicalDocument/author/assignedAuthor/id/@extension");
    createAuthorInstitution(organizationName, authorCode, authorCodeSystem);

    // 2.2.1.2 authorPerson, mandatory
    String authorLastName = getString(cda, "ClinicalDocument/author/assignedAuthor/assignedPerson/name/family");
    List<String> authorGivenNames = getStrings(cda, "ClinicalDocument/author/assignedAuthor/assignedPerson/name/given");
    String authorFirstName = (authorGivenNames.size() > 0) ? authorGivenNames.remove(0) : "";
    createAuthorPerson(authorLastName, authorFirstName, (String[]) authorGivenNames.toArray());

    // 2.2.2 availabilityStatus, mandatory
    createApprovedAvailabilityStatus();

    // 2.2.3 classCode, mandatory
    createClinicalDocumentClassCode(className); // TODO

    // 2.2.4 comments, not used

    // 2.2.5 confidentialityCode, mandatory
    String confidentialityCode = getString(cda, "ClinicalDocument/confidentialityCode/@code");
    createConfidentialityCode(className, confidentialityCode); // TODO

    // 2.2.6 contentTypeCode, not used

    // 2.2.7 creationTime, mandatory
    String creationTime = getString(cda, "ClinicalDocument/effectiveTime/@value");
    createCreationTime(creationTime);

    // 2.2.8 entryUUID, mandatory
    createEntityUUID(""); // TODO

    // 2.2.9 eventCodeList, required when known
    List<String> eventCodes = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/Code/@code");
    List<String> eventNames = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/Code/@displayName");
    List<String> eventCodeSystems = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/Code/@codeSystem");
    int eventSize = Math.min(eventCodes.size(), Math.min(eventNames.size(), eventCodeSystems.size()));
    // log warning at different sizes
    for (int i=0; i<eventSize; i++) {
      createEventCodeList(className, eventCodeSystems.get(i), eventCodes.get(i), eventNames.get(i));
    }

    // 2.2.10 formatCode, mandatory
    createFormatCode(className);

    // 2.2.11 hash, filled by the repository

    // 2.2.12 healthcareFacilityTypeCode, mandatory
    // TODO: can we take facility codes from the custodian
    String facilityCode = getString(cda, "ClinicalDocument/Custodian/id/@root");
    String facilityId = getString(cda, "ClinicalDocument/Custodian/id/@extension");
    String facilityName = ""; // TODO
    createHealthcareFacilityTypeCode(className, facilityCode, facilityId, facilityName);

    // 2.2.13 homeCommunityId, mandatory
    createHomeCommunityId(homeCommunityId);

    // 2.2.14 intendedRecepient, not used

    // 2.2.15 launguageCode, mandatory
    String languageCode = getString(cda, "ClinicalDocument/languageCode/@code");
    createLanguageCode(languageCode);

    // 2.2.16 legalAuthenticator, required when known
    String legalLastName = getString(cda, "ClinicalDocument/legalAuthenticator/assignedEntity/assignedPerson/name/family");
    List<String> legalGivenNames = getStrings(cda, "ClinicalDocument/legalAuthenticator/assignedEntity/assignedPerson/name/given");
    String legalFirstName = (legalGivenNames.size() > 0) ? legalGivenNames.remove(0) : "";
    if (legalLastName.length() > 0 || legalFirstName.length() > 0 || legalGivenNames.size() > 0) {
      createLegalAuthenticator(legalLastName, legalFirstName, (String[]) legalGivenNames.toArray());
    }

    // 2.2.17 limitedMetadata, not used

    // 2.2.18 mimeType, mandatory
    // text/xml specified in createExtrinsicObject

    // 2.2.19 objectType, mandatory
    createObjectType();

    // 2.2.20 patientId, mandatory
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@root");
    createPatientId(patientCodeSystem, patientId);

    // 2.2.21 practiveSettingCode, not used

    // 2.2.22 referenceIdList, optional
    // TODO: ignored for now

    // 2.2.23 repositoryUniqueId, mandatory
    createRepositoryUniqueId(repositoryUniqueId);

    // 2.2.24 serviceStartTime, required when known
    String serviceStartTime = getString(cda, "ClinicalDocument/documentationOf/serviceEvent/effectiveTime/low/@value");
    if (serviceStartTime.length() > 0) createServiceStartTime(serviceStartTime);

    // 2.2.25 serviceStopTime, required when known
    String serviceStopTime = getString(cda, "ClinicalDocument/documentationOf/serviceEvent/effectiveTime/high/@value");
    if (serviceStopTime.length() > 0) createServiceStopTime(serviceStopTime);

    // 2.2.26 size, mandatory
    createSize(bytes.length);

    // 2.2.27 sourceId, not used

    // 2.2.28 sourcePatientId, mandatory
    // reuse parameters from patientId
    createSourcePatientId(patientCodeSystem, patientId);

    // 2.2.29 sourcePatientInfo, mandatory
    String patientLastName = getString(cda, "ClinicalDocument/recordTarget/patientRole/patient/name/family");
    List<String> patientGivenNames = getStrings(cda, "ClinicalDocument/recordTarget/patientRole/patient/name/given");
    String patientFirstName = (legalGivenNames.size() > 0) ? legalGivenNames.remove(0) : "";
    String patientBirthTime = getString(cda, "ClinicalDocument/recordTarget/patientRole/patient/birthTime/@value");
    String patientGender = getString(cda, "ClinicalDocument/recordTarget/patientRole/patient/administrativeGenderCode/@code");
    createSourcePatientInfo(patientBirthTime, patientGender, patientLastName, patientFirstName, (String[]) patientGivenNames.toArray());

    // 2.2.30 submissionTime, mandatory
    String submissionTime = xdsDateFormat.format(new Date());
    createSubmissionTime(submissionTime);

    // 2.2.31 title, mandatory
    String title = getString(cda, "ClinicalDocument/title");
    createTitle(title);

    // 2.2.32 typeCode, mandatory
    String typeCode = getString(cda, "ClinicalDocument/Code/@code");
    String typeName = getString(cda, "ClinicalDocument/Code/@displayName");
    String typeSystem = getString(cda, "ClinicalDocument/Code/@codeSystem");
    createTypeCode(className, typeSystem, typeCode, typeName);

    // 2.2.33 uniqueId, mandatory
    String root = getString(cda, "ClinicalDocument/id/@root");
    String extension = getString(cda, "ClinicalDocument/id/@extension");
    createUniqueId(className, root, extension);

    return true;
  }


  public SlotType1 createAuthorInstitution(String displayName, String code, String codeSystem) {
    String value = String.format("%s^^^^^&%s&ISO^^^^%s", displayName, codeSystem, code);
    return createSlot("authorInstitution", value);
  }

  public SlotType1 createAuthorPerson(String lastName, String firstName, String... middleNames) {
    String value = String.format("^%s^%s^%s^^^^^&ISO", lastName, firstName, StringUtils.arrayToDelimitedString(middleNames, "&"));
    return createSlot("authorPerson", value);
  }

  public ExtrinsicObjectType createApprovedAvailabilityStatus() {
    ExtrinsicObjectType extobj = createExtrinsicObject("fbeacdb7-5421-4474-9267-985007cd8855", "7edca82f-054d-47f2-a032-9b2a5b5186c1");
    extobj.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
    return extobj;
  }

  public ClassificationType createClinicalDocumentClassCode(String className) {
    String valueId = "001"; // Klinisk rapport
    String valueName = "Klinisk rapport";
    return createClassification("41a5887f-8865-4c09-adf7-e362475b143a", className, "2.16.840.1.113883.4208.100.9", valueId, valueName);
  }

  public ClassificationType createConfidentialityCode(String className, String valueId) {
    if (StringUtils.hasLength(valueId)) valueId="N";
    String valueName = confidialityCode2DisplayName(valueId);
    return createClassification("f4f85eac-e6cb-4883-b524-f2705394840f", className, "2.16.840.1.113883.5.25", valueId, valueName);
  }

  public SlotType1 createCreationTime(Date creationTime) {
    return createCreationTime(xdsDateFormat.format(creationTime));
  }

  public SlotType1 createCreationTime(String creationTime) {
    return createSlot("creationTime", creationTime);
  }
  public ExtrinsicObjectType createEntityUUID(String id) {
    return createExtrinsicObject(id, "7edca82f-054d-47f2-a032-9b2a5b5186c1");
  }

  public ClassificationType createEventCodeList(String className, String eventCodeSystem, String eventCode, String eventName) {
    return createClassification("2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4", className, eventCodeSystem, eventCode, eventName);
  }

  public ClassificationType createFormatCode(String className) {
    String valueId = "DK PHMR schema";
    String valueName = "DK PHMR schema";
    return createClassification("a09d5840-386c-46f2-b5ad-9c3699a4309d", className, "urn:ad:dk:medcom:phmr:full", valueId, valueName);
  }

  public ClassificationType createHealthcareFacilityTypeCode(String className, String facilityCode, String facilityId, String facilityName) {
    return createClassification("f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1", className, facilityCode, facilityId, facilityName);
  }

  public ExtrinsicObjectType createHomeCommunityId(String homeCommunityId) {
    ExtrinsicObjectType extobj = rimObjectFactory.createExtrinsicObjectType();
    extobj.setHome(prefixOid(homeCommunityId));
    return extobj;
  }

  public SlotType1 createLanguageCode(String languageCode) {
    return createSlot("languageCode", languageCode);
  }

  public SlotType1 createLegalAuthenticator(String lastName, String firstName, String... middleNames) {
    String value = String.format("^%s^%s^%s^^^^^^^&&ISO", lastName, firstName, StringUtils.arrayToDelimitedString(middleNames, "&"));
    return createSlot("legalAuthenticator", value);
  }

  public ExtrinsicObjectType createObjectType() {
    return createExtrinsicObject("a6e06ca8-0c75-4064-9e5c-88b9045a96f6", "7edca82f-054d-47f2-a032-9b2a5b5186c1");
  }

  public SlotType1 createPatientId(String patientCodeSystem, String patientId) {
    String value = String.format("%s^^^&%s&ISO", patientId, patientCodeSystem);
    return createSlot("patientId", value);
  }

  public SlotType1 createRepositoryUniqueId(String repositoryUniqueId) {
    return createSlot("repositoryUniqueId", repositoryUniqueId);
  }

  public SlotType1 createServiceStartTime(Date serviceStartTime) {
    return createServiceStartTime(xdsDateFormat.format(serviceStartTime));
  }

  public SlotType1 createServiceStartTime(String serviceStartTime) {
    return createSlot("serviceStartTime", serviceStartTime);
  }

  public SlotType1 createServiceStopTime(Date serviceStopTime) {
    return createServiceStopTime(xdsDateFormat.format(serviceStopTime));
  }

  public SlotType1 createServiceStopTime(String serviceStopTime) {
    return createSlot("serviceStopTime", serviceStopTime);
  }

  public SlotType1 createSize(int size) {
    return createSlot("size", Integer.toString(size));
  }

  public SlotType1 createSourcePatientId(String patientCodeSystem, String patientId) {
    String value = String.format("%s^^^&%s&ISO", patientId, patientCodeSystem);
    return createSlot("sourcePatientId", value);
  }

  public SlotType1 createSourcePatientInfo(String patientBirthTime, String patientGender, String patientLastName, String patientFirstName, String... patientMiddleNames) {
    String patientMiddleName = StringUtils.arrayToDelimitedString(patientMiddleNames, "&");
    String value = String.format("%s^%s^%s^^^%s^%s", patientLastName, patientFirstName, patientMiddleName, patientBirthTime, patientGender);
    return createSlot("sourcePatientInfo", value);
  }

  public SlotType1 createSubmissionTime(Date submissionTime) {
    return createSubmissionTime(xdsDateFormat.format(submissionTime));
  }

  public SlotType1 createSubmissionTime(String submissionTime) {
    return createSlot("submissionTime", submissionTime);
  }

  public ExtrinsicObjectType createTitle(String title) {
    ExtrinsicObjectType extobj = rimObjectFactory.createExtrinsicObjectType();
    // TODO: extobj.setId(prefixId(UUID.randomUUID().toString()));
    extobj.setMimeType("text/xml");
    extobj.setObjectType(prefixUuid("7edca82f-054d-47f2-a032-9b2a5b5186c1"));
    extobj.setName(rimObjectFactory.createInternationalStringType());
    LocalizedStringType localizedName = rimObjectFactory.createLocalizedStringType();
    localizedName.setValue(title);
    extobj.getName().getLocalizedString().add(localizedName);
    return extobj;
  }

  public ClassificationType createTypeCode(String className, String typeSystem, String typeCode, String typeName) {
    return createClassification("f0306f51-975f-434e-a61c-c59651d33983", className, typeSystem, typeCode, typeName);
  }

  public ExternalIdentifierType createUniqueId(String className, String root, String extension) {
    String value = String.format("%s^%s", root, extension);
    return createExternalIdentifier("2e82c1f6-a085-4c72-9da3-8640a32e42ab", "DocumentEntry01", "XDSDocumentEntry.uniqueId", value);
  }

  //
  // private methods
  //
  private String confidialityCode2DisplayName(String code) {
    // see http://hl7.org/fhir/v3/Confidentiality
    switch (code.toUpperCase()) {
      case "U": return "Unrestricted";
      case "L": return "Low";
      case "M": return "Moderate";
      case "N": return "Normal";
      case "R": return "Restricted";
      case "V": return "Very restricted";
      default: return "";
    }
  }

  private String prefixOid(String id) {
    if (id == null) id = "";
    return (id.startsWith("urn:oid:")) ? id : "urn:oid:" + id;
  }

  private String prefixUuid(String id) {
    if (id == null) id = "";
    return (id.startsWith("urn:uuid:")) ? id : "urn:uuid:" + id;
  }

  private String getString(Document cda, String exp) {
    try {
      return (String) xpath.evaluate(exp, cda, XPathConstants.STRING);
    }
    catch (XPathExpressionException ex) {
      return "";
    }
  }

  private List<String> getStrings(Document cda, String exp) {
    List<String> list = new ArrayList<>();
    try {
      NodeList nodes = (NodeList) xpath.evaluate(exp, cda, XPathConstants.NODESET);
      for (int i=0; i>nodes.getLength(); i++) list.add(nodes.item(i).getNodeValue());
    }
    catch (XPathExpressionException ex) {
    }
    return list;
  }

  // Generates:
  // <rim:Slot name="{name}">
  //   <rim:ValueList>
  //     <rim:Value>{value}</rim:Value>
  //     ...
  //   </rim:ValueList>
  // </rim:Slot>
  private SlotType1 createSlot(String name, String... values) {
    SlotType1 slotEntry = rimObjectFactory.createSlotType1();
    slotEntry.setName(name);
    slotEntry.setValueList(rimObjectFactory.createValueListType());
    for (String value : values) {
      slotEntry.getValueList().getValue().add(value);
    }
    return slotEntry;
  }

  // Generates
  // <ExtrinsicObject id="{id}" objectType="{objectType}" />
  private ExtrinsicObjectType createExtrinsicObject(String id, String objectType) {
    ExtrinsicObjectType extrinsicObjectType = rimObjectFactory.createExtrinsicObjectType();
    extrinsicObjectType.setId(prefixUuid(id));
    extrinsicObjectType.setMimeType("text/xml");
    extrinsicObjectType.setObjectType(prefixUuid(objectType));
    return extrinsicObjectType;
  }

  // <rim:Classification classificationScheme="{classScheme}" classifiedObject="{classObject}" id="{generated}"
  //   objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
  //   nodeRepresentation="{nodeRepresentation}">
  //   <rim:Name>
  //     <rim:LocalizedString value="{value}"/>
  //   </rim:Name>
  //   <rim:Slot name="codingScheme">
  //     <rim:ValueList>
  //       <rim:Value>{codingScheme}</rim:Value>
  //     </rim:ValueList>
  //   </rim:Slot>
  // </rim:Classification>
  private ClassificationType createClassification(String classScheme, String classObject, String codingScheme, String nodeRepresentation, String name) {
    ClassificationType classification = rimObjectFactory.createClassificationType();
    classification.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification");
    classification.setId(UUID.randomUUID().toString());
    classification.setClassificationScheme(prefixUuid(classScheme));
    classification.setClassifiedObject(classObject);
    if (nodeRepresentation != null) {
      classification.setNodeRepresentation(nodeRepresentation);
    }
    if (name != null) {
      classification.setName(rimObjectFactory.createInternationalStringType());
      LocalizedStringType localizedName = rimObjectFactory.createLocalizedStringType();
      localizedName.setValue(name);
      classification.getName().getLocalizedString().add(localizedName);
    }
    if (codingScheme != null) {
      SlotType1 slotEntry = createSlot("codingScheme", codingScheme);
      classification.getSlot().add(slotEntry);
    }
    return classification;
  }

  // Generates
  // <rim:ExternalIdentifier identificationScheme="{idScheme}" value="{value}"
  //   id="{generated}"
  //   objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier"
  //   registryObject="{registryObject}">
  //   <rim:Name>
  //     <rim:LocalizedString value="{name}"/>
  //   </rim:Name>
  // </rim:ExternalIdentifier>
  private ExternalIdentifierType createExternalIdentifier(String idScheme, String registryObject, String name, String value) {
    ExternalIdentifierType externalIdentifier = rimObjectFactory.createExternalIdentifierType();
    externalIdentifier.setId(UUID.randomUUID().toString());
    externalIdentifier.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier");
    externalIdentifier.setIdentificationScheme(prefixUuid(idScheme));
    externalIdentifier.setRegistryObject(registryObject);
    externalIdentifier.setName(rimObjectFactory.createInternationalStringType());
    LocalizedStringType localizedName = rimObjectFactory.createLocalizedStringType();
    localizedName.setValue(name);
    externalIdentifier.getName().getLocalizedString().add(localizedName);
    externalIdentifier.setValue(value);
    return externalIdentifier;
  }
}
