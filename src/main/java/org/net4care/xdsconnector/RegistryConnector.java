package org.net4care.xdsconnector;

import java.util.List;
import java.util.Map;

import org.net4care.xdsconnector.Utilities.QueryBuilder;
import org.net4care.xdsconnector.service.AdhocQueryResponseType;

public interface RegistryConnector {

  AdhocQueryResponseType executeQuery(QueryBuilder queryBuilder);

  List<AdhocQueryResponseType> queryRegistry(String patientId, Map<String, String[]> parameters);

  List<AdhocQueryResponseType> queryRegistry(String patientId, Map<String, String[]> parameters,
      boolean responseAsObjRef);

}