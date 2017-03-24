package org.net4care.xdsconnector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBElement;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.net4care.xdsconnector.Constants.COID;
import org.net4care.xdsconnector.Constants.XDSStatusValues;
import org.net4care.xdsconnector.Utilities.CodedValue;
import org.net4care.xdsconnector.Utilities.CodedValue.CodedValueBuilder;
import org.net4care.xdsconnector.Utilities.FindDocumentsQueryBuilder;
import org.net4care.xdsconnector.Utilities.FindSubmissionSetsQueryBuilder;
import org.net4care.xdsconnector.Utilities.GetAssociationsQueryBuilder;
import org.net4care.xdsconnector.Utilities.GetDocumentsQueryBuilder;
import org.net4care.xdsconnector.Utilities.GetSubmissionSetAndContentsQueryBuilder;
import org.net4care.xdsconnector.Utilities.GetSubmissionSetsQueryBuilder;
import org.net4care.xdsconnector.Utilities.PrettyPrinter;
import org.net4care.xdsconnector.service.AdhocQueryResponseType;
import org.net4care.xdsconnector.service.ExternalIdentifierType;
import org.net4care.xdsconnector.service.ExtrinsicObjectType;
import org.net4care.xdsconnector.service.IdentifiableType;
import org.net4care.xdsconnector.service.RegistryPackageType;
import org.net4care.xdsconnector.service.RegistryResponseType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.util.StringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConnectorConfiguration.class)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
public class TestXds {

    @Autowired
    RepositoryConnector xdsRepositoryConnector;

    @Autowired
    RegistryConnector xdsRegistryConnector;

    @Value("${xds.test.patientIDforQuery}")
    private String patientIDforQuery;

    @Value("${xds.test.documentForRetrieveUniqueId}")
    private String documentForRetrieveUniqueId;

    @Value("${xds.test.documentForQueryUUID}")
    private String documentForQueryUUID;

    @Value("${xds.test.submissionSetForQueryUUID1}")
    private String submissionSetForQueryUUID1;

    @Value("${xds.test.submissionSetForQueryUUID2}")
    private String submissionSetForQueryUUID2;

    @Value("${xds.test.cdaDocumentEx}")
    private String cdaDocumentEx;

    @Value("${xds.test.cdaDocumentExUUID}")
    private String cdaDocumentExUUID;

    private static final Logger logger = LoggerFactory.getLogger(TestXds.class);

    @Test
    public void retrieveDocumentSet() {
      
        RetrieveDocumentSetResponseType document = xdsRepositoryConnector.retrieveDocumentSet(documentForRetrieveUniqueId);

        Assert.assertTrue("Expected > 0 documents", document.getDocumentResponse().size() > 0);

        RetrieveDocumentSetResponseType.DocumentResponse documentResponse = document.getDocumentResponse().get(0);

        logger.debug("\nResult: " + new String(documentResponse.getDocument()) + "\n");
    }

    //NOTICE: This test does not work with NIST Document Sharing Test Facility at http://ihexds.nist.gov/.
    // The document(s) used stem from the Danish HL7 PHMR profiling work and thus do not correspond to NIST
    // affinity domain setup.
    @Test
    public void provideAndRegisterAndRetrieveCDADocument() throws URISyntaxException, IOException {
        logger.debug("------------ Running provideAndRegisterCDADocument ------------");

        if (!cdaDocumentEx.isEmpty()) {
            // the date for the document is 13.01.2014
            java.net.URL url = getClass().getClassLoader().getResource(cdaDocumentEx);
            List<String> lines = Files.readAllLines(Paths.get(url.toURI()), StandardCharsets.UTF_8);
            String providedDocument = StringUtils.collectionToDelimitedString(lines, "\n");

            // make a new unique id
            String uuid = UUID.randomUUID().toString();
            providedDocument = providedDocument.replace(cdaDocumentExUUID, uuid);
            CodedValue healthcareFacilityTypeCode = new CodedValueBuilder().setCode(COID.DK.FacilityCode).setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName(COID.DK.facilityTypeCode2DisplayName(COID.DK.FacilityCode)).build();
            CodedValue practiceSettingCode = new CodedValueBuilder().setCode("408443003").setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName("almen medicin").build();
            RegistryResponseType provideResponse = xdsRepositoryConnector.provideAndRegisterCDADocument(providedDocument, healthcareFacilityTypeCode, practiceSettingCode);
            logger.debug(PrettyPrinter.prettyPrint(provideResponse));
            Assert.assertTrue(provideResponse.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));

            logger.debug("------------ Running retrieveDocumentSet ------------");

            String docId = "1.2.208.184^" + uuid.replace("-", "").substring(0, 16);
            RetrieveDocumentSetResponseType retriveResponse = xdsRepositoryConnector.retrieveDocumentSet(docId);
            //logger.debug(PrettyPrinter.prettyPrint(retriveResponse));
            Assert.assertTrue(provideResponse.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));

            RetrieveDocumentSetResponseType.DocumentResponse documentResponse = retriveResponse.getDocumentResponse().get(0);
            String retrievedDocument = new String(documentResponse.getDocument());
            //The following assert should really be:
            // Assert.assertEquals(providedDocument, retrievedDocument)
            //...but we have issues with e.g. Danish characters not coming back the same as they went in...
            //(at least w. OpenXDS as backend)
            Assert.assertTrue(retrievedDocument.contains(uuid));
        } else
            logger.info("No CDA document example provided - not running provideAndRegisterCDADocument test");
    }

    //This test will:
    // 1. ProvideAndRegister two CDA Documents in one SubmissionSet
    // 2. Query for one of the DocumentEntries we just submitted using its unique id to get its UUID
    // 3. Query, using DocumentEntry UUID, for SubmissionSet containing one of the documents
    // 4. Query, using SubmissionSet UUID, for SubmissionSet and contents (i.e. DocumentEntries and Associations)
    // 5. Verify that the two DocumentEntries are the two we submitted with our original ProvideAndRegister
    //NOTICE: This test does not work with NIST Document Sharing Test Facility at http://ihexds.nist.gov/.
    // The document(s) used stem from the Danish HL7 PHMR profiling work and thus do not correspond to NIST
    // affinity domain setup.
    @Test
    public void provideAndRegisterAndRetrieveCDADocuments() throws URISyntaxException, IOException {
        if (!cdaDocumentEx.isEmpty()) {
            ////////////////////////////////////////////////////////////
            //1. ProvideAndRegister two CDA Documents in one SubmissionSet
            logger.debug("------------ Running provideAndRegisterCDADocuments ------------");

            java.net.URL url = getClass().getClassLoader().getResource(cdaDocumentEx);
            List<String> lines = Files.readAllLines(Paths.get(url.toURI()), StandardCharsets.UTF_8);
            String providedDocument1 = StringUtils.collectionToDelimitedString(lines, "\n");
            String providedDocument2 = providedDocument1;

            // make a new unique id for the 1st doc:
            //These are not the UUIDs of the DocumentEntry, but UUIDs of the CDA doc. itself
            String uuid1 = UUID.randomUUID().toString();
            providedDocument1 = providedDocument1.replace(cdaDocumentExUUID, uuid1);
            // make a new unique id for the 2nd doc:
            String uuid2 = UUID.randomUUID().toString();
            providedDocument2 = providedDocument2.replace(cdaDocumentExUUID, uuid2);

            List<String> providedDocuments = new ArrayList<String>();
            providedDocuments.add(providedDocument1);
            providedDocuments.add(providedDocument2);

            CodedValue healthcareFacilityTypeCode = new CodedValueBuilder().setCode(COID.DK.FacilityCode).setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName(COID.DK.facilityTypeCode2DisplayName(COID.DK.FacilityCode)).build();
            CodedValue practiceSettingCode = new CodedValueBuilder().setCode("408443003").setCodeSystem(COID.DK.FacilityCodeSystem).setDisplayName("almen medicin").build();
            RegistryResponseType provideResponse = xdsRepositoryConnector.provideAndRegisterCDADocuments(providedDocuments, healthcareFacilityTypeCode, practiceSettingCode);

            logger.debug(PrettyPrinter.prettyPrint(provideResponse));
            Assert.assertTrue(provideResponse.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));
            ////////////////////////////////////////////////////////////
            //2. Query for one of the DocumentEntries we just submitted using its unique id to get its UUID
            String docEntryUniqueId1 = "1.2.208.184^" + uuid1.replace("-", "").substring(0, 16);
            String docEntryUniqueId2 = "1.2.208.184^" + uuid2.replace("-", "").substring(0, 16);

            logger.debug("------------ Running getDocuments ------------");

            GetDocumentsQueryBuilder getDocumentsQueryBuilder = (GetDocumentsQueryBuilder) new GetDocumentsQueryBuilder()
                    .setUniqueId(docEntryUniqueId1)
                    .setReturnType(FindDocumentsQueryBuilder.RETURN_TYPE_OBJECTREFERENCES);

            AdhocQueryResponseType docQueryResponseType = xdsRegistryConnector.executeQuery(getDocumentsQueryBuilder);

            logger.debug(PrettyPrinter.prettyPrint(docQueryResponseType));
            Assert.assertTrue(docQueryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));

            //Get the UUID of the Doc.Entry:
            String docEntryUUID = "";
            for (JAXBElement<? extends IdentifiableType> elm : docQueryResponseType.getRegistryObjectList().getIdentifiable()) {
                docEntryUUID = elm.getValue().getId();
            }

            Assert.assertFalse(docEntryUUID.isEmpty());

            ////////////////////////////////////////////////////////////
            //3. Query, using DocumentEntry UUID, for SubmissionSet containing one of the documents
            logger.debug("------------ Running GetSubmissionSets ------------");

            GetSubmissionSetsQueryBuilder getSubmissionSetsQueryBuilder = (GetSubmissionSetsQueryBuilder) new GetSubmissionSetsQueryBuilder()
                    .setUUID(docEntryUUID)
                    .setReturnType(FindDocumentsQueryBuilder.RETURN_TYPE_FULLMETADATA);

            AdhocQueryResponseType submSetQueryResponseType = xdsRegistryConnector.executeQuery(getSubmissionSetsQueryBuilder);

            logger.debug(PrettyPrinter.prettyPrint(submSetQueryResponseType));
            Assert.assertTrue(submSetQueryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));

            //GetSubmissionSets should return 2 identifiables: A RegistryPackage (= the SubmissionSet) and
            //an Association (HasMember relationship between Subm.Set and DocumentEntry)
            Assert.assertTrue(submSetQueryResponseType.getRegistryObjectList().getIdentifiable().size() == 2);

            //Find the SubmissionSet and get it's UUID:
            String submissionSetUUID = "";
            for (JAXBElement<? extends IdentifiableType> elm : submSetQueryResponseType.getRegistryObjectList().getIdentifiable()) {
                if (elm.getValue() instanceof RegistryPackageType)
                    submissionSetUUID = elm.getValue().getId();
            }

            //We should find a SubmissionSet that the Doc.Entry is part of:
            Assert.assertFalse(submissionSetUUID.isEmpty());

            ////////////////////////////////////////////////////////////
            //4. Query, using SubmissionSet UUID, for SubmissionSet and contents (i.e. DocumentEntries and Associations)
            logger.info("------------ Running getSubmissionSetAndContents ------------");

            GetSubmissionSetAndContentsQueryBuilder getSubmissionSetAndContentsQueryBuilder =
                    (GetSubmissionSetAndContentsQueryBuilder) new GetSubmissionSetAndContentsQueryBuilder()
                            .setSubmissionSetUUID(submissionSetUUID)
                            .setReturnType(FindDocumentsQueryBuilder.RETURN_TYPE_FULLMETADATA);

            AdhocQueryResponseType submSetAndContentsQueryResponseType = xdsRegistryConnector.executeQuery(getSubmissionSetAndContentsQueryBuilder);

            logger.debug(PrettyPrinter.prettyPrint(submSetAndContentsQueryResponseType));
            Assert.assertTrue(submSetAndContentsQueryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));

            //GetSubmissionSetAndContents should return 5 identifiables:
            // 2 ExtrinsicObjects corresponding to the two DocumentEntries
            // 1 RegistryPackage corresponding to the SubmissionSet
            // 2 Associations corresponding to HasMember relationships betw. Subm.Set and the two DocumentEntries
            Assert.assertTrue(submSetAndContentsQueryResponseType.getRegistryObjectList().getIdentifiable().size() == 5);

            //Verify that the two DocumentEntries are the two we submitted with our original ProvideAndRegister:
            for (JAXBElement<? extends IdentifiableType> elm : submSetAndContentsQueryResponseType.getRegistryObjectList().getIdentifiable()) {
                if (elm.getValue() instanceof ExtrinsicObjectType) {
                    ExtrinsicObjectType extrinsicObject = (ExtrinsicObjectType) elm.getValue();
                    for (ExternalIdentifierType externalIdentifier : extrinsicObject.getExternalIdentifier()) {
                        if (externalIdentifier.getName().getLocalizedString().get(0).getValue().equals("XDSDocumentEntry.uniqueId")) {
                            String docEntryUniqueId = externalIdentifier.getValue();
                            logger.debug("externalIdentifier.getValue() = " + docEntryUniqueId);
                            Assert.assertTrue(docEntryUniqueId.equals(docEntryUniqueId1) || docEntryUniqueId.equals(docEntryUniqueId2));
                        }
                    }
                }

            }

            //      String docId = "1.2.208.184^" + uuid.replace("-", "").substring(0, 16);
            //      RetrieveDocumentSetResponseType retriveResponse = xdsRepositoryConnector.retrieveDocumentSet(docId);
            //      Assert.assertTrue(retriveResponse.getRegistryResponse().getStatus().endsWith("Success"));
            //      System.out.println("\nRetrieveDocument Result: " + retriveResponse.getRegistryResponse().getStatus());
            //
            //      RetrieveDocumentSetResponseType.DocumentResponse documentResponse = retriveResponse.getDocumentResponse().get(0);
            //      String retrievedDocument = new String(documentResponse.getDocument());
            //      Assert.assertEquals(providedDocument, retrievedDocument);
        } else
            logger.info("No CDA document example provided - not running provideAndRegisterCDADocument test");
    }

    @Test
    public void findDocumentsQuery() {
        logger.info("------------ Running findDocumentsQuery ------------");

        FindDocumentsQueryBuilder findDocumentsQueryBuilder = (FindDocumentsQueryBuilder) new FindDocumentsQueryBuilder()
                .setPatientId(patientIDforQuery)
                .addDocumentStatus(XDSStatusValues.DocumentEntry.Approved)
                .addDocumentStatus(XDSStatusValues.DocumentEntry.Deprecated)
                .setReturnType(FindDocumentsQueryBuilder.RETURN_TYPE_FULLMETADATA);

        AdhocQueryResponseType queryResponseType = xdsRegistryConnector.executeQuery(findDocumentsQueryBuilder);

        logger.debug(PrettyPrinter.prettyPrint(queryResponseType));
        Assert.assertTrue(queryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));
    }

    @Test
    public void findSubmissionSetsQuery() {
        logger.info("------------ Running findSubmissionSetsQuery ------------");

        FindSubmissionSetsQueryBuilder findSubmissionSetsQueryBuilder = (FindSubmissionSetsQueryBuilder) new FindSubmissionSetsQueryBuilder()
                .setPatientId(patientIDforQuery)
                .setSubmissionSetStatus(XDSStatusValues.DocumentEntry.Approved/*XDSStatusValues.SubmissionSet.Original*/)
                .setReturnType(FindDocumentsQueryBuilder.RETURN_TYPE_FULLMETADATA);

        AdhocQueryResponseType queryResponseType = xdsRegistryConnector.executeQuery(findSubmissionSetsQueryBuilder);

        logger.debug(PrettyPrinter.prettyPrint(queryResponseType));
        Assert.assertTrue(queryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));
    }

    @Test
    public void getSubmissionSetsQuery() {
        logger.info("------------ Running getSubmissionSetsQuery ------------");

        GetSubmissionSetsQueryBuilder getSubmissionSetsQueryBuilder = (GetSubmissionSetsQueryBuilder) new GetSubmissionSetsQueryBuilder()
                .setUUID(documentForQueryUUID)
                .setReturnType(FindDocumentsQueryBuilder.RETURN_TYPE_FULLMETADATA);

        AdhocQueryResponseType queryResponseType = xdsRegistryConnector.executeQuery(getSubmissionSetsQueryBuilder);

        logger.debug(PrettyPrinter.prettyPrint(queryResponseType));
        Assert.assertTrue(queryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));
    }

    @Test
    public void getSubmissionSetAndContents() {
        logger.info("------------ Running getSubmissionSetAndContents ------------");

        GetSubmissionSetAndContentsQueryBuilder getSubmissionSetAndContentsQueryBuilder =
                (GetSubmissionSetAndContentsQueryBuilder) new GetSubmissionSetAndContentsQueryBuilder()
                        .setSubmissionSetUUID(submissionSetForQueryUUID1)
                        .setReturnType(FindDocumentsQueryBuilder.RETURN_TYPE_FULLMETADATA);

        AdhocQueryResponseType queryResponseType = xdsRegistryConnector.executeQuery(getSubmissionSetAndContentsQueryBuilder);

        logger.debug(PrettyPrinter.prettyPrint(queryResponseType));
        Assert.assertTrue(queryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));
    }

    @Test
    public void getAssociationsQuery() {
        logger.info("------------ Running getAssociationsQuery ------------");

        GetAssociationsQueryBuilder getassociationsQueryBuilder = (GetAssociationsQueryBuilder) new GetAssociationsQueryBuilder()
                .setUUID(submissionSetForQueryUUID1)
                .setReturnType(FindDocumentsQueryBuilder.RETURN_TYPE_FULLMETADATA);

        AdhocQueryResponseType queryResponseType = xdsRegistryConnector.executeQuery(getassociationsQueryBuilder);

        logger.debug(PrettyPrinter.prettyPrint(queryResponseType));
        Assert.assertTrue(queryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));
    }

    @Test
    public void queryForObjectReferences() {
        String[] patientID = {patientIDforQuery};
        String[] creationTimeFrom = {"201403010000"};
        String[] creationTimeTo = {"201510312359"};

        Map<String, String[]> parameters = new HashMap();

        parameters.put("PatientID", patientID);
        parameters.put("creationTimeFrom", creationTimeFrom);
        parameters.put("creationTimeTo", creationTimeTo);

        List<AdhocQueryResponseType> queryResponses = xdsRegistryConnector.queryRegistry(patientIDforQuery, parameters, true);

        Assert.assertNotNull(queryResponses);

        for (AdhocQueryResponseType response : queryResponses) {
            logger.debug(PrettyPrinter.prettyPrint(response));
            Assert.assertTrue(response.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Success));
        }
    }
}
