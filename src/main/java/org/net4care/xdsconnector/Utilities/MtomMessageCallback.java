package org.net4care.xdsconnector.Utilities;

import org.apache.axiom.om.OMOutputFormat;
import org.net4care.xdsconnector.Utilities.MessageCallback;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class MtomMessageCallback extends MessageCallback {
  public MtomMessageCallback(String url, String method) {
    super(url, method);
  }

  @Override
  public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
    super.doWithMessage(message);

    // enable MTOM
    OMOutputFormat outputFormat = new OMOutputFormat();
    outputFormat.setSOAP11(false);
    outputFormat.setDoOptimize(true);
    outputFormat.setCharSetEncoding("utf-8");
    ((AxiomSoapMessage) message).setOutputFormat(outputFormat);
  }
}
