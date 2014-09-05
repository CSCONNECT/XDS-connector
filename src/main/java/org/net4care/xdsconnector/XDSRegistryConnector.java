package org.net4care.xdsconnector;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.net4care.xdsconnector.service.AdhocQueryRequestType;
import org.net4care.xdsconnector.service.AdhocQueryResponseType;
import org.net4care.xdsconnector.service.AdhocQueryType;
import org.net4care.xdsconnector.service.ObjectFactory;
import org.net4care.xdsconnector.service.ResponseOptionType;
import org.net4care.xdsconnector.service.SlotType1;
import org.net4care.xdsconnector.service.ValueListType;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;

public class XDSRegistryConnector extends WebServiceGatewaySupport {	
	/*
	 *
	 * MHD Get Document Dossier = 
			XDS Registry Stored Query – GetDocuments
			
		MHD Find Document Dossiers = 
	XDS Registry Stored Query –	FindDocuments+FindSubmissionSets+FindFolders

	 * 
	 */
		
	public AdhocQueryResponseType queryRegistry(List<SlotType1> slots) {
		AdhocQueryRequestType request = new ObjectFactory().createAdhocQueryRequestType();
		JAXBElement<AdhocQueryRequestType> requestWrapper = new ObjectFactory().createAdhocQueryRequest(request);

		//Build the XDS query
		ResponseOptionType responseOption = new ResponseOptionType();
		responseOption.setReturnComposedObjects(true);
		//responseOption.setReturnType("ObjectRef"); //ObjectRef only returns references for each object
		responseOption.setReturnType("LeafClass"); // LeafClass is another option, which returns a ton of data on each object
		request.setResponseOption(responseOption);
		
		AdhocQueryType adhocQuery = new AdhocQueryType();
		adhocQuery.getSlot().addAll(slots);
		
		// TODO: generate id randomly?
		adhocQuery.setId("urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d");
		
		request.setAdhocQuery(adhocQuery);
		
		// Query the XDS registry
		@SuppressWarnings("unchecked")
		JAXBElement<AdhocQueryResponseType> result = (JAXBElement<AdhocQueryResponseType>) getWebServiceTemplate().marshalSendAndReceive(requestWrapper, new WebServiceMessageCallback() {
			
			@Override
			public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		        AxiomSoapMessage soapMessage = (AxiomSoapMessage) message;

		        soapMessage.getSoapHeader()
		        		.addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "Action", "wsa"))
		        		.setText("urn:ihe:iti:2007:RegistryStoredQuery");

		        soapMessage.getSoapHeader()
		        	.addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "To", "wsa"))
		            .setText("http://10.29.1.12:1025/XdsService/XDSRegistry/");
		        
		        UUID uuid = UUID.randomUUID();
                soapMessage.getSoapHeader()
                        .addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "MessageID", "wsa"))
                        .setText("urn:uuid:" + uuid.toString());
			}
		});
		
		return (AdhocQueryResponseType) result.getValue();
	}
}
