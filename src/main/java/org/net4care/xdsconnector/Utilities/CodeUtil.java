package org.net4care.xdsconnector.Utilities;

public class CodeUtil {

  public static String prefixOID(String id) {
    if (id == null)
      id = "";
    return (id.startsWith("urn:oid:")) ? id : "urn:oid:" + id;
  }

  public static String prefixUUID(String id) {
    if (id == null)
      id = "";
    return (id.startsWith("urn:uuid:")) ? id : "urn:uuid:" + id;
  }

}
