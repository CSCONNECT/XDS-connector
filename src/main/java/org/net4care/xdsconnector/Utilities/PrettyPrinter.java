package org.net4care.xdsconnector.Utilities;

import org.net4care.xdsconnector.Constants.XDSStatusValues;
import org.net4care.xdsconnector.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class PrettyPrinter {

    private static final Logger logger = LoggerFactory.getLogger(PrettyPrinter.class);

    public static String prettyPrint(RegistryResponseType registryResponseType) {
        String result = "";

        result += "Response status: " + registryResponseType.getStatus() + "\n";
        if (registryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Failure)) {
            result += prettyPrint(registryResponseType.getRegistryErrorList()) + "\n";
        }

        if (registryResponseType.getResponseSlotList() != null) {
            result += "Slots:\n";
            for (SlotType1 elm : registryResponseType.getResponseSlotList().getSlot()) {
                prettyPrint(elm);
            }
        }

        return result;
    }

    public static String prettyPrint(AdhocQueryResponseType queryResponseType) {
        String result = "";

        result += "Response status: " + queryResponseType.getStatus() + "\n";
        if (queryResponseType.getStatus().equals(XDSStatusValues.AdhocQueryResponse.Failure)) {
            result += prettyPrint(queryResponseType.getRegistryErrorList()) + "\n";
        }

        result += "Number of identifiables: " +
                queryResponseType.getRegistryObjectList().getIdentifiable().size() + "\n";
        result += "Identifiables:" + "\n";

        for (JAXBElement<? extends IdentifiableType> elm : queryResponseType.getRegistryObjectList().getIdentifiable()) {

            result += toXml(elm) + "\n\n";
        }

        return result;
    }

    public static String prettyPrint(RegistryErrorList registryErrorList) {
        String result = "";

        int i = 0;
        List<RegistryError> registryErrors = registryErrorList.getRegistryError();
        for (RegistryError error : registryErrors) {
            result += "Error #" + i + ":";
            result += "value = " + error.getValue() + "\n";
            result += "codeContext = " + error.getCodeContext() + "\n";
            result += "errorCode = " + error.getErrorCode() + "\n";
            result += "severity = " + error.getSeverity() + "\n";
            result += "location = " + error.getLocation() + "\n";
            i++;
        }
        return result;
    }

    public static String prettyPrint(SlotType1 slot) {
        String result = "";

        result += "Name = " + slot.getName() + "\n";
        result += "SlotType = " + slot.getSlotType() + "\n";
        result += "ValueListType: " + "\n";
        prettyPrint(slot.getValueList());

        return result;
    }

    public static String prettyPrint(ValueListType valueListType) {
        String result = "";

        for (String elm : valueListType.getValue()) {
            result += elm + " ";
        }

        return result;
    }

    private static String toXml(JAXBElement element) {
        try {
            JAXBContext jc = JAXBContext.newInstance(element.getValue().getClass());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(element, baos);

            return baos.toString();
        } catch (Exception e) {
            logger.error("", e);
        }

        return "";
    }
}
