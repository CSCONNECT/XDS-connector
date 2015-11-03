package org.net4care.xdsconnector.Constants;


public class XDSStatusValues {
    // IHE ITI TF-2a: 3.18.4.1.2.3.6
    public class DocumentEntry {
        public static final String Approved = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
        public static final String Deprecated = "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated";
    }

    //http://ihewiki.wustl.edu/wiki/index.php/XDS-FAQ_2#SubmissionSetStatus_attribute_of_a_Submission_Set
    public class SubmissionSet {
        public static final String Original = "Original";
        public static final String Reference = "Reference";
    }

    // IHE ITI TF-2a: 3.18.4.1.2.3.6.1
    public class AdhocQueryResponse {
        public static final String Success = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
        public static final String PartialSuccess = "urn:ihe:iti:2007:ResponseStatusType:PartialSuccess";
        public static final String Failure = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure";
    }
}
