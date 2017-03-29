package org.net4care.xdsconnector;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.net4care.xdsconnector.Constants.COID;
import org.net4care.xdsconnector.Utilities.CodedValue;
import org.net4care.xdsconnector.Utilities.SubmitObjectsRequestHelper;
import org.net4care.xdsconnector.Utilities.CodedValue.CodedValueBuilder;
import org.net4care.xdsconnector.service.ObjectFactory;
import org.net4care.xdsconnector.service.ProvideAndRegisterDocumentSetRequestType;
import org.springframework.util.StringUtils;

public class TestSubmitObjectsRequestHelperExamples {
    private SubmitObjectsRequestHelper helper = new SubmitObjectsRequestHelper("1.2.3", "1.2.3");
    private ObjectFactory factory = new ObjectFactory();
    private Calendar calender = new GregorianCalendar();

    @Before
    public void setup() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void buildPHMRNancy() {
        try {
            // get the example cda
            String path = "examples/PHMRNancy.xml";
            java.net.URL url = getClass().getClassLoader().getResource(path);
            List<String> lines = Files.readAllLines(Paths.get(url.toURI()), Charset.forName("UTF-8"));
            String cda = StringUtils.collectionToDelimitedString(lines, "\n");

            CodedValue healthcareFacilityTypeCode = new CodedValueBuilder().setCode(COID.DK.FacilityCode).setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName(COID.DK.facilityTypeCode2DisplayName(COID.DK.FacilityCode)).build();
            CodedValue practiceSettingCode = new CodedValueBuilder().setCode("408443003").setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName("almen medicin").build();
            ProvideAndRegisterDocumentSetRequestType request = new RepositoryConnector().buildProvideAndRegisterCDADocumentRequest(cda, healthcareFacilityTypeCode, practiceSettingCode);
            JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestPayload = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

            String xml = serialize(requestPayload, request.getClass());
//            System.out.println(xml);
        } catch (Exception ex) {
            assertTrue("Unexpected exception: " + ex.getMessage(), false);
        }
    }
    
    @Test
    public void buildQFDDLungInformationNeedsQuestionaire() {
        try {
            // get the example cda
            String path = "examples/QFDDLungInformationNeedsQuestionaire.xml";
            java.net.URL url = getClass().getClassLoader().getResource(path);
            List<String> lines = Files.readAllLines(Paths.get(url.toURI()), Charset.forName("UTF-8"));
            String cda = StringUtils.collectionToDelimitedString(lines, "\n");

            CodedValue healthcareFacilityTypeCode = new CodedValueBuilder().setCode(COID.DK.FacilityCode).setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName(COID.DK.facilityTypeCode2DisplayName(COID.DK.FacilityCode)).build();
            CodedValue practiceSettingCode = new CodedValueBuilder().setCode("408443003").setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName("almen medicin").build();
            ProvideAndRegisterDocumentSetRequestType request = new RepositoryConnector().buildProvideAndRegisterCDADocumentRequest(cda, healthcareFacilityTypeCode, practiceSettingCode);
            JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestPayload = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

            String xml = serialize(requestPayload, request.getClass());
//            System.out.println(xml);
        } catch (Exception ex) {
            assertTrue("Unexpected exception: " + ex.getMessage(), false);
        }
    }
    
    @Test
    public void buildQRDLungInformationNeedsQuestionaire() {
        try {
            // get the example cda
            String path = "examples/QRDLungInformationNeedsQuestionaire.xml";
            java.net.URL url = getClass().getClassLoader().getResource(path);
            List<String> lines = Files.readAllLines(Paths.get(url.toURI()), Charset.forName("UTF-8"));
            String cda = StringUtils.collectionToDelimitedString(lines, "\n");

            CodedValue healthcareFacilityTypeCode = new CodedValueBuilder().setCode(COID.DK.FacilityCode).setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName(COID.DK.facilityTypeCode2DisplayName(COID.DK.FacilityCode)).build();
            CodedValue practiceSettingCode = new CodedValueBuilder().setCode("408443003").setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName("almen medicin").build();
            ProvideAndRegisterDocumentSetRequestType request = new RepositoryConnector().buildProvideAndRegisterCDADocumentRequest(cda, healthcareFacilityTypeCode, practiceSettingCode);
            JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestPayload = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

            String xml = serialize(requestPayload, request.getClass());
//            System.out.println(xml);
        } catch (Exception ex) {
            assertTrue("Unexpected exception: " + ex.getMessage(), false);
        }
    }

    private String serialize(Object object, Class clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
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

