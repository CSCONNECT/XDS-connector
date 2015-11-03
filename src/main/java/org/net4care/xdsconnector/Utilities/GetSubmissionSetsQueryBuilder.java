package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.Constants.QueryParamenters;

//Builder for Registry Stored Queries of type GetSubmissionSets
//IHE ITI TF-2a section 3.18.4.1.2.3.7.9
public class GetSubmissionSetsQueryBuilder extends QueryBuilder {

    public GetSubmissionSetsQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetSubmissionSets);
    }

    //Required parameter
    public GetSubmissionSetsQueryBuilder setUUID(String uuid) {
        addMultiValueSlot(QueryParamenters.uuid, formatQueryParameter(uuid));
        return this;
    }

    public GetSubmissionSetsQueryBuilder setHomeCommunityId(String homeCommunityId) {
        addMultiValueSlot(QueryParamenters.homeCommunityId, formatQueryParameter(homeCommunityId));
        return this;
    }
}
