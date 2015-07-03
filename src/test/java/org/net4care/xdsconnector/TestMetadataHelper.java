package org.net4care.xdsconnector;

import static org.junit.Assert.*;

import org.net4care.xdsconnector.service.*;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestMetadataHelper {

  private final String xmlns = "xmlns=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\"";
  private final String xmlns6 = "xmlns=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\" " +
      "xmlns:ns2=\"urn:hl7-org:v3\" " +
      "xmlns:ns3=\"urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0\" " +
      "xmlns:ns4=\"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0\" " +
      "xmlns:ns5=\"urn:ihe:iti:xds-b:2007\" " +
      "xmlns:ns6=\"urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0\"";
  private MetadataHelper helper = new MetadataHelper();
  private ObjectFactory factory = new ObjectFactory();
  private Calendar calender = new GregorianCalendar();

  @Before
  public void setup() {
    XMLUnit.setIgnoreWhitespace(true);
    helper.setClassName("");
  }

  @Test
  public void createAuthorInstitution() {
    SlotType1 slot = helper.createAuthorInstitution("Odense Universitetshospital – Svendborg Sygehus", "88878685", "1.2.208.176.1");
    String control = "<Slot name=\"authorInstitution\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>Odense Universitetshospital – Svendborg Sygehus^^^^^&amp;1.2.208.176.1&amp;ISO^^^^88878685</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createAuthorPerson() {
    SlotType1 slot = helper.createAuthorPerson("Andersen", "Anders", "Frederik", "Ingolf");
    String control = "<Slot name=\"authorPerson\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>^Andersen^Anders^Frederik&amp;Ingolf^^^^^&amp;ISO</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createApprovedAvailabilityStatus() {
    ExtrinsicObjectType extobj = helper.createApprovedAvailabilityStatus();
    String control = "<ExtrinsicObject\n" +
        "  id=\"urn:uuid:fbeacdb7-5421-4474-9267-985007cd8855\"\n" +
        "  objectType=\"urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1\"\n" +
        "  status=\"urn:oasis:names:tc:ebxml-regrep:StatusType:Approved\"\n" +
        "  mimeType=\"text/xml\"\n" +
        "  " + xmlns6 + "/>";
    assertXMLEqual(control, factory.createExtrinsicObject(extobj));
  }

  @Test
  public void createClinicalDocumentClassCode() {
    ClassificationType clstype = helper.createClinicalDocumentClassCode();
    String control = "<Classification\n" +
        "  classificationScheme=\"urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a\"\n" +
        "  classifiedObject=\"\"\n" +
        "  id=\"" + clstype.getId() + "\"\n" +
        "  objectType=\"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification\"\n" +
        "  nodeRepresentation=\"001\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Slot name=\"codingScheme\">\n" +
        "    <ValueList>\n" +
        "      <Value>2.16.840.1.113883.3.4208.100.9</Value>\n" +
        "    </ValueList>\n" +
        "  </Slot>\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"Klinisk rapport\"/>\n" +
        "  </Name>\n" +
        "</Classification>";
    assertXMLEqual(control, factory.createClassification(clstype));
  }

  @Test
  public void createConfidentialityCode() {
    ClassificationType clstype = helper.createConfidentialityCode("N");
    String control = "<Classification\n" +
        "  classificationScheme=\"urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f\"\n" +
        "  classifiedObject=\"\"\n" +
        "  id=\"" + clstype.getId() + "\"\n" +
        "  objectType=\"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification\"\n" +
        "  nodeRepresentation=\"N\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Slot name=\"codingScheme\">\n" +
        "    <ValueList>\n" +
        "      <Value>2.16.840.1.113883.5.25</Value>\n" +
        "    </ValueList>\n" +
        "  </Slot>\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"Normal\"/>\n" +
        "  </Name>\n" +
        "</Classification>";
    assertXMLEqual(control, factory.createClassification(clstype));
  }

  @Test
  public void createCreationTime() {
    calender.set(2014, 11, 25, 21, 20, 10);
    SlotType1 slot = helper.createCreationTime(calender.getTime());
    String control = "<Slot name=\"creationTime\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>20141225212010</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createEntityUUID() {
    ExtrinsicObjectType extobj = helper.createEntityUUID("a6e06ca8-0c75-4064-9e5c-88b9045a96f6");
    String control = "<ExtrinsicObject\n" +
        "  id=\"urn:uuid:a6e06ca8-0c75-4064-9e5c-88b9045a96f6\"\n" +
        "  objectType=\"urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1\"\n" +
        "  mimeType=\"text/xml\"\n" +
        "  " + xmlns6 + "/>";
    assertXMLEqual(control, factory.createExtrinsicObject(extobj));
  }

  @Test
  public void createEventCodeList() {
    ClassificationType clstype = helper.createEventCodeList("2.16.840.1.113883.3.4208.100.5", "ZZ3160", "Blodtryksmåling");
    String control = "<Classification\n" +
        "  classificationScheme=\"urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4\"\n" +
        "  classifiedObject=\"\"\n" +
        "  id=\"" + clstype.getId() + "\"\n" +
        "  objectType=\"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification\"\n" +
        "  nodeRepresentation=\"ZZ3160\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Slot name=\"codingScheme\">\n" +
        "    <ValueList> \n" +
        "      <Value>2.16.840.1.113883.3.4208.100.5</Value>\n" +
        "    </ValueList>\n" +
        "  </Slot>\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"Blodtryksmåling\"/>\n" +
        "  </Name>\n" +
        "</Classification>";
    assertXMLEqual(control, factory.createClassification(clstype));
  }

  @Test
  public void createFormatCode() {
    ClassificationType clstype = helper.createPHMRFormatCode();
    String control = "<Classification\n" +
        "  classificationScheme=\"urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d\"\n" +
        "  classifiedObject=\"\"\n" +
        "  id=\"" + clstype.getId() + "\"\n" +
        "  objectType=\"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification\"\n" +
        "  nodeRepresentation=\"urn:ad:dk:medcom:phmr:full\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Slot name=\"codingScheme\">\n" +
        "    <ValueList> \n" +
        "      <Value>2.16.840.1.113883.3.4208.100.10</Value>\n" +
        "    </ValueList>\n" +
        "  </Slot>\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"DK PHMR schema\"/>\n" +
        "  </Name>\n" +
        "</Classification>";
    assertXMLEqual(control, factory.createClassification(clstype));
  }

  @Test
  public void createHealthcareFacilityTypeCode() {
    ClassificationType clstype = helper.createHealthcareFacilityTypeCode("22232009", "2.16.840.1.1133883.3.4208.100.11", "hospital");
    String control = "<Classification\n" +
        "  classificationScheme=\"urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1\"\n" +
        "  classifiedObject=\"\"\n" +
        "  id=\"" + clstype.getId() + "\"\n" +
        "  objectType=\"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification\"\n" +
        "  nodeRepresentation=\"2.16.840.1.1133883.3.4208.100.11\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Slot name=\"codingScheme\">\n" +
        "    <ValueList>\n" +
        "      <Value>22232009</Value>\n" +
        "    </ValueList>\n" +
        "  </Slot>\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"hospital\"/>\n" +
        "  </Name>\n" +
        "</Classification>";
    assertXMLEqual(control, factory.createClassification(clstype));
  }

  @Test
  public void createHomeCommunityId() {
    ExtrinsicObjectType extobj = helper.createHomeCommunityId("1.2.3");
    String control = "<ExtrinsicObject home=\"urn:oid:1.2.3\"\n" +
        "  " + xmlns6 + "/>";
    assertXMLEqual(control, factory.createExtrinsicObject(extobj));
  }

  @Test
  public void createLanguageCode() {
    SlotType1 slot = helper.createLanguageCode("da-DK");
    String control = "<Slot name=\"languageCode\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>da-DK</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createLegalAuthenticator() {
    SlotType1 slot = helper.createLegalAuthenticator("Andersen", "Anders", "Frederik", "Ingolf");
    String control = "<Slot name=\"legalAuthenticator\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>^Andersen^Anders^Frederik&amp;Ingolf^^^^^^^&amp;&amp;ISO</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createObjectType() {
    ExtrinsicObjectType extobj = helper.createObjectType();
    String control = "<ExtrinsicObject\n" +
        "  id=\"urn:uuid:a6e06ca8-0c75-4064-9e5c-88b9045a96f6\"\n" +
        "  objectType=\"urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1\"\n" +
        "  mimeType=\"text/xml\"\n" +
        "  " + xmlns6 + "/>";
    assertXMLEqual(control, factory.createExtrinsicObject(extobj));
  }

  @Test
  public void createPatientId() {
    SlotType1 slot = helper.createPatientId("2.16.840.1.113883.3.4208.100.2", "2512484916");
    String control = "<Slot name=\"patientId\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>2512484916^^^&amp;2.16.840.1.113883.3.4208.100.2&amp;ISO</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createExternalPatientId() {
    ExternalIdentifierType extid = helper.createDocumentEntryPatientId("2.16.840.1.113883.3.4208.100.2", "2512484916");
    String control = "<ExternalIdentifier\n" +
        "  identificationScheme=\"urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427\"\n" +
        "  value=\"2512484916^^^&amp;2.16.840.1.113883.3.4208.100.2&amp;ISO\" \n" +
        "  id=\"" + extid.getId() +"\"\n" +
        "  objectType=\"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier\"\n" +
        "  registryObject=\"\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"XDSDocumentEntry.patientId\"/>\n" +
        "  </Name>\n" +
        "</ExternalIdentifier>";
    assertXMLEqual(control, factory.createExternalIdentifier(extid));
  }

  @Test
  public void createRepositoryUniqueId() {
    SlotType1 slot = helper.createRepositoryUniqueId("1.3.6.1.4.5");
    String control = "<Slot name=\"repositoryUniqueId\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>1.3.6.1.4.5</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createServiceStartTime() {
    calender.set(2014, 11, 25, 21, 20, 10);
    SlotType1 slot = helper.createServiceStartTime(calender.getTime());
    String control = "<Slot name=\"serviceStartTime\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>20141225212010</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createServiceStopTime() {
    calender.set(2014, 11, 25, 21, 20, 10);
    SlotType1 slot = helper.createServiceStopTime(calender.getTime());
    String control = "<Slot name=\"serviceStopTime\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>20141225212010</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createSize() {
    SlotType1 slot = helper.createSize(3654);
    String control = "<Slot name=\"size\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>3654</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createSourcePatientId() {
    SlotType1 slot = helper.createSourcePatientId("2.16.840.1.113883.3.4208.100.2", "2512484916");
    String control = "<Slot name=\"sourcePatientId\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>2512484916^^^&amp;2.16.840.1.113883.3.4208.100.2&amp;ISO</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createSourcePatientInfo() {
    SlotType1 slot = helper.createSourcePatientInfo("19481225", "F", "Berggren", "Nancy", "Ann");
    String control = "<Slot name=\"sourcePatientInfo\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>Berggren^Nancy^Ann^^^19481225^F</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createSubmissionTime() {
    calender.set(2014, 11, 25, 21, 20, 10);
    SlotType1 slot = helper.createSubmissionTime(calender.getTime());
    String control = "<Slot name=\"submissionTime\" " + xmlns + ">\n" +
        "  <ValueList>\n" +
        "    <Value>20141225212010</Value>\n" +
        "  </ValueList>\n" +
        "</Slot>";
    assertXMLEqual(control, factory.createSlot(slot));
  }

  @Test
  public void createTitle() {
    ExtrinsicObjectType extobj = helper.createTitle("Hjemmemonitorering for 2303439995");
    String control = "<ExtrinsicObject\n" +
        // "  id=\"\"\n" +
        "  objectType=\"urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1\"\n" +
        "  mimeType=\"text/xml\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"Hjemmemonitorering for 2303439995\"/>\n" +
        "  </Name>\n" +
        "</ExtrinsicObject>";
    assertXMLEqual(control, factory.createExtrinsicObject(extobj));
  }

  @Test
  public void createTypeCode() {
    ClassificationType clstype = helper.createTypeCode("2.16.840.1.113883.6.1", "53576-5", "Personal Health Monitoring Report");
    String control = "<Classification\n" +
        "  classificationScheme=\"urn:uuid:f0306f51-975f-434e-a61c-c59651d33983\"\n" +
        "  classifiedObject=\"\"\n" +
        "  id=\"" + clstype.getId() + "\"\n" +
        "  objectType=\"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification\"\n" +
        "  nodeRepresentation=\"53576-5\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Slot name=\"codingScheme\">\n" +
        "    <ValueList>\n" +
        "      <Value>2.16.840.1.113883.6.1</Value> \n" +
        "    </ValueList>\n" +
        "  </Slot>\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"Personal Health Monitoring Report\"/>\n" +
        "  </Name>\n" +
        "</Classification>";
    assertXMLEqual(control, factory.createClassification(clstype));
  }

  @Test
  public void createUniqueId() {
    ExternalIdentifierType extid = helper.createDocumentEntryUniqueId("2.16.840.1.113883.3.4208", "aa2386d0-79ea-11e3-981f-0800200c9a66");
    String control = "<ExternalIdentifier\n" +
        "  identificationScheme=\"urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab\"\n" +
        "  value=\"2.16.840.1.113883.3.4208^aa2386d0-79ea-11e3-981f-0800200c9a66\" \n" +
        "  id=\"" + extid.getId() +"\"\n" +
        "  objectType=\"urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier\"\n" +
        "  registryObject=\"\"\n" +
        "  " + xmlns6 + ">\n" +
        "  <Name>\n" +
        "    <LocalizedString value=\"XDSDocumentEntry.uniqueId\"/>\n" +
        "  </Name>\n" +
        "</ExternalIdentifier>";
    assertXMLEqual(control, factory.createExternalIdentifier(extid));
  }

  private void assertXMLEqual(String control, JAXBElement test) {
    String xml = serialize(test);
    try {
      Diff diff = XMLUnit.compareXML(control, xml);
      assertTrue(diff.similar());
      assertTrue(diff.identical());
    }
    catch (Exception ex) {
      assertTrue("Unexpected exception: " + ex.getMessage(), false);
    }
  }

  private String serialize(JAXBElement object) {
    try {
      JAXBContext context = JAXBContext.newInstance(object.getValue().getClass());
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      StringWriter writer = new StringWriter();
      marshaller.marshal(object, writer);
      return writer.toString();
    } catch (JAXBException e) {
      return null;
    }
  }
}

