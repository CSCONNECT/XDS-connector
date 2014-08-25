package org.net4care.xdsconnector;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;

@Configuration
@PropertySource(value="classpath:xds.properties")
public class ConnectorConfiguration {

	@Value("${xds.repository}")
	private String repositoryUrl;	
	
	@Value("${xds.registry}")
	private String registryUrl;

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.net4care.xdsconnector.service");
		return marshaller;
	}

	@Bean
	public XDSRepositoryConnector xdsRepositoryConnector(Jaxb2Marshaller marshaller) {
		XDSRepositoryConnector client = new XDSRepositoryConnector();

		AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
		mf.setSoapVersion(SoapVersion.SOAP_12);
		mf.setAttachmentCacheDir(new File("/tmp/"));
		
		client.setMessageFactory(mf);
		client.setDefaultUri(repositoryUrl);
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);

		return client;
	}
	
	@Bean
	public XDSRegistryConnector xdsRegistryConnector(Jaxb2Marshaller marshaller) {
		XDSRegistryConnector client = new XDSRegistryConnector();

		AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
		mf.setSoapVersion(SoapVersion.SOAP_12);
		mf.setAttachmentCacheDir(new File("/tmp/"));
		
		client.setMessageFactory(mf);
		client.setDefaultUri(registryUrl);
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);

		return client;
	}
}
