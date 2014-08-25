package org.net4care;

import org.net4care.xdsconnector.XDSRepositoryConnector;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType.DocumentResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages={"org.net4care"})
@EnableAutoConfiguration
public class TestApplication {
	public static void main(String[] args) {
		try {
	        ConfigurableApplicationContext run = SpringApplication.run(TestApplication.class, args);
	        
	        XDSRepositoryConnector client = run.getBean(XDSRepositoryConnector.class);
	        
	        RetrieveDocumentSetResponseType document = client.getDocument("8b3a64f5-4859-4978-b254-70700e339c2e", "1.3.6.1.4.1.21367.13.40.8");
	        DocumentResponse documentResponse = document.getDocumentResponse().get(0);
	        
	        System.out.println("\nresult = " + new String(documentResponse.getDocument()) + "\n");
	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
