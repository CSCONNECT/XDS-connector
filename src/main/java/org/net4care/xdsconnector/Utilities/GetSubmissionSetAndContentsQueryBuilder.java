package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.Constants.DocumentEntryParamenters;
import org.net4care.xdsconnector.Constants.QueryParamenters;
import org.net4care.xdsconnector.Constants.SubmissionSetParamenters;

//Builder for Registry Stored Queries of type GetSubmisssionSetAndContents
//IHE ITI TF-2a section 3.18.4.1.2.3.7.10
public class GetSubmissionSetAndContentsQueryBuilder extends QueryBuilder {

    public GetSubmissionSetAndContentsQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetSubmissionsetAndContents);
    }

    //Either SubmissionSet UUID or SubmissionSet UniqueId must be specified
    public GetSubmissionSetAndContentsQueryBuilder setSubmissionSetUUID(String uuid) {
        addSingleValueSlot(SubmissionSetParamenters.EntryUUID, formatSingleValueQueryParameter(uuid));
        return this;
    }

    //Either SubmissionSet UUID or SubmissionSet UniqueId must be specified
    public GetSubmissionSetAndContentsQueryBuilder setSubmissionSetUniqueId(String uniqueId) {
        addSingleValueSlot(SubmissionSetParamenters.UniqueId, formatSingleValueQueryParameter(uniqueId));
        return this;
    }

    public GetSubmissionSetAndContentsQueryBuilder setFormatCode(String formatCode) {
        addMultiValueSlot(DocumentEntryParamenters.FormatCode, formatQueryParameter(formatCode));
        return this;
    }

    public GetSubmissionSetAndContentsQueryBuilder setConfidentialityCode(String confidentialityCode) {
        addMultiValueSlot(DocumentEntryParamenters.ConfidentialityCode, formatQueryParameter(confidentialityCode));
        return this;
    }

    public GetSubmissionSetAndContentsQueryBuilder setHomeCommunityId(String homeCommunityId) {
        addSingleValueSlot(QueryParamenters.homeCommunityId, formatSingleValueQueryParameter(homeCommunityId));
        return this;
    }

    public GetSubmissionSetAndContentsQueryBuilder setObjectType(String objectType) {
        addMultiValueSlot(DocumentEntryParamenters.ObjectType, formatQueryParameter(objectType));
        return this;
    }
}
