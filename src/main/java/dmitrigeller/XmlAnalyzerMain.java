package dmitrigeller;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The solution for the "Smart XML Analyzer" test task
 *
 * @see "https://agileengine.bitbucket.io/keFivpUlPMtzhfAy/"
 */
public class XmlAnalyzerMain {

    private static final String CHARSET_NAME = "utf8";
    static final String DEFAULT_TARGET_ELEMENT_ID = "make-everything-ok-button";

    // the are many algorithms computing string distance (or similarity):
    // see https://github.com/tdebatty/java-string-similarity
    //
    private NormalizedStringDistance algorithm = new NormalizedLevenshtein();

    public static void main(String[] args) throws IOException {

        String originFilePath;
        String sampleFilePath;
        String originalElementId = DEFAULT_TARGET_ELEMENT_ID;

        switch (args.length) {
            case 2:
                originFilePath = args[0];
                sampleFilePath = args[1];
                break;
            case 3:
                originFilePath = args[0];
                sampleFilePath = args[1];
                originalElementId = args[2];
                break;
            default:
                throw new IllegalArgumentException("Provide either TWO or THREE command-line arguments!");
        }
        System.out.println("\nOriginal file: " + originFilePath);
        System.out.println("Sample file: " + sampleFilePath);
        System.out.println("Original HTML element ID (in origin file): " + originalElementId);

        XmlAnalyzerMain analyzer = new XmlAnalyzerMain();

        Element similarElement = analyzer.findSimilarElementInFiles(
                originFilePath,
                sampleFilePath,
                originalElementId
        );

        System.out.println("\nSIMILAR HTML ELEMENT (from " + sampleFilePath + "):");
        printElement(similarElement);
    }

    /**
     * @return the HTML element (one from the "sampleFilePath" HTML file) which string representation has minimal
     * Levenstein distance to the HTML element with "originElementId" in "originFilePath". can be null
     */
    Element findSimilarElementInFiles(String originFilePath, String sampleFilePath, String originElementId)
            throws IOException {
        Element originalElement = findElementById(new File(originFilePath), originElementId);
        if (originalElement == null)
            throw new IllegalStateException(
                    "Can not find element with id=" + originElementId + " in " + originFilePath);

        System.out.println("\nORIGINAL HTML ELEMENT (from " + originFilePath + " ):");
        printElement(originalElement);

        Tag originTag = originalElement.tag();
        List<Element> sameTagElements = getElementsSameTag(new File(sampleFilePath), originTag);

        String originAllAttrs = getAttributesAsOneString(originalElement);
        return findSimilarElement(originAllAttrs, sameTagElements);
    }

    private static void printElement(Element element) {
        if (element == null) {
            System.out.println("THE ELEMENT: null");
        } else {
            System.out.println("PATH TO THE ELEMENT: " + getPathToElement(element));
            System.out.println("THE ELEMENT: " + element);
        }
    }

    /**
     * @return the HTML element (one from the "toCompare") which string representation has minimal
     * distance to the "etalon" string; null if nothing found
     */
    private Element findSimilarElement(String etalon, List<Element> toCompare) {

        double minDistance = 1.0F;
        Element closestElement = null;

        for (Element compareElement : toCompare) {
            double compareDistance = algorithm.distance(
                    etalon,
                    getAttributesAsOneString(compareElement)
            );
            if (compareDistance < minDistance) {
                minDistance = compareDistance;
                closestElement = compareElement;
            }
        }

        System.out.println("\nThe minimal (normalized Levenshtein) distance between the original and similar elements: " + minDistance);
        return closestElement;
    }

    /**
     * @return list of HTML elements having the same tag as the "originTag"; never null
     */
    private static List<Element> getElementsSameTag(File htmlFile, Tag originTag) throws IOException {
        Document doc = Jsoup.parse(
                htmlFile,
                CHARSET_NAME,
                htmlFile.getAbsolutePath());

        Elements elementsOriginTag = doc.select(originTag.getName());
        List<Element> result = new ArrayList<>();
        for (Element curElement : elementsOriginTag) {
            result.add(curElement);
        }
        return result;
    }

    /**
     * @return the human-readable "path" to the given "currentElement". never null
     */
    private static String getPathToElement(Element currentElement) {
        StringBuilder result = new StringBuilder();
        for (int i = currentElement.parents().size() - 1; i >= 0; i--) {
            Element parent = currentElement.parents().get(i);
            appendOneElementToPath(result, parent);
            result.append(" >");
        }
        appendOneElementToPath(result, currentElement);
        return result.toString();
    }

    /**
     * Helper method, used in "getPathToElement"
     */
    private static void appendOneElementToPath(StringBuilder result, Element element) {
        result.append(" ");
        result.append(element.tag());
        if (element.id() != null && element.id().length() > 0) {
            result.append("#");
            result.append(element.id());
        }
        if (element.tag().getName().equals("a")) {
            String href = element.attr("href");
            if (href != null) {
                result.append(" href=\"");
                result.append(href);
                result.append("\"");
            }
        }
    }

    /**
     * @return the string representation of HTML element; includes all the attributes and child
     * elements. never null
     */
    private static String getAttributesAsOneString(Element element) {
        return element.attributes().asList().stream()
                .map(attr -> attr.getKey() + " = " + attr.getValue())
                .collect(Collectors.joining(", "));
    }

    /**
     * @return return the HTML element from "htmlFile" identified by "targetElementId" or null
     */
    private static Element findElementById(File htmlFile, String targetElementId) throws IOException {
        Document doc = Jsoup.parse(
                htmlFile,
                CHARSET_NAME,
                htmlFile.getAbsolutePath());

        return doc.getElementById(targetElementId);
    }

}
