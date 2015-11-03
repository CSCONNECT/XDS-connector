package org.net4care.xdsconnector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.net4care.xdsconnector.Utilities.MtomMessageCallback;
import org.net4care.xdsconnector.Utilities.SubmitObjectsRequestHelper;
import org.net4care.xdsconnector.service.*;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.support.MarshallingUtils;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
@PropertySource(value="classpath:xds.properties")
public class RepositoryConnector extends WebServiceGatewaySupport {

  @Value("${xds.repositoryUrl}")
  private String repositoryUrl;

  @Value("${xds.repositoryId}")
  private String repositoryId;

  @Value("${xds.homeCommunityId}")
  private String homeCommunityId;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

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
      throw t;
    }
	}

  public RegistryResponseType provideAndRegisterCDADocument(Document cda) {
    try {
      ProvideAndRegisterDocumentSetRequestType request = buildProvideAndRegisterCDADocumentRequest(cda);
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

  public RegistryResponseType provideAndRegisterCDADocument(String cda) {
    try {
      ProvideAndRegisterDocumentSetRequestType request = buildProvideAndRegisterCDADocumentRequest(cda);
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

  public RegistryResponseType provideAndRegisterCDADocuments(List<String> cdas) {
    try {
      ProvideAndRegisterDocumentSetRequestType request = buildProvideAndRegisterCDADocumentsRequest(cdas);
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

  protected ProvideAndRegisterDocumentSetRequestType buildProvideAndRegisterCDADocumentRequest(Document cdaDocument) {
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

    try {
      SubmitObjectsRequest submitRequest = new SubmitObjectsRequestHelper(repositoryId, homeCommunityId).buildFromCDA(cdaDocument);
      request.setSubmitObjectsRequest(submitRequest);

      ByteArrayOutputStream writer = new ByteArrayOutputStream();
      Marshaller marshaller = JAXBContext.newInstance(cdaDocument.getClass()).createMarshaller();
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

  protected ProvideAndRegisterDocumentSetRequestType buildProvideAndRegisterCDADocumentRequest(String cdaString) {
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      byte[] bytes = cdaString.getBytes(StandardCharsets.UTF_8);
      Document cdaDocument = builder.parse(new ByteArrayInputStream(bytes));

      SubmitObjectsRequest submitRequest = new SubmitObjectsRequestHelper(repositoryId, homeCommunityId).buildFromCDA(cdaDocument);
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

  protected ProvideAndRegisterDocumentSetRequestType buildProvideAndRegisterCDADocumentsRequest(List<String> cdaStrings) {
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

    try {
      SubmitObjectsRequest submitRequest = new SubmitObjectsRequest();
      request.setSubmitObjectsRequest(submitRequest);

      Map<String, Document> cdaDocuments = new HashMap<String, Document>();
      //List<Document> cdaDocuments = new ArrayList<Document>();
      for (String cdaString : cdaStrings) {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        byte[] bytes = cdaString.getBytes(StandardCharsets.UTF_8);
        Document cdaDocument = builder.parse(new ByteArrayInputStream(bytes));

        String docEntryId = UUID.randomUUID().toString();
        cdaDocuments.put(docEntryId, cdaDocument);

        ProvideAndRegisterDocumentSetRequestType.Document document = new ProvideAndRegisterDocumentSetRequestType.Document();
        document.setId(docEntryId);
        document.setValue(bytes);
        request.getDocument().add(document);
      }

      new SubmitObjectsRequestHelper(repositoryId, homeCommunityId).buildFromCDAs(submitRequest, cdaDocuments);
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
