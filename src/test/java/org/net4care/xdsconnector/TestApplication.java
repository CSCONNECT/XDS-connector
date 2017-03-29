package org.net4care.xdsconnector;

import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType.DocumentResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"org.net4care"})
@EnableAutoConfiguration
public class TestApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext run = SpringApplication.run(TestApplication.class, args);

            IRepositoryConnector xdsRepositoryConnector = run.getBean("xdsRepositoryConnector", RepositoryConnector.class);

            // external XDS, Microsoft XDS document
            // RetrieveDocumentSetResponseType document = xdsRepositoryConnector.retrieveDocumentSet("8b3a64f5-4859-4978-b254-70700e339c2e");

            // local XDS, Microsoft XDS document
            RetrieveDocumentSetResponseType document = xdsRepositoryConnector.retrieveDocumentSet("ba8bb6fa-abaa-4b57-810b-110dcd23788c");

            // KIH document
            //	        RetrieveDocumentSetResponseType document = client.retrieveDocumentSet("6681d517-1423-4913-8f00-c8fdc9957dd7");

            DocumentResponse documentResponse = document.getDocumentResponse().get(0);

            System.out.println("\nresult = " + new String(documentResponse.getDocument()) + "\n");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
