package org.net4care;

import org.net4care.xdsconnector.XDSConnector;
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
	        
	        XDSConnector client = (XDSConnector) run.getBean("repository");
	        
	        RetrieveDocumentSetResponseType document = client.getDocument("c28ce479-6b5a-4d12-95ad-0d2395319e3d");
	        System.out.println(document.getDocumentResponse().size());
	        DocumentResponse documentResponse = document.getDocumentResponse().get(0);
	        System.out.println(new String(documentResponse.getDocument()));
	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
