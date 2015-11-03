package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.Constants.DocumentEntryParamenters;
import org.net4care.xdsconnector.Constants.XDSStatusValues;
import org.net4care.xdsconnector.service.*;

import javax.xml.bind.JAXBElement;

//Builder for Registry Stored Queries of type FindDocuments
//IHE ITI TF-2a section 3.18.4.1.2.3.7.1
public class FindDocumentsQueryBuilder extends QueryBuilder {

    public FindDocumentsQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.FindDocuments);
    }

    //Required parameter
    public FindDocumentsQueryBuilder setPatientId(String patientId) {
        addSingleValueSlot(DocumentEntryParamenters.PatientId, formatSingleValueQueryParameter(patientId));
        return this;
    }

    //Required parameter
    public FindDocumentsQueryBuilder addDocumentStatus(String status) {
        addMultiValueSlot(DocumentEntryParamenters.Status, formatQueryParameter(status));
        return this;
    }

    public FindDocumentsQueryBuilder addClassCode(String classCode) {
        addMultiValueSlot(DocumentEntryParamenters.ClassCode, formatQueryParameter(classCode));
        return this;
    }

    public FindDocumentsQueryBuilder addTypeCode(String typeCode) {
        addMultiValueSlot(DocumentEntryParamenters.TypeCode, formatQueryParameter(typeCode));
        return this;
    }

    public FindDocumentsQueryBuilder addPracticeSettingCode(String practiceSettingCode) {
        addMultiValueSlot(DocumentEntryParamenters.PracticeSettingCode, formatQueryParameter(practiceSettingCode));
        return this;
    }

    public FindDocumentsQueryBuilder setCreationTimeFrom(String creationTimeFrom) {
        addMultiValueSlot(DocumentEntryParamenters.CreationTimeFrom, formatSingleValueQueryParameter(creationTimeFrom));
        return this;
    }

    public FindDocumentsQueryBuilder setCreationTimeTo(String creationTimeTo) {
        addMultiValueSlot(DocumentEntryParamenters.CreationTimeTo, formatSingleValueQueryParameter(creationTimeTo));
        return this;
    }

    public FindDocumentsQueryBuilder setServiceStartTimeFrom(String serviceStartTimeFrom) {
        addMultiValueSlot(DocumentEntryParamenters.ServiceStartTimeFrom, formatSingleValueQueryParameter(serviceStartTimeFrom));
        return this;
    }

    public FindDocumentsQueryBuilder setServiceStartTimeTo(String serviceStartTimeTo) {
        addMultiValueSlot(DocumentEntryParamenters.ServiceStartTimeTo, formatSingleValueQueryParameter(serviceStartTimeTo));
        return this;
    }

    public FindDocumentsQueryBuilder setServiceStopTimeFrom(String serviceStopTimeFrom) {
        addMultiValueSlot(DocumentEntryParamenters.ServiceStopTimeFrom, formatSingleValueQueryParameter(serviceStopTimeFrom));
        return this;
    }

    public FindDocumentsQueryBuilder setServiceStopTimeTo(String serviceStopTimeTo) {
        addMultiValueSlot(DocumentEntryParamenters.ServiceStopTimeTo, formatSingleValueQueryParameter(serviceStopTimeTo));
        return this;
    }

    public FindDocumentsQueryBuilder addHealthcareFacilityTypeCode(String healthcareFacilityTypeCode) {
        addMultiValueSlot(DocumentEntryParamenters.HealthcareFacilityTypeCode, formatQueryParameter(healthcareFacilityTypeCode));
        return this;
    }

    public FindDocumentsQueryBuilder addEventCodeList(String eventCodeList) {
        addMultiValueSlot(DocumentEntryParamenters.EventCodeList, formatQueryParameter(eventCodeList));
        return this;
    }

    public FindDocumentsQueryBuilder addConfidentialityCode(String confidentialityCode) {
        addMultiValueSlot(DocumentEntryParamenters.ConfidentialityCode, formatQueryParameter(confidentialityCode));
        return this;
    }

    public FindDocumentsQueryBuilder addAuthorPerson(String authorPerson) {
        addMultiValueSlot(DocumentEntryParamenters.AuthorPerson, formatQueryParameter(authorPerson));
        return this;
    }

    public FindDocumentsQueryBuilder addFormatCode(String formatCode) {
        addMultiValueSlot(DocumentEntryParamenters.FormatCode, formatQueryParameter(formatCode));
        return this;
    }

    public FindDocumentsQueryBuilder addObjectType(String objectType) {
        addMultiValueSlot(DocumentEntryParamenters.ObjectType, formatQueryParameter(objectType));
        return this;
    }
}
