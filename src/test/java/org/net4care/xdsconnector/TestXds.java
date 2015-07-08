package org.net4care.xdsconnector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.net4care.xdsconnector.service.RegistryResponseType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration("classpath:applicationContext-test.xml")
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
  public void provideAndRegisterCDADocument() {
    try {
      String path = "examples/Ex1-Weight_measurement.xml";
      java.net.URL url = getClass().getClassLoader().getResource(path);
      List<String> lines = Files.readAllLines(Paths.get(url.toURI()), Charset.forName("UTF-8"));
      String cda = StringUtils.collectionToDelimitedString(lines, "\n");
      // make a new unique id
      cda = cda.replace("aa2386d0-79ea-11e3-981f-0800200c9a66", UUID.randomUUID().toString());
      RegistryResponseType response = xdsRepositoryConnector.provideAndRegisterCDADocument(cda);
      System.out.println("\nResult: " + new String(response.getStatus()) + "\n");
    }
    catch (Throwable t) {
      System.out.println("\nError: " + t.getMessage() + "\n");
    }
  }

}
