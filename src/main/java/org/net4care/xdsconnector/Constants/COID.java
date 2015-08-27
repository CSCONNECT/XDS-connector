package org.net4care.xdsconnector.Constants;

public class COID {

  public static class HL7 {
    // https://www.hl7.org/fhir/v3/Confidentiality/index.html
    public static final String Confidentiality = "2.16.840.1.113883.5.25";
    public static String confidialityCode2DisplayName(String code) {
      // see http://hl7.org/fhir/v3/Confidentiality
      switch (code.toUpperCase()) {
        case "U": return "Unrestricted";
        case "L": return "Low";
        case "M": return "Moderate";
        case "N": return "Normal";
        case "R": return "Restricted";
        case "V": return "Very restricted";
        default: return "";
      }
    }
  }

  public static class DK {
    // See http://svn.medcom.dk/svn/drafts/Standarder/IHE/OID/

    public static final String ClassCode = "2.16.840.1.113883.3.4208.100.9";
    public static final String ClassCode_ClinicalReport_Code = "001";
    public static final String ClassCode_ClinicalReport_DisplayName = "Klinisk rapport";

    public static final String FormatCode = "2.16.840.1.113883.3.4208.100.10";
    public static final String FormatCode_PHMR_Code = "urn:ad:dk:medcom:phmr:full";
    public static final String FormatCode_PHMR_DisplayName = "DK PHMR schema";

    public static final String TemplateId_PHMR = "1.2.208.184.11.1";
    public static final String TemplateId_PHMR_OLD = "2.16.840.1.113883.3.4208.11.1";
    public static final String TemplateId_QRD = "1.2.208.184.13.1";
    public static final String TemplateId_QFDD = "2.16.840.1.113883.10.20.32";

    public static final String TypeCode = "2.16.840.1.113883.6.1";
    public static final String TypeCode_PHMR_Code = "53576-5";
    public static final String TypeCode_PHMR_DisplayName = "Personal health monitoring report Document";

    public static final String LanguageCode = "2.16.840.1.113883.6.121";
    public static final String LanguageCode_DaDk_Code = "da-DK";
    public static final String LanguageCode_DaDk_DisplayName = "Danish-Denmark";

    public static final String FacilityCodeSystem = "2.16.840.1.113883.3.4208.100.11";
    public static final String FacilityCode = "22232009";
    public static String facilityTypeCode2DisplayName(String code) {
      switch (code) {
        case "264372000": return "apotek";
        case "20078004": return "behandlingscenter for stofmisbrugere";
        case "554221000005108": return "bosted";
        case "554031000005103": return "diætistklinik";
        case "546821000005103": return "ergoterapiklinik";
        case "547011000005103": return "fysioterapiklinik";
        case "546811000005109": return "genoptræningscenter";
        case "550621000005101": return "hjemmesygepleje";
        case "22232009": return "hospital";
        case "550631000005103": return "jordemoderklinik";
        case "550641000005106": return "kiropraktor klinik";
        case "550651000005108": return "lægelaboratorium";
        case "394761003": return "lægepraksis";
        case "550661000005105": return "lægevagt";
        case "42665001": return "plejehjem";
        case "554211000005102": return "præhospitals enhed";
        case "550711000005101": return "psykologisk rådgivningsklinik";
        case "550671000005100": return "speciallægepraksis";
        case "554061000005105": return "statsautoriseret fodterapeut";
        case "264361005": return "sundhedscenter";
        case "554041000005106": return "sundhedsforvaltning";
        case "554021000005101": return "sundhedspleje";
        case "550681000005102": return "tandlægepraksis";
        case "550691000005104": return "tandpleje klinik";
        case "550701000005104": return "tandteknisk klinik";
        case "554231000005106": return "vaccinationsklinik";
        case "554051000005108": return "zoneterapiklinik";
        default: return "";
      }
    }
  }
}
