package org.net4care.xdsconnector.Constants;

public class CUUID {

    //For a list of UUIDs define by XDS: http://ihewiki.wustl.edu/wiki/index.php/Notes_on_XDS_Profile#UUIDs_Defined_by_XDS
    public class DocumentEntry {
        // ITI Vol3 4.2.5.2
        public static final String stableDocument = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";
        public static final String onDemandDocument = "urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248";

        public static final String authorId = "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d";
        public static final String classCode = "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";
        public static final String confidentialityCode = "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f";
        public static final String eventCodeList = "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
        public static final String formatCode = "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d";
        public static final String healthcareFacilityTypeCode = "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1";
        public static final String patientId = "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427";
        public static final String practiceSettingCode = "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead";
        public static final String typeCode = "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983";
        public static final String uniqueId = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";
    }

    public class SubmissionSet {
        // ITI Vol3 4.2.5.1
        public static final String classificationNode = "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd";

        public static final String authorId = "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d";
        public static final String contentTypeCode = "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500";
        public static final String patientId = "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446";
        public static final String sourceId = "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832";
        public static final String uniqueId = "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8";
    }

    public class StoredQuery {
        /*IDs for the registry stored query */
        public static final String FindDocuments = "urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d";
        public static final String FindSubmissionSets = "urn:uuid:f26abbcb-ac74-4422-8a30-edb644bbc1a9";
        public static final String FindFolders = "urn:uuid:958f3006-baad-4929-a4de-ff1114824431";
        public static final String GetAll = "urn:uuid:10b545ea-725c-446d-9b95-8aeb444eddf3";
        public static final String GetDocuments = "urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4";
        public static final String GetFolders = "urn:uuid:5737b14c-8a1a-4539-b659-e03a34a5e1e4";
        public static final String GetAssociations = "urn:uuid:a7ae438b-4bc2-4642-93e9-be891f7bb155";
        public static final String GetDocumentsAndAssociations = "urn:uuid:bab9529a-4a10-40b3-a01f-f68a615d247a";
        public static final String GetSubmissionSets = "urn:uuid:51224314-5390-4169-9b91-b1980040715a";
        public static final String GetSubmissionsetAndContents = "urn:uuid:e8e3cb2c-e39c-46b9-99e4-c12f57260b83";
        public static final String GetFolderAndContents = "urn:uuid:b909a503-523d-4517-8acf-8e5834dfc4c7";
        public static final String GetFoldersForDocument = "urn:uuid:10cae35a-c7f9-4cf5-b61e-fc3278ffb578";
        public static final String GetRelatedDocuments = "urn:uuid:d90e5407-b356-4d91-a89f-873917b4b0e6";

        public static final String FindDocumentsByReferenceId = "urn:uuid:12941a89-e02e-4be5-967c-ce4bfc8fe492";

        //Multipatient Stored Queries:
        public static final String FindDocumentsForMultiplePatients = "urn:uuid:3d1bdb10-39a2-11de-89c2-2f44d94eaa9f";
        public static final String FindFoldersForMultiplePatients = "urn:uuid:50d3f5ac-39a2-11de-a1ca-b366239e58df";
    }
}
