package org.net4care.xdsconnector.Constants;

public class COID {

  public class HL7 {
    // https://www.hl7.org/fhir/v3/Confidentiality/index.html
    public static final String Confidentiality = "2.16.840.1.113883.5.25";
  }

  public class DK {
    public static final String ClassCode = "2.16.840.1.113883.3.4208.100.9";
    public static final String ClassCode_ClinicalReport_Code = "001";
    public static final String ClassCode_ClinicalReport_DisplayName = "Klinisk rapport";

    public static final String FormatCode = "2.16.840.1.113883.3.4208.100.10";
    public static final String FormatCode_PHMR_Code = "urn:ad:dk:medcom:phmr:full";
    public static final String FormatCode_PHMR_DisplayName = "DK PHMR schema";

    public static final String TypeCode = "2.16.840.1.113883.6.1";
    public static final String TypeCode_PHMR_Code = "53576-5";
    public static final String TypeCode_PHMR_DisplayName = "Personal health monitoring report Document";

    public static final String LanguageCode = "2.16.840.1.113883.6.121";
    public static final String LanguageCode_DaDk_Code = "da-DK";
    public static final String LanguageCode_DaDk_DisplayName = "Danish-Denmark";
  }
}
