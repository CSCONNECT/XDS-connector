package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;

//Builder for Registry Stored Queries of type GetAll
//IHE ITI TF-2a section 3.18.4.1.2.3.7.13
public class GetRelatedDocumentsQueryBuilder extends QueryBuilder {

    public GetRelatedDocumentsQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetRelatedDocuments);
    }

    //TODO: implement set'ters and add'ers for parameters
}
