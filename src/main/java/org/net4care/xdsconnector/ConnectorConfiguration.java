package org.net4care.xdsconnector;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;

@Configuration
public class ConnectorConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.net4care.xdsconnector.service");
		return marshaller;
	}

	@Bean(name="repository")
	public XDSConnector xdsRepositoryConnector(Jaxb2Marshaller marshaller) {
		XDSConnector client = new XDSConnector();

		AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
		mf.setSoapVersion(SoapVersion.SOAP_12);
		mf.setAttachmentCacheDir(new File("/tmp/"));
		
		client.setMessageFactory(mf);
		client.setDefaultUri("http://n4cxds.nfit.au.dk:1026/XdsService/XDSRepository/");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);

		return client;
	}
	
	@Bean(name="registry")
	public XDSConnector xdsRegistryConnector(Jaxb2Marshaller marshaller) {
		XDSConnector client = new XDSConnector();

		AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
		mf.setSoapVersion(SoapVersion.SOAP_12);
		mf.setAttachmentCacheDir(new File("/tmp/"));
		
		client.setMessageFactory(mf);
		client.setDefaultUri("http://n4cxds.nfit.au.dk:1025/XdsService/XDSRegistry/");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);

		return client;
	}
}
