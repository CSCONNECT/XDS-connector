package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.Constants.QueryParamenters;

//Builder for Registry Stored Queries of type GetAssociations
//IHE ITI TF-2a section 3.18.4.1.2.3.7.7
public class GetAssociationsQueryBuilder extends QueryBuilder {

    public GetAssociationsQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetAssociations);
    }

    //Required parameter
    public GetAssociationsQueryBuilder setUUID(String uuid) {
        addMultiValueSlot(QueryParamenters.uuid, formatQueryParameter(uuid));
        return this;
    }

    public GetAssociationsQueryBuilder setHomeCommunityId(String homeCommunityId) {
        addMultiValueSlot(QueryParamenters.homeCommunityId, formatQueryParameter(homeCommunityId));
        return this;
    }
}
