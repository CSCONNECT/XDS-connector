package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;

//Builder for Registry Stored Queries of type FindDocumentsByReferenceId
//IHE ITI TF-2a section 3.18.4.1.2.3.7.14
public class FindDocumentsByReferenceIdQueryBuilder extends QueryBuilder {

    public FindDocumentsByReferenceIdQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.FindDocumentsByReferenceId);
    }

    //TODO: implement set'ters and add'ers for parameters
}
