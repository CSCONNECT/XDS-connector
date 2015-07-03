package org.net4care.xdsconnector;

import org.net4care.xdsconnector.Constants.OID;
import org.net4care.xdsconnector.Constants.UUID;
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
  private static ObjectFactory factory = new ObjectFactory();

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

  public SubmitObjectsRequest buildSubmitObjectsRequest(String xml, String homeCommunityId) {
    Document cda;
    byte[] bytes; // outside try-catch to give byte size below
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      bytes = xml.getBytes();
      cda = builder.parse(new ByteArrayInputStream(bytes));
    }
    catch (Exception ex) {
      return null;
    }

    // Follows XDS metadata specification
    // XPath code is optimized for readability, not performance

    // ITI Vol3 4.3.1.2.2 SubmitObjectsRequest has
    //  - SubmissionSet metadata
    //  - DocumentEntry metadata
    //  - Folders to be created
    //  - Associations (various relationships and HasMember as needed)
    // the aspects are held in a RegistryObjectList

    String submissionSetId = java.util.UUID.randomUUID().toString();

    SubmitObjectsRequest request = new SubmitObjectsRequest();
    RegistryObjectListType registry = factory.createRegistryObjectListType();
    request.setRegistryObjectList(registry);

    // DocumentEntry
    ExtrinsicObjectType entry = createStableDocumentEntry(cda, bytes.length);
    registry.getIdentifiable().add(factory.createExtrinsicObject(entry));

    // RegistryPackage
    RegistryPackageType registryPackage = createRegistryPackage(cda, submissionSetId);
    registry.getIdentifiable().add(factory.createRegistryPackage(registryPackage));

    return request;
  }

  public ExtrinsicObjectType createStableDocumentEntry(Document cda, int size) {
    ExtrinsicObjectType documentEntry = createStableDocumentEntryObject(cda);

    documentEntry.getSlot().add(createSize(size));
    documentEntry.getSlot().add(createLanguageCode(cda));
    documentEntry.getSlot().add(createCreationTime(cda));
    documentEntry.getSlot().add(createServiceStartTime(cda));
    documentEntry.getSlot().add(createServiceStopTime(cda));
    documentEntry.getSlot().add(createSourcePatientId(cda));
    documentEntry.getSlot().add(createSourcePatientInfo(cda));
    documentEntry.getSlot().add(createPatientId(cda));

    documentEntry.getClassification().add(createPHMRFormatCode());
    documentEntry.getClassification().add(createClinicalDocumentClassCode());
    documentEntry.getClassification().add(createTypeCode(cda));
    documentEntry.getClassification().add(createConfidentialityCode(cda));
    documentEntry.getClassification().add(createHealthcareFacilityTypeCode(cda));
    // practiceSettingCode not used

    documentEntry.getExternalIdentifier().add(createDocumentEntryUniqueId(cda));
    documentEntry.getExternalIdentifier().add(createDocumentEntryPatientId(cda));
    // TODO: event codes

    return documentEntry;
  }

  public ExtrinsicObjectType createStableDocumentEntryObject(Document cda) {
    String title = getString(cda, "ClinicalDocument/title");
    String entityUUID = java.util.UUID.randomUUID().toString(); // TODO: get from CDA?
    return createStableDocumentEntryObject(entityUUID, title);
  }

  public ExtrinsicObjectType createStableDocumentEntryObject(String entityUUID, String title) {
    ExtrinsicObjectType extobj = factory.createExtrinsicObjectType();
    extobj.setObjectType(UUID.DocumentEntry.stableDocument);
    extobj.setMimeType("text/xml");
    extobj.setId(prefixUUID(entityUUID));
    extobj.setName(createInternationalString(title));
    extobj.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
    return extobj;
  }

  public RegistryPackageType createRegistryPackage(Document cda, String submissionSetId) {
    RegistryPackageType registryPackage = factory.createRegistryPackageType();

    registryPackage.setId(submissionSetId);
    registryPackage.getSlot().add(createSubmissionTime());
    // TODO: registryPackage.setName(createInternationalString(submissionSetName));
    // contentTypeCode not used
    registryPackage.getExternalIdentifier().add(createSubmissionSetUniqueId(cda));
    registryPackage.getExternalIdentifier().add(createSubmissionSetPatientId(cda));
    // sourceId not used

    registryPackage.getSlot().add(createAuthorInstitution(cda));
    registryPackage.getSlot().add(createAuthorPerson(cda));

    return registryPackage;
  }

  // 2.2.1.1 authorInstitution, mandatory
  public SlotType1 createAuthorInstitution(Document cda) {
    String organizationName = getString(cda, "ClinicalDocument/author/assignedAuthor/representedOrganization/name");
    String authorCodeSystem = getString(cda, "ClinicalDocument/author/assignedAuthor/id/@root");
    String authorCode = getString(cda, "ClinicalDocument/author/assignedAuthor/id/@extension");
    return createAuthorInstitution(organizationName, authorCode, authorCodeSystem);
  }

  public SlotType1 createAuthorInstitution(String displayName, String code, String codeSystem) {
    String value = String.format("%s^^^^^&%s&ISO^^^^%s", displayName, codeSystem, code);
    return createSlot("authorInstitution", value);
  }

  // 2.2.1.2 authorPerson, mandatory
  public SlotType1 createAuthorPerson(Document cda) {
    String authorLastName = getString(cda, "ClinicalDocument/author/assignedAuthor/assignedPerson/name/family");
    List<String> authorGivenNames = getStrings(cda, "ClinicalDocument/author/assignedAuthor/assignedPerson/name/given");
    String authorFirstName = (authorGivenNames.size() > 0) ? authorGivenNames.remove(0) : "";
    return createAuthorPerson(authorLastName, authorFirstName, (String[]) authorGivenNames.toArray());
  }

  public SlotType1 createAuthorPerson(String lastName, String firstName, String... middleNames) {
    String value = String.format("^%s^%s^%s^^^^^&ISO", lastName, firstName, StringUtils.arrayToDelimitedString(middleNames, "&"));
    return createSlot("authorPerson", value);
  }

  // 2.2.2 availabilityStatus, mandatory
  @Deprecated
  public ExtrinsicObjectType createApprovedAvailabilityStatus() {
    ExtrinsicObjectType extobj = createExtrinsicObject("fbeacdb7-5421-4474-9267-985007cd8855", "7edca82f-054d-47f2-a032-9b2a5b5186c1");
    extobj.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
    return extobj;
  }

  // 2.2.3 classCode, mandatory
  public ClassificationType createClinicalDocumentClassCode() {
    return createClassification(UUID.DocumentEntry.classCode, className, OID.DK.ClassCode_ClinicalReport_Code, OID.DK.ClassCode_ClinicalReport_DisplayName, OID.DK.ClassCode);
  }

  // 2.2.4 comments, not used

  // 2.2.5 confidentialityCode, mandatory
  public ClassificationType createConfidentialityCode(Document cda) {
    String confidentialityCode = getString(cda, "ClinicalDocument/confidentialityCode/@code");
    return createConfidentialityCode(confidentialityCode); // TODO
  }

  public ClassificationType createConfidentialityCode(String valueId) {
    if (StringUtils.hasLength(valueId)) valueId="N";
    String valueName = confidialityCode2DisplayName(valueId);
    return createClassification(UUID.DocumentEntry.confidentialityCode, className, valueId, valueName, OID.HL7.Confidentiality);
  }

  // 2.2.6 contentTypeCode, not used

  // 2.2.7 creationTime, mandatory
  public SlotType1 createCreationTime(Document cda) {
    String creationTime = getString(cda, "ClinicalDocument/effectiveTime/@value");
    return createCreationTime(creationTime);
  }

  public SlotType1 createCreationTime(Date creationTime) {
    return createCreationTime(xdsDateFormat.format(creationTime));
  }

  public SlotType1 createCreationTime(String creationTime) {
    return createSlot("creationTime", creationTime);
  }

  // 2.2.8 entryUUID, mandatory
  @Deprecated
  public ExtrinsicObjectType createEntityUUID(String id) {
    return createExtrinsicObject(id, "7edca82f-054d-47f2-a032-9b2a5b5186c1");
  }

  // 2.2.9 eventCodeList, required when known
  public List<ClassificationType> createEventCodeList(Document cda) {
    List<String> eventCodes = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/Code/@code");
    List<String> eventNames = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/Code/@displayName");
    List<String> eventCodeSystems = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/Code/@codeSystem");
    int eventSize = Math.min(eventCodes.size(), Math.min(eventNames.size(), eventCodeSystems.size()));
    // log warning at different sizes
    List<ClassificationType> list = new ArrayList<ClassificationType>();
    for (int i=0; i<eventSize; i++) {
      list.add(createEventCodeList(eventCodeSystems.get(i), eventCodes.get(i), eventNames.get(i)));
    }
    return list;
  }

  public ClassificationType createEventCodeList(String eventCodeSystem, String eventCode, String eventName) {
    return createClassification(UUID.DocumentEntry.eventCodeList, className, eventCode, eventName, eventCodeSystem);
  }

  // 2.2.10 formatCode, mandatory
  public ClassificationType createPHMRFormatCode() {
    return createClassification(UUID.DocumentEntry.formatCode, className, OID.DK.FormatCode_PHMR_Code, OID.DK.FormatCode_PHMR_DisplayName, OID.DK.FormatCode);
  }

  // 2.2.11 hash, filled by the repository

  // 2.2.12 healthcareFacilityTypeCode, mandatory
  public ClassificationType createHealthcareFacilityTypeCode(Document cda) {
    // TODO: can we take facility codes from the custodian
    String facilityCode = getString(cda, "ClinicalDocument/Custodian/id/@root");
    String facilityId = getString(cda, "ClinicalDocument/Custodian/id/@extension");
    String facilityName = healthcareFacilityTypeCode2DisplayName(facilityCode);
    return createHealthcareFacilityTypeCode(facilityCode, facilityId, facilityName);
  }

  public ClassificationType createHealthcareFacilityTypeCode(String facilityCode, String facilityId, String facilityName) {
    return createClassification(UUID.DocumentEntry.healthcareFacilityTypeCode, className, facilityId, facilityName, facilityCode);
  }

  // 2.2.13 homeCommunityId, mandatory
  public ExtrinsicObjectType createHomeCommunityId(String homeCommunityId) {
    ExtrinsicObjectType extobj = factory.createExtrinsicObjectType();
    extobj.setHome(prefixOID(homeCommunityId));
    return extobj;
  }

  // 2.2.14 intendedRecepient, not used

  // 2.2.15 launguageCode, mandatory
  public SlotType1 createLanguageCode(Document cda) {
    String languageCode = getString(cda, "ClinicalDocument/languageCode/@code");
    return createLanguageCode(languageCode);
  }

  public SlotType1 createLanguageCode(String languageCode) {
    return createSlot("languageCode", languageCode);
  }

  // 2.2.16 legalAuthenticator, required when known
  public SlotType1 createLegalAuthenticator(Document cda) {
    String legalLastName = getString(cda, "ClinicalDocument/legalAuthenticator/assignedEntity/assignedPerson/name/family");
    List<String> legalGivenNames = getStrings(cda, "ClinicalDocument/legalAuthenticator/assignedEntity/assignedPerson/name/given");
    String legalFirstName = (legalGivenNames.size() > 0) ? legalGivenNames.remove(0) : "";
    if (legalLastName.length() > 0 || legalFirstName.length() > 0 || legalGivenNames.size() > 0) {
      return createLegalAuthenticator(legalLastName, legalFirstName, (String[]) legalGivenNames.toArray());
    }
    else return null;
  }

  public SlotType1 createLegalAuthenticator(String lastName, String firstName, String... middleNames) {
    String value = String.format("^%s^%s^%s^^^^^^^&&ISO", lastName, firstName, StringUtils.arrayToDelimitedString(middleNames, "&"));
    return createSlot("legalAuthenticator", value);
  }

  // 2.2.17 limitedMetadata, not used

  // 2.2.18 mimeType, mandatory
  // text/xml specified in createExtrinsicObject

  // 2.2.19 objectType, mandatory
  @Deprecated
  public ExtrinsicObjectType createObjectType() {
    return createExtrinsicObject("a6e06ca8-0c75-4064-9e5c-88b9045a96f6", "7edca82f-054d-47f2-a032-9b2a5b5186c1");
  }

  // 2.2.20 patientId, mandatory
  public SlotType1 createPatientId(Document cda) {
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@root");
    return createPatientId(patientCodeSystem, patientId);
  }

  public SlotType1 createPatientId(String patientCodeSystem, String patientId) {
    String value = formatPatientId(patientCodeSystem, patientId);
    return createSlot("patientId", value);
  }

  public ExternalIdentifierType createDocumentEntryPatientId(Document cda) {
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@root");
    return createDocumentEntryPatientId(patientCodeSystem, patientId);
  }

  public ExternalIdentifierType createDocumentEntryPatientId(String patientCodeSystem, String patientId) {
    String value = formatPatientId(patientCodeSystem, patientId);
    return createExternalIdentifier(UUID.DocumentEntry.patientId, "", "XDSDocumentEntry.patientId", value);
  }

  public ExternalIdentifierType createSubmissionSetPatientId(Document cda) {
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@root");
    return createSubmissionSetPatientId(patientCodeSystem, patientId);
  }

  public ExternalIdentifierType createSubmissionSetPatientId(String patientCodeSystem, String patientId) {
    String value = formatPatientId(patientCodeSystem, patientId);
    return createExternalIdentifier(UUID.SubmissionSet.patientId, "", "XDSSubmissionSet.patientId", value);
  }

  public String formatPatientId(String patientCodeSystem, String patientId) {
    return String.format("%s^^^&%s&ISO", patientId, patientCodeSystem);
  }

  public String formatPatientId(Document cda) {
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/Id/@root");
    return formatPatientId(patientCodeSystem, patientId);
  }

  // 2.2.21 practiceSettingCode, not used

  // 2.2.22 referenceIdList, optional
  // TODO: ignored for now

  // 2.2.23 repositoryUniqueId, mandatory
  public SlotType1 createRepositoryUniqueId(String repositoryUniqueId) {
    return createSlot("repositoryUniqueId", repositoryUniqueId);
  }

  // 2.2.24 serviceStartTime, required when known
  public SlotType1 createServiceStartTime(Document cda) {
    String serviceStartTime = getString(cda, "ClinicalDocument/documentationOf/serviceEvent/effectiveTime/low/@value");
    return (serviceStartTime.length() > 0) ? createServiceStartTime(serviceStartTime) : null;
  }

  public SlotType1 createServiceStartTime(Date serviceStartTime) {
    return createServiceStartTime(xdsDateFormat.format(serviceStartTime));
  }

  public SlotType1 createServiceStartTime(String serviceStartTime) {
    return createSlot("serviceStartTime", serviceStartTime);
  }

  // 2.2.25 serviceStopTime, required when known
  public SlotType1 createServiceStopTime(Document cda) {
    String serviceStopTime = getString(cda, "ClinicalDocument/documentationOf/serviceEvent/effectiveTime/high/@value");
    return (serviceStopTime.length() > 0) ? createServiceStopTime(serviceStopTime) : null;
  }

  public SlotType1 createServiceStopTime(Date serviceStopTime) {
    return createServiceStopTime(xdsDateFormat.format(serviceStopTime));
  }

  public SlotType1 createServiceStopTime(String serviceStopTime) {
    return createSlot("serviceStopTime", serviceStopTime);
  }

  // 2.2.26 size, mandatory
  public SlotType1 createSize(int size) {
    return createSlot("size", Integer.toString(size));
  }

  // 2.2.27 sourceId, not used

  // 2.2.28 sourcePatientId, mandatory
  public SlotType1 createSourcePatientId(Document cda) {
    String value = formatPatientId(cda);
    return createSlot("sourcePatientId", value);
  }

  public SlotType1 createSourcePatientId(String patientCodeSystem, String patientId) {
    String value = formatPatientId(patientCodeSystem, patientId);
    return createSlot("sourcePatientId", value);
  }

  // 2.2.29 sourcePatientInfo, mandatory
  public SlotType1 createSourcePatientInfo(Document cda) {
    String patientLastName = getString(cda, "ClinicalDocument/recordTarget/patientRole/patient/name/family");
    List<String> patientGivenNames = getStrings(cda, "ClinicalDocument/recordTarget/patientRole/patient/name/given");
    String patientFirstName = (patientGivenNames.size() > 0) ? patientGivenNames.remove(0) : "";
    String patientBirthTime = getString(cda, "ClinicalDocument/recordTarget/patientRole/patient/birthTime/@value");
    String patientGender = getString(cda, "ClinicalDocument/recordTarget/patientRole/patient/administrativeGenderCode/@code");
    return createSourcePatientInfo(patientBirthTime, patientGender, patientLastName, patientFirstName, (String[]) patientGivenNames.toArray());
  }

  public SlotType1 createSourcePatientInfo(String patientBirthTime, String patientGender, String patientLastName, String patientFirstName, String... patientMiddleNames) {
    String patientMiddleName = StringUtils.arrayToDelimitedString(patientMiddleNames, "&");
    String value = String.format("%s^%s^%s^^^%s^%s", patientLastName, patientFirstName, patientMiddleName, patientBirthTime, patientGender);
    return createSlot("sourcePatientInfo", value);
  }

  // 2.2.30 submissionTime, mandatory
  public SlotType1 createSubmissionTime() {
    String submissionTime = xdsDateFormat.format(new Date());
    return createSubmissionTime(submissionTime);
  }

  public SlotType1 createSubmissionTime(Date submissionTime) {
    return createSubmissionTime(xdsDateFormat.format(submissionTime));
  }

  public SlotType1 createSubmissionTime(String submissionTime) {
    return createSlot("submissionTime", submissionTime);
  }

  // 2.2.31 title, mandatory
  @Deprecated
  public ExtrinsicObjectType createTitle(Document cda) {
    String title = getString(cda, "ClinicalDocument/title");
    return createTitle(title);
  }

  @Deprecated
  public ExtrinsicObjectType createTitle(String title) {
    ExtrinsicObjectType extobj = factory.createExtrinsicObjectType();
    // TODO: extobj.setId(prefixId(UUID.randomUUID().toString()));
    extobj.setMimeType("text/xml");
    extobj.setObjectType(prefixUUID("7edca82f-054d-47f2-a032-9b2a5b5186c1"));
    extobj.setName(createInternationalString(title));
    return extobj;
  }

  // 2.2.32 typeCode, mandatory
  public ClassificationType createTypeCode(Document cda) {
    String typeCode = getString(cda, "ClinicalDocument/Code/@code");
    String typeName = getString(cda, "ClinicalDocument/Code/@displayName");
    String typeSystem = getString(cda, "ClinicalDocument/Code/@codeSystem");
    return createTypeCode(typeSystem, typeCode, typeName);
  }

  public ClassificationType createTypeCode(String typeSystem, String typeCode, String typeName) {
    return createClassification(UUID.DocumentEntry.typeCode, className, typeCode, typeName, typeSystem);
  }

  // 2.2.33 uniqueId, mandatory
  public ExternalIdentifierType createDocumentEntryUniqueId(Document cda) {
    String root = getString(cda, "ClinicalDocument/id/@root");
    String extension = getString(cda, "ClinicalDocument/id/@extension");
    return createDocumentEntryUniqueId(root, extension);
  }

  public ExternalIdentifierType createDocumentEntryUniqueId(String root, String extension) {
    String value = String.format("%s^%s", root, extension);
    return createExternalIdentifier(UUID.DocumentEntry.uniqueId, "", "XDSDocumentEntry.uniqueId", value);
  }

  public ExternalIdentifierType createSubmissionSetUniqueId(Document cda) {
    String root = getString(cda, "ClinicalDocument/id/@root");
    String extension = getString(cda, "ClinicalDocument/id/@extension");
    return createSubmissionSetUniqueId(root, extension);
  }

  public ExternalIdentifierType createSubmissionSetUniqueId(String root, String extension) {
    String value = String.format("%s^%s", root, extension);
    return createExternalIdentifier(UUID.SubmissionSet.uniqueId, "", "XDSSubmissionSet.uniqueId", value);
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

  private String healthcareFacilityTypeCode2DisplayName(String code) {
    switch (code) {
      case "264372000": return "apotek";
      case "20078004": return "behandlingscenter for stofmisbrugere";
      case "554221000005108": return "bosted";
      case "554031000005103": return "diætistklinik";
      case "546821000005103": return "ergoterapiklinik";
      case "547011000005103": return "fysioterapiklinik";
      case "546811000005109": return "genoptræningscenter";
      case "550621000005101": return "hjemmesygepleje";
      case "22232009": return "hospital";
      case "550631000005103": return "jordemoderklinik";
      case "550641000005106": return "kiropraktor klinik";
      case "550651000005108": return "lægelaboratorium";
      case "394761003": return "lægepraksis";
      case "550661000005105": return "lægevagt";
      case "42665001": return "plejehjem";
      case "554211000005102": return "præhospitals enhed";
      case "550711000005101": return "psykologisk rådgivningsklinik";
      case "550671000005100": return "speciallægepraksis";
      case "554061000005105": return "statsautoriseret fodterapeut";
      case "264361005": return "sundhedscenter";
      case "554041000005106": return "sundhedsforvaltning";
      case "554021000005101": return "sundhedspleje";
      case "550681000005102": return "tandlægepraksis";
      case "550691000005104": return "tandpleje klinik";
      case "550701000005104": return "tandteknisk klinik";
      case "554231000005106": return "vaccinationsklinik";
      case "554051000005108": return "zoneterapiklinik";
      default: return "";
    }
  }

  private String prefixOID(String id) {
    if (id == null) id = "";
    return (id.startsWith("urn:oid:")) ? id : "urn:oid:" + id;
  }

  private String prefixUUID(String id) {
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

  private InternationalStringType createInternationalString(String value) {
    InternationalStringType ist = factory.createInternationalStringType();
    LocalizedStringType lst = factory.createLocalizedStringType();
    lst.setValue(value);
    ist.getLocalizedString().add(lst);
    return ist;
  }

  // Generates:
  // <rim:Slot name="{name}">
  //   <rim:ValueList>
  //     <rim:Value>{value}</rim:Value>
  //     ...
  //   </rim:ValueList>
  // </rim:Slot>
  private SlotType1 createSlot(String name, String... values) {
    SlotType1 slotEntry = factory.createSlotType1();
    slotEntry.setName(name);
    slotEntry.setValueList(factory.createValueListType());
    for (String value : values) {
      slotEntry.getValueList().getValue().add(value);
    }
    return slotEntry;
  }

  // Generates
  // <ExtrinsicObject id="{id}" objectType="{objectType}" />
  private ExtrinsicObjectType createExtrinsicObject(String id, String objectType) {
    ExtrinsicObjectType extrinsicObjectType = factory.createExtrinsicObjectType();
    extrinsicObjectType.setId(prefixUUID(id));
    extrinsicObjectType.setMimeType("text/xml");
    extrinsicObjectType.setObjectType(prefixUUID(objectType));
    return extrinsicObjectType;
  }

  // <rim:Classification
  //   objectType="urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"
  //   id="{generated}"
  //   classificationScheme="{scheme}"
  //   classifiedObject="{object}"
  //   nodeRepresentation="{nodeRep}">
  //   <rim:Name>
  //     <rim:LocalizedString value="{name}"/>
  //   </rim:Name>
  //   <rim:Slot name="codingScheme">
  //     <rim:ValueList>
  //       <rim:Value>{value(s)}</rim:Value>
  //     </rim:ValueList>
  //   </rim:Slot>
  // </rim:Classification>
  private ClassificationType createClassification(String scheme, String object, String nodeRep, String name, String... values) {
    ClassificationType classification = factory.createClassificationType();
    classification.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification");
    classification.setId(java.util.UUID.randomUUID().toString());
    classification.setClassificationScheme(prefixUUID(scheme));
    classification.setClassifiedObject(object);
    if (nodeRep != null) {
      classification.setNodeRepresentation(nodeRep);
    }
    if (name != null) {
      classification.setName(createInternationalString(name));
    }
    if (values != null) {
      SlotType1 slotEntry = createSlot("codingScheme", values);
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
    ExternalIdentifierType externalIdentifier = factory.createExternalIdentifierType();
    externalIdentifier.setId(java.util.UUID.randomUUID().toString());
    externalIdentifier.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier");
    externalIdentifier.setIdentificationScheme(prefixUUID(idScheme));
    externalIdentifier.setRegistryObject(registryObject);
    externalIdentifier.setName(createInternationalString(name));
    externalIdentifier.setValue(value);
    return externalIdentifier;
  }
}
