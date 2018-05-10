package dmitrigeller;


import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Unit test for XmlAnalyzerMain
 */
public class XmlAnalyzerMainTest
{

    XmlAnalyzerMain analyzer = new XmlAnalyzerMain();

    @Test
    public void testApp() throws IOException {

        testFindingSimilar(
                "src/test/resources/sample-0-origin.html",
                "src/test/resources/sample-1-evil-gemini.html",
                XmlAnalyzerMain.DEFAULT_TARGET_ELEMENT_ID,
                "<a class=\"btn btn-success\" href=\"#check-and-ok\" title=\"Make-Button\" rel=\"done\" onclick=\"javascript:window.okDone(); return false;\"> Make everything OK </a>"
        );

        testFindingSimilar(
                "src/test/resources/sample-0-origin.html",
                "src/test/resources/sample-2-container-and-clone.html",
                XmlAnalyzerMain.DEFAULT_TARGET_ELEMENT_ID,
                "<a class=\"btn test-link-ok\" href=\"#ok\" title=\"Make-Button\" rel=\"next\" onclick=\"javascript:window.okComplete(); return false;\"> Make everything OK </a>"
        );

        testFindingSimilar(
                "src/test/resources/sample-0-origin.html",
                "src/test/resources/sample-3-the-escape.html",
                XmlAnalyzerMain.DEFAULT_TARGET_ELEMENT_ID,
                "<a class=\"btn btn-success\" href=\"#ok\" title=\"Do-Link\" rel=\"next\" onclick=\"javascript:window.okDone(); return false;\"> Do anything perfect </a>"
        );

        testFindingSimilar(
                "src/test/resources/sample-0-origin.html",
                "src/test/resources/sample-4-the-mash.html",
                XmlAnalyzerMain.DEFAULT_TARGET_ELEMENT_ID,
                "<a class=\"btn btn-success\" href=\"#ok\" title=\"Make-Button\" rel=\"next\" onclick=\"javascript:window.okFinalize(); return false;\"> Do all GREAT </a>"
        );
    }

    private void testFindingSimilar(
            String originalFile,
            String sampleFile,
            String orginalElementId,
            String sampleElement
    )
    throws IOException
    {
        Element similarElement = analyzer.findSimilarElementInFiles(
                originalFile,
                sampleFile,
                orginalElementId
        );
        assertEquals(sampleElement, similarElement.toString());
    }
}
