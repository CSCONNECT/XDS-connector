package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;

//Builder for Registry Stored Queries of type GetAll
//IHE ITI TF-2a section 3.18.4.1.2.3.7.11
public class GetFolderAndContentsBuilder extends QueryBuilder {

    public GetFolderAndContentsBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetFolderAndContents);
    }

    //TODO: implement set'ters and add'ers for parameters
}
