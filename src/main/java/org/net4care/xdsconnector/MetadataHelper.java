package org.net4care.xdsconnector;

import org.net4care.xdsconnector.Constants.COID;
import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.service.*;
import org.springframework.util.StringUtils;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.nio.charset.Charset;
import java.util.*;

public class MetadataHelper {
  private static SimpleDateFormat xdsDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  private static ObjectFactory factory = new ObjectFactory();
  private XPathFactory xpathFactory = XPathFactory.newInstance();
  private XPath xpath = xpathFactory.newXPath();

  private String homeCommunityId;

  public MetadataHelper(String homeCommunityId) {
    this.homeCommunityId = homeCommunityId;
  }

  //
  // public methods
  //

  public SubmitObjectsRequest buildSubmitObjectsRequest(String xml, String associatedId) {
    Document cda;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      byte[] bytes = xml.getBytes(Charset.forName("UTF-8"));
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
    // these aspects are held in a RegistryObjectList

    SubmitObjectsRequest request = new SubmitObjectsRequest();
    RegistryObjectListType registry = factory.createRegistryObjectListType();
    request.setRegistryObjectList(registry);

    // DocumentEntry
    ExtrinsicObjectType entry = createStableDocumentEntry(cda, associatedId);
    registry.getIdentifiable().add(factory.createExtrinsicObject(entry));

    // RegistryPackage
    String submissionSetId = UUID.randomUUID().toString();
    String submissionSetName = getString(cda, "ClinicalDocument/title");
    RegistryPackageType registryPackage = createRegistryPackage(cda, submissionSetId, submissionSetName);
    registry.getIdentifiable().add(factory.createRegistryPackage(registryPackage));

    // Classification
    ClassificationType classification = createClassificationNode(submissionSetId);
    registry.getIdentifiable().add(factory.createClassification(classification));

    // Associations
    AssociationType1 association = createAssociation(submissionSetId, associatedId);
    registry.getIdentifiable().add(factory.createAssociation(association));

    return request;
  }

  public ExtrinsicObjectType createStableDocumentEntry(Document cda, String associatedId) {
    ExtrinsicObjectType documentEntry = createStableDocumentEntryObject(cda, associatedId);

    documentEntry.getSlot().add(createLanguageCode(cda));
    documentEntry.getSlot().add(createCreationTime(cda));
    documentEntry.getSlot().add(createServiceStartTime(cda));
    documentEntry.getSlot().add(createServiceStopTime(cda));
    documentEntry.getSlot().add(createPatientId(cda));
    documentEntry.getSlot().add(createSourcePatientId(cda));
    documentEntry.getSlot().add(createSourcePatientInfo(cda));
    // SlotType1 legalAuthenticator = createLegalAuthenticator(cda);
    // if (legalAuthenticator != null) documentEntry.getSlot().add(legalAuthenticator);

    // TODO: determine format code from template id, using PHMR for now
    documentEntry.getClassification().add(createFormatCode(associatedId, COID.DK.FormatCode_PHMR_Code, COID.DK.FormatCode_PHMR_DisplayName));
    documentEntry.getClassification().add(createClassCode(associatedId));
    documentEntry.getClassification().add(createTypeCode(cda, associatedId));
    documentEntry.getClassification().add(createConfidentialityCode(cda, associatedId));
    documentEntry.getClassification().add(createHealthcareFacilityTypeCode(cda, associatedId));
    documentEntry.getClassification().add(createPracticeSettingCode(associatedId));
    documentEntry.getClassification().addAll(createEventCodeList(cda, associatedId));

    documentEntry.getExternalIdentifier().add(createDocumentEntryUniqueId(cda, associatedId));
    documentEntry.getExternalIdentifier().add(createDocumentEntryPatientId(cda, associatedId));

    return documentEntry;
  }

  public ExtrinsicObjectType createStableDocumentEntryObject(Document cda, String associatedId) {
    String title = getString(cda, "ClinicalDocument/title");
    return createStableDocumentEntryObject(associatedId, title);
  }

  public ExtrinsicObjectType createStableDocumentEntryObject(String associatedId, String title) {
    ExtrinsicObjectType extobj = factory.createExtrinsicObjectType();
    extobj.setObjectType(CUUID.DocumentEntry.stableDocument);
    extobj.setMimeType("text/xml");
    extobj.setId(associatedId);
    extobj.setName(createInternationalString(title));
    extobj.setHome(prefixOID(homeCommunityId));
    extobj.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");
    extobj.setDescription(createInternationalString(title));
    return extobj;
  }

  public RegistryPackageType createRegistryPackage(Document cda, String submissionSetId, String submissionSetName) {
    RegistryPackageType registryPackage = factory.createRegistryPackageType();

    registryPackage.setId(submissionSetId);
    registryPackage.setName(createInternationalString(submissionSetName));

    registryPackage.getSlot().add(createSubmissionTime());

    ClassificationType authorClassification = createClassification(CUUID.SubmissionSet.authorId, submissionSetId, null, null);
    authorClassification.getSlot().add(createAuthorInstitution(cda));
    authorClassification.getSlot().add(createAuthorPerson(cda));
    registryPackage.getClassification().add(authorClassification);

    registryPackage.getClassification().add(createContentTypeCode(submissionSetId));

    registryPackage.getExternalIdentifier().add(createSubmissionSetUniqueId(cda, submissionSetId));
    registryPackage.getExternalIdentifier().add(createSubmissionSetPatientId(cda, submissionSetId));
    registryPackage.getExternalIdentifier().add(createSourceId(submissionSetId));

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
    return createAuthorPerson(authorLastName, authorFirstName, authorGivenNames.toArray(new String[0]));
  }

  public SlotType1 createAuthorPerson(String lastName, String firstName, String... middleNames) {
    String authorMiddleName = StringUtils.arrayToDelimitedString(middleNames, "&");
    String value = String.format("^%s^%s^%s^^^^^&ISO", lastName, firstName, authorMiddleName);
    return createSlot("authorPerson", value);
  }

  // 2.2.2 availabilityStatus, mandatory
  // part of createStableDocumentEntryObject

  // 2.2.3 classCode, mandatory
  public ClassificationType createClassCode(String associatedId) {
    // Allways clinical report
    return createClassification(CUUID.DocumentEntry.classCode, associatedId, COID.DK.ClassCode_ClinicalReport_Code, COID.DK.ClassCode_ClinicalReport_DisplayName, COID.DK.ClassCode);
  }

  // 2.2.4 comments, not used

  // 2.2.5 confidentialityCode, mandatory
  public ClassificationType createConfidentialityCode(Document cda, String associatedId) {
    String confidentialityCode = getString(cda, "ClinicalDocument/confidentialityCode/@code");
    return createConfidentialityCode(associatedId, confidentialityCode);
  }

  public ClassificationType createConfidentialityCode(String associatedId, String valueId) {
    if (StringUtils.hasLength(valueId)) valueId="N";
    String valueName = confidialityCode2DisplayName(valueId);
    return createClassification(CUUID.DocumentEntry.confidentialityCode, associatedId, valueId, valueName, COID.HL7.Confidentiality);
  }

  // 2.2.6 contentTypeCode, not used
  public ClassificationType createContentTypeCode(String submissionSetId) {
    // unused, but required, inserting empty values
    return createClassification(CUUID.SubmissionSet.contentTypeCode, submissionSetId, "", "", "");
  }

  // 2.2.7 creationTime, mandatory
  public SlotType1 createCreationTime(Document cda) {
    String creationTime = getString(cda, "ClinicalDocument/effectiveTime/@value");
    return createCreationTime(creationTime);
  }

  public SlotType1 createCreationTime(Date creationTime) {
    return createCreationTime(xdsDateFormat.format(creationTime));
  }

  public SlotType1 createCreationTime(String creationTime) {
    return createSlot("creationTime", creationTime.substring(0,14));
  }

  // 2.2.8 entryUUID, mandatory
  // part of createStableDocumentEntryObject

  // 2.2.9 eventCodeList, required when known
  public List<ClassificationType> createEventCodeList(Document cda, String associatedId) {
    List<String> eventCodes = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/code/@code");
    List<String> eventNames = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/code/@displayName");
    List<String> eventCodeSystems = getStrings(cda, "ClinicalDocument/documentationOf/serviceEvent/code/@codeSystem");
    int eventSize = Math.min(eventCodes.size(), Math.min(eventNames.size(), eventCodeSystems.size()));
    // log warning at different sizes
    List<ClassificationType> list = new ArrayList<ClassificationType>();
    for (int i=0; i<eventSize; i++) {
      list.add(createEventCodeList(associatedId, eventCodeSystems.get(i), eventCodes.get(i), eventNames.get(i)));
    }
    return list;
  }

  public ClassificationType createEventCodeList(String associatedId, String eventCodeSystem, String eventCode, String eventName) {
    return createClassification(CUUID.DocumentEntry.eventCodeList, associatedId, eventCode, eventName, eventCodeSystem);
  }

  // 2.2.10 formatCode, mandatory
  public ClassificationType createFormatCode(String associatedId, String code, String displayName) {
    return createClassification(CUUID.DocumentEntry.formatCode, associatedId, code, displayName, COID.DK.FormatCode);
  }

  // 2.2.11 hash, mandatory
  public SlotType1 createHash(byte[] bytes) {
    return createSlot("hash", Integer.toString(bytes.hashCode()));
  }

  // 2.2.12 healthcareFacilityTypeCode, mandatory
  public ClassificationType createHealthcareFacilityTypeCode(Document cda, String associatedId) {
    // TODO: info not in CDA, locked to hospital for now?
    String facilityCodeSystem = "2.16.840.1.113883.3.4208.100.11";
    String facilityCode = "22232009";
    String facilityName = healthcareFacilityTypeCode2DisplayName(facilityCode);
    return createHealthcareFacilityTypeCode(associatedId, facilityCodeSystem, facilityCode, facilityName);
  }

  public ClassificationType createHealthcareFacilityTypeCode(String associatedId, String facilityCode, String facilityId, String facilityName) {
    return createClassification(CUUID.DocumentEntry.healthcareFacilityTypeCode, associatedId, facilityId, facilityName, facilityCode);
  }

  // 2.2.13 homeCommunityId, mandatory
  // part of createStableDocumentEntryObject

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
  // part of createStableDocumentEntryObject

  // 2.2.19 objectType, mandatory
  // part of createStableDocumentEntryObject

  // 2.2.20 patientId, mandatory
  public SlotType1 createPatientId(Document cda) {
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/id/@root");
    return createPatientId(patientCodeSystem, patientId);
  }

  public SlotType1 createPatientId(String patientCodeSystem, String patientId) {
    String value = formatPatientId(patientCodeSystem, patientId);
    return createSlot("patientId", value);
  }

  public ExternalIdentifierType createDocumentEntryPatientId(Document cda, String associatedId) {
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/id/@root");
    return createDocumentEntryPatientId(associatedId, patientCodeSystem, patientId);
  }

  public ExternalIdentifierType createDocumentEntryPatientId(String associatedId, String patientCodeSystem, String patientId) {
    String value = formatPatientId(patientCodeSystem, patientId);
    // TODO: using associatedId for registryObject
    return createExternalIdentifier(CUUID.DocumentEntry.patientId, associatedId, "XDSDocumentEntry.patientId", value);
  }

  public ExternalIdentifierType createSubmissionSetPatientId(Document cda, String submissionSetId) {
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/id/@root");
    return createSubmissionSetPatientId(submissionSetId, patientCodeSystem, patientId);
  }

  public ExternalIdentifierType createSubmissionSetPatientId(String submissionSetId, String patientCodeSystem, String patientId) {
    String value = formatPatientId(patientCodeSystem, patientId);
    // TODO: using submissionSetId for registryObject
    return createExternalIdentifier(CUUID.SubmissionSet.patientId, submissionSetId, "XDSSubmissionSet.patientId", value);
  }

  public String formatPatientId(String patientCodeSystem, String patientId) {
    return String.format("%s^^^&%s&ISO", patientId, patientCodeSystem);
  }

  public String formatPatientId(Document cda) {
    String patientId = getString(cda, "ClinicalDocument/recordTarget/patientRole/id/@extension");
    String patientCodeSystem = getString(cda, "ClinicalDocument/recordTarget/patientRole/id/@root");
    return formatPatientId(patientCodeSystem, patientId);
  }

  // 2.2.21 practiceSettingCode, not used
  public ClassificationType createPracticeSettingCode(String associatedId) {
    // unused, but required, inserting empty values
    return createClassification(CUUID.DocumentEntry.practiceSettingCode, associatedId, "", "", "");
  }

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
    return createSlot("serviceStartTime", serviceStartTime.substring(0,14));
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
    return createSlot("serviceStopTime", serviceStopTime.substring(0,14));
  }

  // 2.2.26 size, mandatory
  public SlotType1 createSize(int size) {
    return createSlot("size", Integer.toString(size));
  }

  // 2.2.27 sourceId, not used
  public ExternalIdentifierType createSourceId(String submissionSetId) {
    // TODO: using submissionSetId for registryObject
    return createExternalIdentifier(CUUID.SubmissionSet.sourceId, submissionSetId, "XDSSubmissionSet.sourceId", UUID.randomUUID().toString());
  }

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
    return createSourcePatientInfo(patientBirthTime, patientGender, patientLastName, patientFirstName, patientGivenNames.toArray(new String[0]));
  }

  public SlotType1 createSourcePatientInfo(String patientBirthTime, String patientGender, String patientLastName, String patientFirstName, String... patientMiddleNames) {
    String patientMiddleName = StringUtils.arrayToDelimitedString(patientMiddleNames, "&");
    String value = String.format("%s^%s^%s^^^%s^%s", patientLastName, patientFirstName, patientMiddleName, patientBirthTime.substring(0,8), patientGender);
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
    return createSlot("submissionTime", submissionTime.substring(0,14));
  }

  // 2.2.31 title, mandatory
  // part of createStableDocumentEntryObject

  // 2.2.32 typeCode, mandatory
  public ClassificationType createTypeCode(Document cda, String associatedId) {
    String typeCode = getString(cda, "ClinicalDocument/code/@code");
    String typeName = getString(cda, "ClinicalDocument/code/@displayName");
    String typeSystem = getString(cda, "ClinicalDocument/code/@codeSystem");
    return createTypeCode(associatedId, typeSystem, typeCode, typeName);
  }

  public ClassificationType createTypeCode(String associatedId, String typeSystem, String typeCode, String typeName) {
    return createClassification(CUUID.DocumentEntry.typeCode, associatedId, typeCode, typeName, typeSystem);
  }

  // 2.2.33 uniqueId, mandatory
  public ExternalIdentifierType createDocumentEntryUniqueId(Document cda, String associatedId) {
    String root = getString(cda, "ClinicalDocument/id/@root");
    String extension = getString(cda, "ClinicalDocument/id/@extension");
    return createDocumentEntryUniqueId(associatedId, root, extension);
  }

  public ExternalIdentifierType createDocumentEntryUniqueId(String associatedId, String root, String extension) {
    String value = formatUniqueId(root, extension);
    // TODO: using associatedId for registryObject
    return createExternalIdentifier(CUUID.DocumentEntry.uniqueId, associatedId, "XDSDocumentEntry.uniqueId", value);
  }

  public ExternalIdentifierType createSubmissionSetUniqueId(Document cda, String associatedId) {
    String root = getString(cda, "ClinicalDocument/id/@root");
    String extension = getString(cda, "ClinicalDocument/id/@extension");
    return createSubmissionSetUniqueId(associatedId, root, extension);
  }

  public ExternalIdentifierType createSubmissionSetUniqueId(String associatedId, String root, String extension) {
    String value = formatUniqueId(root, extension);
    // TODO: using associatedId for registryObject
    return createExternalIdentifier(CUUID.SubmissionSet.uniqueId, associatedId, "XDSSubmissionSet.uniqueId", value);
  }

  public String formatUniqueId(Document cda) {
    String root = getString(cda, "ClinicalDocument/id/@root");
    String extension = getString(cda, "ClinicalDocument/id/@extension");
    return formatUniqueId(root, extension);
  }

  public String formatUniqueId(String root, String extension) {
    return String.format("%s^%s", root, extension);
  }

  public ClassificationType createClassificationNode(String submissionSetId) {
    ClassificationType classification = factory.createClassificationType();
    classification.setObjectType("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification");
    classification.setId(java.util.UUID.randomUUID().toString());
    classification.setClassifiedObject(submissionSetId);
    classification.setClassificationNode(CUUID.SubmissionSet.classificationNode);
    return classification;
  }

  public AssociationType1 createAssociation(String sourceId, String targetId) {
    AssociationType1 association = factory.createAssociationType1();
    association.setAssociationType("HasMember");
    association.setId(UUID.randomUUID().toString());
    association.setSourceObject(sourceId);
    association.setTargetObject(targetId);
    association.getSlot().add(createSlot("SubmissionSetStatus","Original"));
    return association;
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
      for (int i=0; i<nodes.getLength(); i++) {
        String value = nodes.item(i).getTextContent();
        list.add(value != null ? value : "");
      }
    }
    catch (XPathExpressionException ex) {
    }
    return list;
  }

  // Generates:
  // <rim:LocalizedString value="{value}"/>
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
    if (nodeRep != null) classification.setNodeRepresentation(nodeRep);
    if (name != null) classification.setName(createInternationalString(name));
    if (values != null && values.length > 0) {
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
    if (registryObject != null) externalIdentifier.setRegistryObject(registryObject);
    externalIdentifier.setName(createInternationalString(name));
    externalIdentifier.setValue(value);
    return externalIdentifier;
  }
}
