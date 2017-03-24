package org.net4care.xdsconnector;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;

@Configuration
@PropertySource(value="classpath:xds.properties")
public class ConnectorConfiguration {

  @Value("${xds.repositoryUrl}")
	private String repositoryUrl;
	
	@Value("${xds.registryUrl}")
	private String registryUrl;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.net4care.xdsconnector.service");
		return marshaller;
	}

	@Bean
	public RepositoryConnector xdsRepositoryConnector(Jaxb2Marshaller marshaller) {
		RepositoryConnectorBean client = new RepositoryConnectorBean();
		client.setDefaultUri(repositoryUrl);

		AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
		mf.setSoapVersion(SoapVersion.SOAP_12);
		mf.setAttachmentCacheDir(new File(System.getProperty("java.io.tmpdir")));
		mf.setAttachmentCaching(true);
		client.setMessageFactory(mf);

		marshaller.setMtomEnabled(true);
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);

		return client;
	}
	
	@Bean
	public RegistryConnector xdsRegistryConnector(Jaxb2Marshaller marshaller) {
		RegistryConnectorBean client = new RegistryConnectorBean();
		client.setDefaultUri(registryUrl);

		AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
		mf.setSoapVersion(SoapVersion.SOAP_12);
		mf.setAttachmentCacheDir(new File(System.getProperty("java.io.tmpdir")));
		client.setMessageFactory(mf);

		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);

		return client;
	}
}
