package parsing;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import parsing.exceptions.InvalidTagException;

public class ProbabilityEnergyTimeProfileReader {

    /**
     * Trigger for retrieveProbEnergyTimeHelper
     * @param n
     * @throws InvalidTagException
     */
    public static ProbabilityEnergyTimeProfile retrieveProbEnergyTime(String nodeId, Document doc) throws InvalidTagException {
        ProbabilityEnergyTimeProfile profile = new ProbabilityEnergyTimeProfile();

        retrieveProbEnergyTimeHelper(doc.getElementsByTagName("GQAM:GaStep"), nodeId, profile);
        retrieveProbEnergyTimeHelper(doc.getElementsByTagName("PAM:PaStep"), nodeId, profile);
        retrieveProbEnergyTimeHelper(doc.getElementsByTagName("GRM:ResourceUsage"), nodeId, profile);
        retrieveProbEnergyTimeHelper(doc.getElementsByTagName("PAM:PaCommStep"), nodeId, profile);

        return profile;
    }

    /**
     * Parses the xmi file in search for pertinent annotations of an behavioral diagram
     * $nodes indicates the xmi nodes to be analyzed
     * $n indicates the object in which the resultant data will be put
     * @param nodes
     * @param n
     * @throws InvalidTagException
     */
    private static void retrieveProbEnergyTimeHelper(NodeList nodes, String nodeId, ProbabilityEnergyTimeProfile profile)  throws InvalidTagException {
        for (int k = 0; k < nodes.getLength(); k++) {
            org.w3c.dom.Node tmp;
            org.w3c.dom.Node item = nodes.item(k);
            NamedNodeMap kAttrs = item.getAttributes();

            if (kAttrs.getNamedItem("base_NamedElement").getTextContent().equals(nodeId)) {
                if (kAttrs.getNamedItem("prob") != null) {
                    profile.setProb(parseTag(kAttrs.getNamedItem("prob").getTextContent(), "prob").floatValue());
                }

                if (item.hasChildNodes()) {
                    NodeList kChilds = item.getChildNodes();
                    for (int i = 0; i < kChilds.getLength(); i++) {
                        tmp = kChilds.item(i);
                        
                        final boolean nodeNameIsNotNull = tmp.getNodeName() != null;
						final boolean nodeNameIsEnergy = tmp.getNodeName().equals("energy");
						if (nodeNameIsNotNull && nodeNameIsEnergy) {
                            profile.setEnergy(parseTag(tmp.getTextContent(), "energy").floatValue());
                        }
						
                        final boolean nodeNameIsExecTime = tmp.getNodeName().equals("execTime");
						if (nodeNameIsNotNull && nodeNameIsExecTime) {
                            profile.setExecTime(parseTag(tmp.getTextContent(), "execTime"));
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * Validates the input string and returns the proper float value from it
     * @param tagValue
     * @param tagName
     * @return the string related float value
     * @throws InvalidTagException
     */
    private static Float parseTag(String tagValue, String tagName) throws InvalidTagException {
        if ("".equals(tagValue)) {
            throw new InvalidTagException("Tag " + tagName + " is missing!", tagName);
        }
        Float parsedValue;
        try {
            parsedValue = Float.valueOf(tagValue);
        } catch (NumberFormatException e) {
            throw new InvalidTagException("Tag \"" + tagValue + "\" is not a float number!", tagName);
        }

        return parsedValue;
    }
}
