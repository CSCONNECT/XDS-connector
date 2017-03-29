package org.net4care.xdsconnector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.net4care.xdsconnector.Utilities.CodeUtil;
import org.net4care.xdsconnector.Utilities.CodedValue;
import org.net4care.xdsconnector.Utilities.IdDocumentPair;
import org.net4care.xdsconnector.Utilities.MtomMessageCallback;
import org.net4care.xdsconnector.Utilities.SubmitObjectsRequestHelper;
import org.net4care.xdsconnector.service.ExtrinsicObjectType;
import org.net4care.xdsconnector.service.IdentifiableType;
import org.net4care.xdsconnector.service.ObjectFactory;
import org.net4care.xdsconnector.service.ProvideAndRegisterDocumentSetRequestType;
import org.net4care.xdsconnector.service.RegistryResponseType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.net4care.xdsconnector.service.SubmitObjectsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.w3c.dom.Document;

@Configuration
@PropertySource(value="classpath:xds.properties")
public class RepositoryConnector extends WebServiceGatewaySupport implements IRepositoryConnector {

  @Value("${xds.repositoryUrl}")
  private String repositoryUrl;

  @Value("${xds.repositoryId}")
  private String repositoryId;

  @Value("${xds.homeCommunityId}")
  private String homeCommunityId;
  
  private static JAXBContext jaxbContext = createJAXBContext();
  
  private static JAXBContext createJAXBContext() {
    try {
      return JAXBContext.newInstance(Document.class);
    } catch(JAXBException jx) {
      return null;
    }
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /* (non-Javadoc)
   * @see org.net4care.xdsconnector.RepositoryConnnector#retrieveDocumentSet(java.lang.String)
   */
 
  @Override
  public RetrieveDocumentSetResponseType retrieveDocumentSet(String docId) {
    try {
      // using the JAXB Wrapper voids the requirement for a @XMLRootElement annotation on the domain model objects
      RetrieveDocumentSetRequestType request = new RetrieveDocumentSetRequestType();
      JAXBElement<RetrieveDocumentSetRequestType> requestPayload = new ObjectFactory().createRetrieveDocumentSetRequest(request);

      DocumentRequest documentRequest = new DocumentRequest();
      documentRequest.setDocumentUniqueId(docId);
      documentRequest.setHomeCommunityId(homeCommunityId);
      documentRequest.setRepositoryUniqueId(repositoryId);
      request.getDocumentRequest().add(documentRequest);

      @SuppressWarnings("unchecked")
      JAXBElement<RetrieveDocumentSetResponseType> result = (JAXBElement<RetrieveDocumentSetResponseType>) getWebServiceTemplate()
        .marshalSendAndReceive(requestPayload,  new MtomMessageCallback(repositoryUrl, "RetrieveDocumentSet"));
      
      return result.getValue();
    }
    catch (Throwable t) {
      throw new RuntimeException(t);
    }
	}

  /* (non-Javadoc)
   * @see org.net4care.xdsconnector.RepositoryConnnector#provideAndRegisterCDADocument(org.w3c.dom.Document, org.net4care.xdsconnector.Utilities.CodedValue, org.net4care.xdsconnector.Utilities.CodedValue)
   */
  @Override
  public RegistryResponseType provideAndRegisterCDADocument(Document cda, CodedValue healthcareFacilityType, CodedValue practiceSettingsCode) {
    try {
      ProvideAndRegisterDocumentSetRequestType request = buildProvideAndRegisterCDADocumentRequest(cda, healthcareFacilityType, practiceSettingsCode);
      JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestPayload = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

      @SuppressWarnings("unchecked")
      JAXBElement<RegistryResponseType> result = (JAXBElement<RegistryResponseType>) getWebServiceTemplate()
          .marshalSendAndReceive(requestPayload, new MtomMessageCallback(repositoryUrl, "ProvideAndRegisterDocumentSet-b"));

      return result.getValue();
    }
    catch (Throwable t) {
      throw t;
    }
  }

  /* (non-Javadoc)
   * @see org.net4care.xdsconnector.RepositoryConnnector#provideAndRegisterCDADocument(java.lang.String, org.net4care.xdsconnector.Utilities.CodedValue, org.net4care.xdsconnector.Utilities.CodedValue)
   */
  @Override
  public RegistryResponseType provideAndRegisterCDADocument(String cda, CodedValue healthcareFacilityType, CodedValue practiceSettingsCode) {
    try {
      ProvideAndRegisterDocumentSetRequestType request = buildProvideAndRegisterCDADocumentRequest(cda, healthcareFacilityType, practiceSettingsCode);
      JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestPayload = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

      @SuppressWarnings("unchecked")
      JAXBElement<RegistryResponseType> result = (JAXBElement<RegistryResponseType>) getWebServiceTemplate()
          .marshalSendAndReceive(requestPayload, new MtomMessageCallback(repositoryUrl, "ProvideAndRegisterDocumentSet-b"));

      return result.getValue();
    }
    catch (Throwable t) {
      throw t;
    }
  }

  /* (non-Javadoc)
   * @see org.net4care.xdsconnector.RepositoryConnnector#provideAndRegisterCDADocuments(java.util.List, org.net4care.xdsconnector.Utilities.CodedValue, org.net4care.xdsconnector.Utilities.CodedValue)
   */
  @Override
  public RegistryResponseType provideAndRegisterCDADocuments(List<String> cdas, CodedValue healthcareFacilityType, CodedValue practiceSettingsCode) {
    try {
      ProvideAndRegisterDocumentSetRequestType request = buildProvideAndRegisterCDADocumentsRequest(cdas, healthcareFacilityType, practiceSettingsCode);
      JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestPayload = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

      @SuppressWarnings("unchecked")
      JAXBElement<RegistryResponseType> result = (JAXBElement<RegistryResponseType>) getWebServiceTemplate()
              .marshalSendAndReceive(requestPayload, new MtomMessageCallback(repositoryUrl, "ProvideAndRegisterDocumentSet-b"));

      return result.getValue();
    } catch (Throwable t) {
      throw t;
    }
  }

  protected ProvideAndRegisterDocumentSetRequestType buildProvideAndRegisterCDADocumentRequest(Document cdaDocument, CodedValue healthcareFacilityType, CodedValue practiceSettingsCode) {
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

    try {
      SubmitObjectsRequest submitRequest = new SubmitObjectsRequestHelper(repositoryId, homeCommunityId).buildFromCDA(cdaDocument, healthcareFacilityType, practiceSettingsCode);
      request.setSubmitObjectsRequest(submitRequest);

      ByteArrayOutputStream writer = new ByteArrayOutputStream();
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.marshal(cdaDocument, writer);

      ProvideAndRegisterDocumentSetRequestType.Document document = new ProvideAndRegisterDocumentSetRequestType.Document();
      document.setId(getDocumentId(submitRequest));
      document.setValue(writer.toByteArray());
      request.getDocument().add(document);
    }
    catch (Exception ex) {
      // TODO: log this
    }
    return request;
  }

  protected ProvideAndRegisterDocumentSetRequestType buildProvideAndRegisterCDADocumentRequest(String cdaString, CodedValue healthcareFacilityType, CodedValue practiceSettingsCode) {
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      byte[] bytes = cdaString.getBytes(StandardCharsets.UTF_8);
      Document cdaDocument = builder.parse(new ByteArrayInputStream(bytes));

      SubmitObjectsRequest submitRequest = new SubmitObjectsRequestHelper(repositoryId, homeCommunityId).buildFromCDA(cdaDocument, healthcareFacilityType, practiceSettingsCode);
      request.setSubmitObjectsRequest(submitRequest);

      ProvideAndRegisterDocumentSetRequestType.Document document = new ProvideAndRegisterDocumentSetRequestType.Document();
      document.setId(getDocumentId(submitRequest));
      document.setValue(bytes);
      request.getDocument().add(document);
    }
    catch (Exception ex) {
      // TODO: log this
    }

    return request;
  }

  protected ProvideAndRegisterDocumentSetRequestType buildProvideAndRegisterCDADocumentsRequest(List<String> cdaStrings, CodedValue healthcareFacilityType, CodedValue practiceSettingsCode) {
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

    try {
      SubmitObjectsRequest submitRequest = new SubmitObjectsRequest();
      request.setSubmitObjectsRequest(submitRequest);

      List<IdDocumentPair> idDocumentPairs = new ArrayList<IdDocumentPair>();
      for (String cdaString : cdaStrings) {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        byte[] bytes = cdaString.getBytes(StandardCharsets.UTF_8);
        Document cdaDocument = builder.parse(new ByteArrayInputStream(bytes));

        String documentEntryId = CodeUtil.prefixUUID(UUID.randomUUID().toString());
        idDocumentPairs.add(new IdDocumentPair(documentEntryId, cdaDocument));

        ProvideAndRegisterDocumentSetRequestType.Document document = new ProvideAndRegisterDocumentSetRequestType.Document();
        document.setId(documentEntryId);
        document.setValue(bytes);
        request.getDocument().add(document);
      }
      
      new SubmitObjectsRequestHelper(repositoryId, homeCommunityId).buildFromCDAs(submitRequest, idDocumentPairs, healthcareFacilityType, practiceSettingsCode);
    }
    catch (Exception ex) {
      logger.error("", ex);
    }

    return request;
  }

  protected static String getDocumentId(SubmitObjectsRequest request) {
    for (JAXBElement<? extends IdentifiableType> identifiable: request.getRegistryObjectList().getIdentifiable()) {
      if (identifiable.getValue() instanceof ExtrinsicObjectType) {
        ExtrinsicObjectType object = (ExtrinsicObjectType) identifiable.getValue();
        return object.getId();
      }
    }
    return null;
  }
}
