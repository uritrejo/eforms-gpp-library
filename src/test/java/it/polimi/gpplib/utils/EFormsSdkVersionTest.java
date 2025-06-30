package it.polimi.gpplib.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import it.polimi.gpplib.model.Constants;

/**
 * Test class for verifying the version template functionality.
 */
public class EFormsSdkVersionTest {

    @Test
    public void testVersionTemplateReplacement() {
        // Test that the template correctly replaces version placeholders
        String template = Constants.EFORMS_SDK_UBL_COMMON_AGGREGATE_COMPONENTS_XSD_PATH_TEMPLATE;

        // Verify the template has the placeholder
        assertTrue("Template should contain version placeholder",
                template.contains("{version}"));

        // Test various version replacements
        String version113 = template.replace("{version}", "1.13");
        String version112 = template.replace("{version}", "1.12");
        String version200 = template.replace("{version}", "2.0.0");

        assertEquals("Version 1.13 path should be correct",
                "eForms-SDK/v1.13/schemas/common/UBL-CommonAggregateComponents-2.3.xsd",
                version113);

        assertEquals("Version 1.12 path should be correct",
                "eForms-SDK/v1.12/schemas/common/UBL-CommonAggregateComponents-2.3.xsd",
                version112);

        assertEquals("Version 2.0.0 path should be correct",
                "eForms-SDK/v2.0.0/schemas/common/UBL-CommonAggregateComponents-2.3.xsd",
                version200);

        // Verify template is unchanged after replacements
        assertTrue("Original template should still have placeholder",
                template.contains("{version}"));
    }

    @Test
    public void testDefaultVersionConstant() {
        // Test that the default version constant exists and is correct
        assertEquals("Default version should be 1.13",
                "1.13", Constants.EFORMS_SDK_DEFAULT_VERSION);

        // Test that default version creates the same path as the static constant
        String templatePath = Constants.EFORMS_SDK_UBL_COMMON_AGGREGATE_COMPONENTS_XSD_PATH_TEMPLATE
                .replace("{version}", Constants.EFORMS_SDK_DEFAULT_VERSION);

        assertEquals("Template with default version should equal static path",
                Constants.EFORMS_SDK_UBL_COMMON_AGGREGATE_COMPONENTS_XSD_PATH,
                templatePath);
    }

    @Test
    public void testVersionConstructorPathGeneration() {
        // This test verifies the behavior through the public interface
        try {
            // Create wrappers with different versions (same version, different
            // constructors)
            EFormsSdkWrapper defaultWrapper = new EFormsSdkWrapper();
            EFormsSdkWrapper versionWrapper = new EFormsSdkWrapper("1.13");
            EFormsSdkWrapper customWrapper = new EFormsSdkWrapper(
                    "eForms-SDK/v1.13/schemas/common/UBL-CommonAggregateComponents-2.3.xsd", true);

            // All should load the same data for version 1.13
            assertEquals("All constructors should load same number of elements",
                    defaultWrapper.getProcurementProjectTypeSchema().size(),
                    versionWrapper.getProcurementProjectTypeSchema().size());

            assertEquals("Version and custom path should load same number of elements",
                    versionWrapper.getProcurementProjectTypeSchema().size(),
                    customWrapper.getProcurementProjectTypeSchema().size());

        } catch (Exception e) {
            fail("Should not throw exception for valid version: " + e.getMessage());
        }
    }
}
