package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;

//Builder for Registry Stored Queries of type GetFolders
//IHE ITI TF-2a section 3.18.4.1.2.3.7.6
public class GetFoldersQueryBuilder extends QueryBuilder {

    public GetFoldersQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetFolders);
    }

    //TODO: implement set'ters and add'ers for parameters
}
