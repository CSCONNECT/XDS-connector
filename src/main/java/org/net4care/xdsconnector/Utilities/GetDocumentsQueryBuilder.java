package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.Constants.DocumentEntryParamenters;
import org.net4care.xdsconnector.Constants.QueryParamenters;

//Builder for Registry Stored Queries of type GetDocuments
//IHE ITI TF-2a section 3.18.4.1.2.3.7.5
public class GetDocumentsQueryBuilder extends QueryBuilder {

    public GetDocumentsQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetDocuments);
    }

    //Either DocumentEntry UUID or DocumentEntry UniqueId shall be specified.
    public GetDocumentsQueryBuilder setUUID(String uuid) {
        addMultiValueSlot(DocumentEntryParamenters.entryUUID, formatQueryParameter(uuid));
        return this;
    }

    //Either DocumentEntry UUID or DocumentEntry UniqueId shall be specified.
    public GetDocumentsQueryBuilder setUniqueId(String uuid) {
        addMultiValueSlot(DocumentEntryParamenters.uniqueId, formatQueryParameter(uuid));
        return this;
    }

    public GetDocumentsQueryBuilder setHomeCommunityId(String homeCommunityId) {
        addMultiValueSlot(QueryParamenters.homeCommunityId, formatQueryParameter(homeCommunityId));
        return this;
    }
}
