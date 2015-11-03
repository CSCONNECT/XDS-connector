package org.net4care.xdsconnector;

import java.util.*;

import javax.xml.bind.JAXBElement;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.Constants.XDSStatusValues;
import org.net4care.xdsconnector.Utilities.FindDocumentsQueryBuilder;
import org.net4care.xdsconnector.Utilities.FindSubmissionSetsQueryBuilder;
import org.net4care.xdsconnector.Utilities.MessageCallback;
import org.net4care.xdsconnector.Utilities.QueryBuilder;
import org.net4care.xdsconnector.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Configuration
@PropertySource(value="classpath:xds.properties")
public class RegistryConnector extends WebServiceGatewaySupport {

    @Value("${xds.registryUrl}")
    private String registryUrl;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public AdhocQueryResponseType executeQuery(QueryBuilder queryBuilder) {

        // Query the XDS registry
        @SuppressWarnings("unchecked")
        JAXBElement<AdhocQueryResponseType> result = (JAXBElement<AdhocQueryResponseType>) getWebServiceTemplate()
                .marshalSendAndReceive(queryBuilder.getRequestPayload(),
                        new MessageCallback(registryUrl, "RegistryStoredQuery"));

        return (AdhocQueryResponseType) result.getValue();
    }

    public List<AdhocQueryResponseType> queryRegistry(String patientId, Map<String, String[]> parameters) {
        return queryRegistry(patientId, parameters, false); //default to full metadata
    }

    public List<AdhocQueryResponseType> queryRegistry(String patientId, Map<String, String[]> parameters, boolean responseAsObjRef) {
        if (patientId == null || patientId.isEmpty()) return null;

        Map<String, String[]> newParameters = new HashMap<String, String[]>();
        for (String s: parameters.keySet()){
            if (!"PatientID".equals(s)){
                newParameters.put(s, parameters.get(s));
            }
        }

        // Construct the query.
        SlotType1 patientIdSlot = new SlotType1();
        patientIdSlot.setName("$XDSDocumentEntryPatientId");
        patientIdSlot.setValueList(new ValueListType());
        patientIdSlot.getValueList().getValue().add("'" + patientId + "'");

        List<List<SlotType1>> queries = null;
        try {
            queries = createQueries(newParameters);
            validateQuery(queries);
        }
        catch (IllegalRequestException ex) {
            return null;
        }

        // Query the registry.
        List<AdhocQueryResponseType> queryResponses = new LinkedList<AdhocQueryResponseType>();
        for (List<SlotType1> query: queries){
            query.add(patientIdSlot);
            AdhocQueryResponseType queryResponse = queryRegistry(query, responseAsObjRef);
            queryResponses.add(queryResponse);
        }

        return queryResponses;
    }

    private AdhocQueryResponseType queryRegistry(List<SlotType1> slots, boolean responseAsObjRef) {
        AdhocQueryRequestType request = new ObjectFactory().createAdhocQueryRequestType();
        JAXBElement<AdhocQueryRequestType> requestPayload = new ObjectFactory().createAdhocQueryRequest(request);

        //Build the XDS query
        ResponseOptionType responseOption = new ResponseOptionType();
        responseOption.setReturnComposedObjects(true);
        if (responseAsObjRef)
            responseOption.setReturnType("ObjectRef"); //ObjectRef only returns references for each object
        else
            responseOption.setReturnType("LeafClass"); // LeafClass returns full metadata on all objects in result
        request.setResponseOption(responseOption);

        AdhocQueryType adhocQuery = new AdhocQueryType();
        adhocQuery.getSlot().addAll(slots);

        //Three types of stored queries: for documents, submissionsets or folders.
        //Tell it to query for documents:
        adhocQuery.setId(CUUID.StoredQuery.FindDocuments);
        //Tell it to query for submissionsets:
        //adhocQuery.setId(CUUID.StoredQuery.FindSubmissionSets);

        request.setAdhocQuery(adhocQuery);

        // Query the XDS registry
        @SuppressWarnings("unchecked")
        JAXBElement<AdhocQueryResponseType> result = (JAXBElement<AdhocQueryResponseType>) getWebServiceTemplate()
                .marshalSendAndReceive(requestPayload, new MessageCallback(registryUrl, "RegistryStoredQuery"));

        return (AdhocQueryResponseType) result.getValue();
    }

    private List<List<SlotType1>> createQueries(Map<String, String[]> parameters) throws IllegalRequestException {
        if (parameters.isEmpty()) {
            List<List<SlotType1>> recursiveQueries = new LinkedList<List<SlotType1>>();
            recursiveQueries.add(new LinkedList<SlotType1>());
            return recursiveQueries;
        }

        String s = parameters.keySet().iterator().next();
        Map<String, String[]> rec = new HashMap<String, String[]>();
        for (String innerS: parameters.keySet()) {
            if (!innerS.equals(s)) {
                rec.put(innerS, parameters.get(innerS));
            }
        }

        List<List<SlotType1>> recursiveQueries = createQueries(rec);
        List<List<SlotType1>> newList  = new LinkedList<List<SlotType1>>();

        for (String value: parameters.get(s)) {
            SlotType1 slot = createSingleQuery(s, value);

            for (List<SlotType1> innerList : recursiveQueries) {
                List<SlotType1> newInnerList = new LinkedList<SlotType1>();
                newInnerList.add(slot);
                newInnerList.addAll(innerList);
                newList.add(newInnerList);
            }
        }

        return newList;
    }

    private SlotType1 createSingleQuery(String s, String value) throws IllegalRequestException {
        SlotType1 slot = new SlotType1();
        slot.setValueList(new ValueListType());

        switch (s) {
            case "creationTimeFrom":
                slot.setName("$XDSDocumentEntryCreationTimeFrom");
                slot.getValueList().getValue().add(value);
                return slot;
            case "creationTimeTo":
                slot.setName("$XDSDocumentEntryCreationTimeTo");
                slot.getValueList().getValue().add(value);
                return slot;
            case "serviceStartTimeFrom":
                slot.setName("$XDSDocumentEntryServiceStartTimeFrom");
                slot.getValueList().getValue().add(value);
                return slot;
            case "serviceStartTimeTo":
                slot.setName("$XDSDocumentEntryServiceStartTimeTo");
                slot.getValueList().getValue().add(value);
                return slot;
            case "serviceStopTimeFrom":
                slot.setName("$XDSDocumentEntryServiceStopTimeFrom");
                slot.getValueList().getValue().add(value);
                return slot;
            case "serviceStopTimeTo":
                slot.setName("$XDSDocumentEntryServiceStopTimeTo");
                slot.getValueList().getValue().add(value);
                return slot;
            case "status":
                // TODO: need to test that this work, as it is supposed to create a list like this ('status-1', 'status-2', 'status-3', 'etc...')
                slot.setName("$XDSDocumentEntryStatus");
                slot.getValueList().getValue().add("('urn:oasis:names:tc:ebxml-regrep:StatusType:" + value + "')");
                return slot;
            case "classCode":
                // TODO: should this also be encoded as a string? (e.g. add("'" + value + "'") and not add(value))
                slot.setName("$XDSDocumentEntryClassCode");
                slot.getValueList().getValue().add(value);
                return slot;
            default:
                throw new IllegalRequestException();
        }
    }

    private void validateQuery(List<List<SlotType1>> queries) throws IllegalRequestException {
        int creationFrom = 0, creationTo= 0, serviceStartFrom = 0, serviceStartTo = 0, serviceStopFrom = 0, serviceStopTo = 0;

        for (List<SlotType1> list: queries) {
            boolean statusDefined = false;

            for (SlotType1 slot: list) {
                if("$XDSDocumentEntryStatus".equals(slot.getName())){
                    statusDefined = true;
                }

                switch (slot.getName()) {
                    case "$XDSDocumentEntryCreationTimeFrom":
                        creationFrom++;
                        break;
                    case "$XDSDocumentEntryCreationTimeTo":
                        creationTo++;
                        break;
                    case "$XDSDocumentEntryServiceStartTimeFrom":
                        serviceStartFrom++;
                        break;
                    case "$XDSDocumentEntryServiceStartTimeTo":
                        serviceStartTo++;
                        break;
                    case "$XDSDocumentEntryServiceStopTimeTo":
                        serviceStopTo++;
                        break;
                    case "$XDSDocumentEntryServiceStopTimeFrom":
                        serviceStopFrom++;
                        break;
                }
            }

            if (creationFrom > 1 || creationTo > 1 || serviceStartFrom > 1 || serviceStartTo > 1 ||	serviceStopTo > 1 || serviceStopFrom > 1) {
                throw new IllegalRequestException();
            }

            if (!statusDefined) {
                SlotType1 statusSlot = new SlotType1();
                statusSlot.setValueList(new ValueListType());
                statusSlot.setName("$XDSDocumentEntryStatus");
                statusSlot.getValueList().getValue().add("('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')");
                list.add(statusSlot);
            }
        }
    }

    private class IllegalRequestException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}
