package org.net4care.xdsconnector;

import java.io.IOException;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.net4care.xdsconnector.service.ObjectFactory;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;

public class XDSRepositoryConnector extends WebServiceGatewaySupport {	
	
	public RetrieveDocumentSetResponseType getDocument(String docId, String RepositoryId) {		
		// using the JAXB Wrapper voids the requirement for a @XMLRootElement annotation on the domain model objects
		RetrieveDocumentSetRequestType request = new RetrieveDocumentSetRequestType();
		JAXBElement<RetrieveDocumentSetRequestType> requestWrapper = new ObjectFactory().createRetrieveDocumentSetRequest(request);

		DocumentRequest documentRequest = new DocumentRequest();
		documentRequest.setRepositoryUniqueId(RepositoryId);
		documentRequest.setDocumentUniqueId(docId);
		request.getDocumentRequest().add(documentRequest);

		@SuppressWarnings("unchecked")
		JAXBElement<RetrieveDocumentSetResponseType> result = (JAXBElement<RetrieveDocumentSetResponseType>) getWebServiceTemplate().marshalSendAndReceive(requestWrapper, new WebServiceMessageCallback() {
			
			@Override
			public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		        AxiomSoapMessage soapMessage = (AxiomSoapMessage) message;

		        soapMessage.getSoapHeader()
		        		.addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "Action", "wsa"))
		        		.setText("urn:ihe:iti:2007:RetrieveDocumentSet");

		        soapMessage.getSoapHeader()
		        	.addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "To", "wsa"))
		        	.setText("http://10.29.1.12:1026/XdsService/XDSRepository/");
		        
		        UUID uuid = UUID.randomUUID();
                soapMessage.getSoapHeader()
                        .addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "MessageID", "wsa"))
                        .setText("urn:uuid:" + uuid.toString());
			}
		});
		
		return result.getValue();
	}
	
}
