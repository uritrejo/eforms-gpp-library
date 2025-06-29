package it.polimi.gpplib.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import org.w3c.dom.Document;

/**
 * Test class for EFormsSdkWrapper functionality.
 */
public class EFormsSdkWrapperTest {

    @Test
    public void testGetProcurementProjectTypeSequenceRefs() {
        // Test that we can successfully read the ProcurementProjectType sequence refs
        EFormsSdkWrapper wrapper = new EFormsSdkWrapper();
        List<String> refElements = wrapper.getProcurementProjectTypeSchema();

        // Verify that we got some results
        assertNotNull("Ref elements list should not be null", refElements);
        assertFalse("Ref elements list should not be empty", refElements.isEmpty());

        // Verify some expected elements based on the XSD content we saw
        assertTrue("Should contain ext:UBLExtensions",
                refElements.contains("ext:UBLExtensions"));
        assertTrue("Should contain cbc:ID",
                refElements.contains("cbc:ID"));
        assertTrue("Should contain cbc:Name",
                refElements.contains("cbc:Name"));
        assertTrue("Should contain cbc:Description",
                refElements.contains("cbc:Description"));
        assertTrue("Should contain cac:ProcurementAdditionalType",
                refElements.contains("cac:ProcurementAdditionalType"));
        assertTrue("Should contain cac:MainCommodityClassification",
                refElements.contains("cac:MainCommodityClassification"));

        // Print the results for verification
        System.out.println("ProcurementProjectType sequence ref elements:");
        for (int i = 0; i < refElements.size(); i++) {
            System.out.println((i + 1) + ". " + refElements.get(i));
        }
    }

    @Test
    public void testRefElementsCount() {
        // Test that we get the expected number of elements
        EFormsSdkWrapper wrapper = new EFormsSdkWrapper();
        List<String> refElements = wrapper.getProcurementProjectTypeSchema();

        // Based on the XSD content we examined, there should be around 22 elements
        assertTrue("Should have at least 20 ref elements, got: " + refElements.size(),
                refElements.size() >= 20);
        assertTrue("Should have at most 25 ref elements, got: " + refElements.size(),
                refElements.size() <= 25);
    }

    @Test
    public void testNonExistentComplexType() {
        // Test with a complex type that doesn't exist in the XSD
        try {
            Document xsdDocument = XmlUtils.loadDocumentFromResource(
                    "eForms-SDK/v1.13/schemas/common/UBL-CommonAggregateComponents-2.3.xsd");
            List<String> nonExistentRefs = XmlUtils.extractSequenceRefElements(
                    xsdDocument, "NonExistentComplexType");

            // Should return empty list for non-existent complex type
            assertNotNull("Should return empty list for non-existent type", nonExistentRefs);
            assertTrue("Should be empty for non-existent type", nonExistentRefs.isEmpty());
        } catch (Exception e) {
            fail("Should not throw exception for non-existent complex type: " + e.getMessage());
        }
    }

    @Test
    public void testDefaultConstructor() {
        // Test the default constructor that uses Constants path
        EFormsSdkWrapper wrapper = new EFormsSdkWrapper();
        List<String> refElements = wrapper.getProcurementProjectTypeSchema();

        assertNotNull("Ref elements should not be null", refElements);
        assertFalse("Ref elements should not be empty", refElements.isEmpty());
        assertEquals("Should have exactly 21 elements", 21, refElements.size());
    }

    @Test
    public void testCustomPathConstructor() {
        // Test the constructor with custom path
        EFormsSdkWrapper wrapper = new EFormsSdkWrapper(
                "eForms-SDK/v1.13/schemas/common/UBL-CommonAggregateComponents-2.3.xsd");
        List<String> refElements = wrapper.getProcurementProjectTypeSchema();

        assertNotNull("Ref elements should not be null", refElements);
        assertFalse("Ref elements should not be empty", refElements.isEmpty());
        assertEquals("Should have exactly 21 elements", 21, refElements.size());
    }

    @Test
    public void testMultipleInstancesReturnSameData() {
        // Test that multiple instances return the same data
        EFormsSdkWrapper wrapper1 = new EFormsSdkWrapper();
        EFormsSdkWrapper wrapper2 = new EFormsSdkWrapper();

        List<String> refElements1 = wrapper1.getProcurementProjectTypeSchema();
        List<String> refElements2 = wrapper2.getProcurementProjectTypeSchema();

        assertEquals("Both instances should return same number of elements",
                refElements1.size(), refElements2.size());
        assertTrue("Both instances should contain same elements",
                refElements1.containsAll(refElements2) && refElements2.containsAll(refElements1));
    }
}
