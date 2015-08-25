package org.net4care.xdsconnector;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.net4care.xdsconnector.service.RegistryResponseType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import javax.validation.constraints.AssertTrue;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConnectorConfiguration.class)
public class TestXds {

  @Autowired
  RepositoryConnector xdsRepositoryConnector;

  @Test
  public void retrieveDocumentSet() {
    try {
      // n4cxds
      // RetrieveDocumentSetResponseType document = xdsRepositoryConnector.retrieveDocumentSet("8b3a64f5-4859-4978-b254-70700e339c2e");

      RetrieveDocumentSetResponseType document = xdsRepositoryConnector.retrieveDocumentSet("ba8bb6fa-abaa-4b57-810b-110dcd23788c");

      RetrieveDocumentSetResponseType.DocumentResponse documentResponse = document.getDocumentResponse().get(0);

      System.out.println("\nResult: " + new String(documentResponse.getDocument()) + "\n");
    }
    catch (Throwable t) {
      System.out.println("\nError: " + t.getMessage() + "\n");
    }
  }

  @Test
  public void provideAndRegisterAndRetrieveCDADocument() {
    try {
      // the date for the document is 13.01.2014
      java.net.URL url = getClass().getClassLoader().getResource("examples/Ex1-Weight_measurement.xml");
      List<String> lines = Files.readAllLines(Paths.get(url.toURI()), StandardCharsets.UTF_8);
      String providedDocument = StringUtils.collectionToDelimitedString(lines, "\n");

      // make a new unique id
      String uuid = UUID.randomUUID().toString();
      providedDocument = providedDocument.replace("aa2386d0-79ea-11e3-981f-0800200c9a66", uuid);
      RegistryResponseType provideResponse = xdsRepositoryConnector.provideAndRegisterCDADocument(providedDocument);
      System.out.println("\nProvideAndRegister Result: " + provideResponse.getStatus());
      Assert.assertTrue(provideResponse.getStatus().endsWith("Success"));

      String docId = "1.2.208.184^" + uuid.replace("-", "").substring(0, 16);
      RetrieveDocumentSetResponseType retriveResponse = xdsRepositoryConnector.retrieveDocumentSet(docId);
      Assert.assertTrue(retriveResponse.getRegistryResponse().getStatus().endsWith("Success"));
      System.out.println("\nRetrieveDocument Result: " + retriveResponse.getRegistryResponse().getStatus());

      RetrieveDocumentSetResponseType.DocumentResponse documentResponse = retriveResponse.getDocumentResponse().get(0);
      String retrievedDocument = new String(documentResponse.getDocument(), StandardCharsets.UTF_8);
      Assert.assertEquals(providedDocument, retrievedDocument);
    }
    catch (Throwable t) {
      System.out.println("\nError: " + t.getMessage());
    }
  }
}
