package org.net4care.xdsconnector;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.WebServiceMessageSender;


@Configuration
public class ConnectorConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.net4care.xdsconnector.service");
		return marshaller;
	}

	@Bean
	public XDSConnector weatherClient(Jaxb2Marshaller marshaller) {
		XDSConnector client = new XDSConnector();

//		SaajSoapMessageFactory webServiceMessageFactory = new SaajSoapMessageFactory();
//		webServiceMessageFactory.setSoapVersion(SoapVersion.SOAP_12);
//		client.setMessageFactory(webServiceMessageFactory);
/*
		WebServiceTemplate webServiceTemplate = new WebServiceTemplate(webServiceMessageFactory);

		client.setWebServiceTemplate(webServiceTemplate);*/

		AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
		mf.setSoapVersion(SoapVersion.SOAP_12);
		mf.setAttachmentCacheDir(new File("/home/mstaus/tmp/"));
		
		client.setMessageFactory(mf);
		
		client.setDefaultUri("http://n4cxds.nfit.au.dk:1026/XdsService/XDSRepository/");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);


		return client;
	}

}

