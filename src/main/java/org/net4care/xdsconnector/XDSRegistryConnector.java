package org.net4care.xdsconnector;

import java.io.IOException;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.net4care.xdsconnector.service.AdhocQueryRequestType;
import org.net4care.xdsconnector.service.AdhocQueryResponseType;
import org.net4care.xdsconnector.service.AdhocQueryType;
import org.net4care.xdsconnector.service.ObjectFactory;
import org.net4care.xdsconnector.service.ResponseOptionType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetRequestType.DocumentRequest;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.net4care.xdsconnector.service.SlotType1;
import org.net4care.xdsconnector.service.ValueListType;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
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
	
			
			
	
	
	public AdhocQueryResponseType queryRegistry() {
		AdhocQueryRequestType request = new ObjectFactory().createAdhocQueryRequestType();
		JAXBElement<AdhocQueryRequestType> requestWrapper = new ObjectFactory().createAdhocQueryRequest(request);
		
		ResponseOptionType responseOption = new ResponseOptionType();
		responseOption.setReturnComposedObjects(true);
		//responseOption.setReturnType("ObjectRef"); // LeafClass is another option, which returns a ton of data on each object
		responseOption.setReturnType("LeafClass"); 
		request.setResponseOption(responseOption);
		
		SlotType1 patientIdSlot = new SlotType1();
		patientIdSlot.setName("$XDSDocumentEntryPatientId");
		patientIdSlot.setValueList(new ValueListType());
		patientIdSlot.getValueList().getValue().add("'2512484916^^^&2.16.840.1.113883.3.1558.2.1&ISO'");
		
		SlotType1 statusSlot = new SlotType1();
		statusSlot.setName("$XDSDocumentEntryStatus");
		statusSlot.setValueList(new ValueListType());
		statusSlot.getValueList().getValue().add("('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')");
		
		AdhocQueryType adhocQuery = new AdhocQueryType();
		adhocQuery.getSlot().add(patientIdSlot);
		adhocQuery.getSlot().add(statusSlot);
		
		adhocQuery.setId("urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d");
		
		request.setAdhocQuery(adhocQuery);
		
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
