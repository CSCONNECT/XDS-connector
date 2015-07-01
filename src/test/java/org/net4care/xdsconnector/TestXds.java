package org.net4care.xdsconnector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.net4care.xdsconnector.ConnectorConfiguration;
import org.net4care.xdsconnector.XDSRepositoryConnector;
import org.net4care.xdsconnector.service.ProvideAndRegisterDocumentSetRequestType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration("classpath:applicationContext-test.xml")
@ContextConfiguration(classes = ConnectorConfiguration.class)
public class TestXds {

  @Autowired
  XDSRepositoryConnector xdsRepositoryConnector;

  @Test
  public void getDocument() {
    try {
      // n4cxds
      // RetrieveDocumentSetResponseType document = xdsRepositoryConnector.getDocument("8b3a64f5-4859-4978-b254-70700e339c2e");

      RetrieveDocumentSetResponseType document = xdsRepositoryConnector.getDocument("ba8bb6fa-abaa-4b57-810b-110dcd23788c");

      RetrieveDocumentSetResponseType.DocumentResponse documentResponse = document.getDocumentResponse().get(0);

      System.out.println("\nresult: " + new String(documentResponse.getDocument()) + "\n");
    }
    catch (Throwable t) {
      System.out.println("\nerror: " + t.getMessage() + "\n");
    }
  }

  @Test
  public void putDocument() {
    try {
      UUID uuid = UUID.randomUUID();
      ProvideAndRegisterDocumentSetRequestType document = xdsRepositoryConnector.putDocument(uuid.toString());

    }
    catch (Throwable t) {
      System.out.println("\nerror: " + t.getMessage() + "\n");
    }
  }

}
