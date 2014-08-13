package org.net4care.xdsconnector;

import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType.DocumentResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages={"org.net4care.xdsconnector"})
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
    	ApplicationContext ctx = SpringApplication.run(Application.class, args);

		XDSConnector client = ctx.getBean(XDSConnector.class);

		RetrieveDocumentSetResponseType response = client.get();
		System.out.println("docsize=" + response.getDocumentResponse().size());
		if (response.getDocumentResponse().size() > 0) {
			DocumentResponse documentResponse = response.getDocumentResponse().get(0);
			System.out.println("documentId=" + documentResponse.getDocumentUniqueId());
			System.out.println("communityId=" + documentResponse.getHomeCommunityId());
			System.out.println("******");
			System.out.println("document=" + new String(documentResponse.getDocument()));
			System.out.println("******");
		}
		System.out.println("requestid=" + response.getRegistryResponse().getRequestId());
		System.out.println("status=" + response.getRegistryResponse().getStatus());
    }
}
