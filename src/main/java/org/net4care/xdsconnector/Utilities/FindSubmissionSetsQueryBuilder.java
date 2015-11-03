package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.Constants.SubmissionSetParamenters;
import org.net4care.xdsconnector.service.*;

import javax.xml.bind.JAXBElement;

//Builder for Registry Stored Queries of type FindSubmissionSets
//IHE ITI TF-2a section 3.18.4.1.2.3.7.2
public class FindSubmissionSetsQueryBuilder extends QueryBuilder {

    public FindSubmissionSetsQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.FindSubmissionSets);
    }

    //Required parameter
    public FindSubmissionSetsQueryBuilder setPatientId(String patientId) {
        addSingleValueSlot(SubmissionSetParamenters.PatientId, formatSingleValueQueryParameter(patientId));
        return this;
    }

    //Required parameter
    //NIST XDS (http://ihexds.nist.gov/) and OpenXDS both expect this value to be one of the XDSStatusValues.DocumentEntry
    //values - not as one might assume either "Original" or "Reference", the "in-going" SubmissionSetStatus
    //(see http://ihewiki.wustl.edu/wiki/index.php/XDS-FAQ_2#SubmissionSetStatus_attribute_of_a_Submission_Set).
    public FindSubmissionSetsQueryBuilder setSubmissionSetStatus(String status) {
        addMultiValueSlot(SubmissionSetParamenters.Status, formatQueryParameter(status));
        return this;
    }

    public FindSubmissionSetsQueryBuilder setSourceId(String sourceId) {
        addMultiValueSlot(SubmissionSetParamenters.SourceId, formatQueryParameter(sourceId));
        return this;
    }

    public FindSubmissionSetsQueryBuilder setSubmissionTimeFrom(String submissionTimeFrom) {
        addSingleValueSlot(SubmissionSetParamenters.SubmissionTimeFrom, formatSingleValueQueryParameter(submissionTimeFrom));
        return this;
    }

    public FindSubmissionSetsQueryBuilder setSubmissionTimeTo(String submissionTimeTo) {
        addSingleValueSlot(SubmissionSetParamenters.SubmissionTimeTo, formatSingleValueQueryParameter(submissionTimeTo));
        return this;
    }

    public FindSubmissionSetsQueryBuilder setAuthorPerson(String authorPerson) {
        addSingleValueSlot(SubmissionSetParamenters.AuthorPerson, formatSingleValueQueryParameter(authorPerson));
        return this;
    }

    public FindSubmissionSetsQueryBuilder setContentType(String contentType) {
        addMultiValueSlot(SubmissionSetParamenters.ContentType, formatQueryParameter(contentType));
        return this;
    }
}
