package it.polimi.gpplib.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GppPatchTest {

    @Test
    public void testGettersAndSetters() {
        GppPatch patch = new GppPatch();
        patch.setName("Test Patch");
        patch.setBtIds(Arrays.asList("BT-1", "BT-2"));
        patch.setDependsOn("Other Patch");
        patch.setPathInLot("cac:ProcurementProject");
        patch.setValue("<xml>value</xml>");

        assertEquals("Test Patch", patch.getName());
        assertEquals(Arrays.asList("BT-1", "BT-2"), patch.getBtIds());
        assertEquals("Other Patch", patch.getDependsOn());
        assertEquals("cac:ProcurementProject", patch.getPathInLot());
        assertEquals("<xml>value</xml>", patch.getValue());
    }

    @Test
    public void testAllArgsConstructor() {
        List<String> btIds = Collections.singletonList("BT-805-Lot");
        GppPatch patch = new GppPatch(
                "Green Public Procurement Criteria",
                btIds,
                "Procurement Project",
                "cac:ProcurementProject",
                "<cac:ProcurementAdditionalType></cac:ProcurementAdditionalType>");

        assertEquals("Green Public Procurement Criteria", patch.getName());
        assertEquals(btIds, patch.getBtIds());
        assertEquals("Procurement Project", patch.getDependsOn());
        assertEquals("cac:ProcurementProject", patch.getPathInLot());
        assertEquals("<cac:ProcurementAdditionalType></cac:ProcurementAdditionalType>", patch.getValue());
    }

    @Test
    public void testToString() {
        GppPatch patch = new GppPatch(
                "Patch Name",
                Arrays.asList("BT-1", "BT-2"),
                "Depends Patch",
                "some/path",
                "<xml/>");
        String str = patch.toString();
        assertTrue(str.contains("Patch Name"));
        assertTrue(str.contains("BT-1"));
        assertTrue(str.contains("Depends Patch"));
        assertTrue(str.contains("some/path"));
        assertTrue(str.contains("<xml/>"));
    }
}
