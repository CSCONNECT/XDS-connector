package org.net4care.xdsconnector;

import javax.xml.bind.JAXBElement;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.service.*;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.util.List;
import java.util.UUID;

@Configuration
@PropertySource(value="classpath:xds.properties")
public class XDSRepositoryConnector extends WebServiceGatewaySupport {

  // TODO: load home community id from properties
  private MetadataHelper metadataHelper = new MetadataHelper("");

  @Value("${xds.repositoryId}")
  private String repositoryId;

  @Value("${xds.repositoryUrl}")
  private String repositoryUrl;

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
          .marshalSendAndReceive(requestWrapper, new XDSMessageCallback(repositoryUrl, "RetrieveDocumentSet"));

      return result.getValue();
    }
    catch (Throwable t) {
      throw t;
    }
	}

  public RegistryResponseType provideAndRegisterCDADocument(String cda) {
    try {
      // using the JAXB Wrapper voids the requirement for a @XMLRootElement annotation on the domain model objects
      ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();
      JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestWrapper = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

      String associatedId = UUID.randomUUID().toString();

      // metadata
      SubmitObjectsRequest submitRequest = metadataHelper.buildSubmitObjectsRequest(cda, associatedId);
      request.setSubmitObjectsRequest(submitRequest);

      // document
      ProvideAndRegisterDocumentSetRequestType.Document document = new ProvideAndRegisterDocumentSetRequestType.Document();
      document.setId(getDocumentId(submitRequest));
      document.setValue(cda.getBytes());
      request.getDocument().add(document);

      // execute
      @SuppressWarnings("unchecked")
      JAXBElement<RegistryResponseType> result = (JAXBElement<RegistryResponseType>) getWebServiceTemplate()
          .marshalSendAndReceive(requestWrapper, new XDSMessageCallback(repositoryUrl, "ProvideAndRegisterDocumentSet-b"));

      return result.getValue();
    }
    catch (Throwable t) {
      throw t;
    }
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
