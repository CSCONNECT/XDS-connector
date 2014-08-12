package org.net4care.xdsconnector;


import org.net4care.xdsconnector.service.RetrieveDocumentSetResponseType;
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
		System.out.println(response);
    	
//        SpringApplication.run(Application.class, args);
    }
}
