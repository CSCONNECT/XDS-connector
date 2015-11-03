package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;

//Builder for Registry Stored Queries of type GetDocumentsAndAssociations
//IHE ITI TF-2a section 3.18.4.1.2.3.7.8
public class GetDocumentsAndAssociationsBuilder extends QueryBuilder {

    public GetDocumentsAndAssociationsBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.GetDocumentsAndAssociations);
    }

    //TODO: implement set'ters and add'ers for parameters
}
