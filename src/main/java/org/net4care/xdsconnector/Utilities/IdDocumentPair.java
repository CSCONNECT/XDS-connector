package org.net4care.xdsconnector.Utilities;

import org.w3c.dom.Document;

public class IdDocumentPair {
  private String documentEntryId;
  private Document cdaDocument;
  
  public IdDocumentPair(String documentEntryId, Document cdaDocument) {
    super();
    this.documentEntryId = documentEntryId;
    this.cdaDocument = cdaDocument;
  }

  public String getDocumentEntryId() {
    return documentEntryId;
  }

  public Document getCdaDocument() {
    return cdaDocument;
  }
}
