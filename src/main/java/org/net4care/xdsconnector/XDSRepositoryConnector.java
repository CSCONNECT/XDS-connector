package org.net4care.xdsconnector;

import javax.xml.bind.JAXBElement;

import org.net4care.xdsconnector.service.*;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.util.UUID;

@Configuration
@PropertySource(value="classpath:xds.properties")
public class XDSRepositoryConnector extends WebServiceGatewaySupport {

  private MetadataHelper metadataHelper = new MetadataHelper();

  @Value("${xds.repositoryId}")
  private String repositoryId;

  @Value("${xds.repositoryUrl}")
  private String repositoryUrl;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  public RetrieveDocumentSetResponseType getDocument(String docId) {
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

  public ProvideAndRegisterDocumentSetRequestType putDocument(String cda) {
    // using the JAXB Wrapper voids the requirement for a @XMLRootElement annotation on the domain model objects
    ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();
    JAXBElement<ProvideAndRegisterDocumentSetRequestType> requestWrapper = new ObjectFactory().createProvideAndRegisterDocumentSetRequest(request);

    String homeCommunityId = "87"; // TODO
    SubmitObjectsRequest submitRequest = metadataHelper.buildSubmitObjectsRequest(cda, homeCommunityId);
    request.setSubmitObjectsRequest(submitRequest);

    @SuppressWarnings("unchecked")
    JAXBElement<ProvideAndRegisterDocumentSetRequestType> result = (JAXBElement<ProvideAndRegisterDocumentSetRequestType>) getWebServiceTemplate()
        .marshalSendAndReceive(requestWrapper, new XDSMessageCallback(repositoryUrl, "ProvideAndRegisterDocumentSetRequest"));

    return result.getValue();
  }
}
