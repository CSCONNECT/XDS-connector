package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.CUUID;
import org.net4care.xdsconnector.Constants.FolderParamenters;
import org.net4care.xdsconnector.Constants.QueryParamenters;

//Builder for Registry Stored Queries of type FindFolders
//IHE ITI TF-2a section 3.18.4.1.2.3.7.3
public class FindFoldersQueryBuilder extends QueryBuilder {

    public FindFoldersQueryBuilder() {
        super();
        adhocQuery.setId(CUUID.StoredQuery.FindFolders);
    }

    //Required parameter
    public FindFoldersQueryBuilder setPatientId(String patientId) {
        addSingleValueSlot(FolderParamenters.PatientId, formatSingleValueQueryParameter(patientId));
        return this;
    }

    //Required parameter
    public FindFoldersQueryBuilder addStatus(String status) {
        addMultiValueSlot(FolderParamenters.Status, formatQueryParameter(status));
        return this;
    }

    public FindFoldersQueryBuilder setLastUpdateTimeFrom(String lastUpdateTimeFrom) {
        addSingleValueSlot(FolderParamenters.LastUpdateTimeFrom, formatSingleValueQueryParameter(lastUpdateTimeFrom));
        return this;
    }

    public FindFoldersQueryBuilder setLastUpdateTimeTo(String lastUpdateTimeTo) {
        addSingleValueSlot(FolderParamenters.LastUpdateTimeTo, formatSingleValueQueryParameter(lastUpdateTimeTo));
        return this;
    }

    public FindFoldersQueryBuilder addCodeList(String codeList) {
        addMultiValueSlot(FolderParamenters.CodeList, formatQueryParameter(codeList));
        return this;
    }
}
