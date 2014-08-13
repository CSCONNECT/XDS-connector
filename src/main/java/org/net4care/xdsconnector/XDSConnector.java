package org.net4care.xdsconnector;

import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.net4care.xdsconnector.service.ObjectFactory;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapFaultException;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;

public class XDSConnector extends WebServiceGatewaySupport {
	private static final Logger logger = Logger.getLogger(XDSConnector.class);
	
	public RetrieveDocumentSetResponseType get() {		
		try {
			// using the JAXB Wrapper voids the requirement for a @XMLRootElement annotation on the domain model objects
			RetrieveDocumentSetRequestType request = new RetrieveDocumentSetRequestType();
			JAXBElement<RetrieveDocumentSetRequestType> requestWrapper = new ObjectFactory().createRetrieveDocumentSetRequest(request);
	
			DocumentRequest documentRequest = new DocumentRequest();
			documentRequest.setRepositoryUniqueId("1.3.6.1.4.1.21367.13.40.8");
			documentRequest.setDocumentUniqueId("8b3a64f5-4859-4978-b254-70700e339c2e");
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
				}
			});
			
			return result.getValue();
		}
		catch (SoapFaultException ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
	}
}
