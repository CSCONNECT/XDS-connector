package org.net4care.xdsconnector;

import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

public class XDSConnector extends WebServiceGatewaySupport {
	
	public RetrieveDocumentSetResponseType get() {
		RetrieveDocumentSetRequestType request = new RetrieveDocumentSetRequestType();
		DocumentRequest documentRequest = new DocumentRequest();
		documentRequest.setRepositoryUniqueId("1.3.6.1.4.1.21367.13.40.8");
		documentRequest.setDocumentUniqueId("8b3a64f5-4859-4978-b254-70700e339c2e");
		
		request.getDocumentRequest().add(documentRequest);

		RetrieveDocumentSetResponseType response = (RetrieveDocumentSetResponseType) 
				getWebServiceTemplate().marshalSendAndReceive(
				request);
/*				new SoapActionCallback(
						"http://n4cxds.nfit.au.dk:1026/XdsService/XDSRepository/"));
	*/	return response;
	}
	
	
	public static Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.net4care.xdsconnector.service");
		return marshaller;
	}
}
