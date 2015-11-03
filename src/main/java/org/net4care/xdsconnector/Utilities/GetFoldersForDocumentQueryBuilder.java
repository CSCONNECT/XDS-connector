package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;

//Builder for Registry Stored Queries of type GetAll
//IHE ITI TF-2a section 3.18.4.1.2.3.7.12
public class GetFoldersForDocumentQueryBuilder extends QueryBuilder {

    public GetFoldersForDocumentQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetFoldersForDocument);
    }

    //TODO: implement set'ters and add'ers for parameters
}
