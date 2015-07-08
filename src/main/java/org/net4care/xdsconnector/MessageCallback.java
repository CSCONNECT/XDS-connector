package org.net4care.xdsconnector;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.UUID;

public class MessageCallback implements WebServiceMessageCallback {

  private String url;
  private String method;

  public MessageCallback(String url, String method) {
    this.url = url;
    this.method = method;
  }

  @Override
  public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
    AxiomSoapMessage soapMessage = (AxiomSoapMessage) message;

    soapMessage.getSoapHeader()
        .addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "Action", "wsa"))
        .setText("urn:ihe:iti:2007:" + method);

    soapMessage.getSoapHeader()
        .addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "To", "wsa"))
        .setText(url);

    UUID uuid = UUID.randomUUID();
    soapMessage.getSoapHeader()
        .addHeaderElement(new QName("http://www.w3.org/2005/08/addressing", "MessageID", "wsa"))
        .setText("urn:uuid:" + uuid.toString());
  }
}
