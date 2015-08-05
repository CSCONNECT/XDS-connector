package org.net4care.xdsconnector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.net4care.xdsconnector.Utilities.MessageCallback;
import org.net4care.xdsconnector.Utilities.SubmitObjectsRequestHelper;
import org.net4care.xdsconnector.service.*;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

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
      JAXBElement<RetrieveDocumentSetRequestType> requestWrapper = new ObjectFactory().createRetrieveDocumentSetRequest(request);

      DocumentRequest documentRequest = new DocumentRequest();
      documentRequest.setRepositoryUniqueId(repositoryId);
      documentRequest.setDocumentUniqueId(docId);
      request.getDocumentRequest().add(documentRequest);

      @SuppressWarnings("unchecked")
      JAXBElement<RetrieveDocumentSetResponseType> result = (JAXBElement<RetrieveDocumentSetResponseType>) getWebServiceTemplate()
          .marshalSendAndReceive(requestWrapper, new MessageCallback(repositoryUrl, "RetrieveDocumentSet"));

      return result.getValue();
    }
    catch (Throwable t) {
      throw t;
    }
	}

  public RegistryResponseType provideAndRegisterCDADocument(Document cda) {
    try {
      ProvideAndRegisterDocumentSetRequestType request = buildProvideAndRegisterCDADocumentRequest(cda);
      JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestWrapper = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

      @SuppressWarnings("unchecked")
      JAXBElement<RegistryResponseType> result = (JAXBElement<RegistryResponseType>) getWebServiceTemplate()
          .marshalSendAndReceive(requestWrapper, new MessageCallback(repositoryUrl, "ProvideAndRegisterDocumentSet-b"));

      return result.getValue();
    }
    catch (Throwable t) {
      throw t;
    }
  }

  public RegistryResponseType provideAndRegisterCDADocument(String cda) {
    try {
      ProvideAndRegisterDocumentSetRequestType request = buildProvideAndRegisterCDADocumentRequest(cda);
      JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestWrapper = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

      @SuppressWarnings("unchecked")
      JAXBElement<RegistryResponseType> result = (JAXBElement<RegistryResponseType>) getWebServiceTemplate()
          .marshalSendAndReceive(requestWrapper, new MessageCallback(repositoryUrl, "ProvideAndRegisterDocumentSet-b"));

      return result.getValue();
    }
    catch (Throwable t) {
      throw t;
    }
  }

  protected ProvideAndRegisterDocumentSetRequestType buildProvideAndRegisterCDADocumentRequest(Document cdaDocument) {
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

    SubmitObjectsRequest submitRequest = new SubmitObjectsRequestHelper(homeCommunityId).buildFromCDA(cdaDocument);
    request.setSubmitObjectsRequest(submitRequest);

    ByteArrayOutputStream writer = new ByteArrayOutputStream();
    try {
      Marshaller marshaller = JAXBContext.newInstance(cdaDocument.getClass()).createMarshaller();
      marshaller.marshal(cdaDocument, writer);
    }
    catch (Exception ex) {
      // TODO: log this
    }

    ProvideAndRegisterDocumentSetRequestType.Document document = new ProvideAndRegisterDocumentSetRequestType.Document();
    document.setId(getDocumentId(submitRequest));
    document.setValue(writer.toByteArray());
    request.getDocument().add(document);

    return request;
  }

  protected ProvideAndRegisterDocumentSetRequestType buildProvideAndRegisterCDADocumentRequest(String cdaString) {
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

    Document cdaDocument = null;
    byte[] bytes = cdaString.getBytes(Charset.forName("UTF-8"));
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      cdaDocument = builder.parse(new ByteArrayInputStream(bytes));
    }
    catch (Exception ex) {
      // TODO: log this
    }
    SubmitObjectsRequest submitRequest = new SubmitObjectsRequestHelper(homeCommunityId).buildFromCDA(cdaDocument);
    request.setSubmitObjectsRequest(submitRequest);

    ProvideAndRegisterDocumentSetRequestType.Document document = new ProvideAndRegisterDocumentSetRequestType.Document();
    document.setId(getDocumentId(submitRequest));
    document.setValue(bytes);
    request.getDocument().add(document);

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
