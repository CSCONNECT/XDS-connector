package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.service.*;

import javax.xml.bind.JAXBElement;

public class QueryBuilder {
    //ObjectRef only returns references for each object while
    //LeafClass returns full metadata on all objects in result
    public static final String RETURN_TYPE_OBJECTREFERENCES = "ObjectRef";
    public static final String RETURN_TYPE_FULLMETADATA = "LeafClass";

    protected AdhocQueryRequestType request;
    protected AdhocQueryType adhocQuery;
    JAXBElement<AdhocQueryRequestType> requestPayload;

    public QueryBuilder() {
        adhocQuery = new AdhocQueryType();
        request = new ObjectFactory().createAdhocQueryRequestType();
        requestPayload = new ObjectFactory().createAdhocQueryRequest(request);
        request.setAdhocQuery(adhocQuery);
    }

    //Required option
    public QueryBuilder setReturnType(String type) {
        ResponseOptionType responseOption = new ResponseOptionType();
        responseOption.setReturnComposedObjects(true); //meaningless but required
        responseOption.setReturnType(type);
        request.setResponseOption(responseOption);
        return this;
    }

    public JAXBElement<AdhocQueryRequestType> getRequestPayload() {
        return requestPayload;
    }

    protected void addSingleValueSlot(String name, String value) {
        SlotType1 slot = null;
        for (SlotType1 slotelm : adhocQuery.getSlot()) {
            if (slotelm.getName().equals(name)) {
                slot = slotelm;
                break;
            }
        }
        if (slot == null) {
            slot = new SlotType1();
            slot.setName(name);
            slot.setValueList(new ValueListType());
            slot.getValueList().getValue().add(value);
            adhocQuery.getSlot().add(slot);
        } else {
            slot.getValueList().getValue().clear();
            slot.getValueList().getValue().add(value);
        }
    }

    protected void addMultiValueSlot(String name, String value) {
        SlotType1 slot = null;
        for (SlotType1 slotelm : adhocQuery.getSlot()) {
            if (slotelm.getName().equals(name)) {
                slot = slotelm;
                break;
            }
        }
        if (slot == null) {
            slot = new SlotType1();
            slot.setName(name);
            slot.setValueList(new ValueListType());
            slot.getValueList().getValue().add(value);
            adhocQuery.getSlot().add(slot);
        } else {
            slot.getValueList().getValue().add(value);
        }
    }

    //Formatting of values see: 3.18.4.1.2.3.3 through 3.18.4.1.2.3.5 in IHE ITI TF-2a
    protected String formatQueryParameter(String value) {
        return "(\'" + value + "\')";
    }

    protected String formatSingleValueQueryParameter(String value) {
        return "\'" + value + "\'";
    }
}
