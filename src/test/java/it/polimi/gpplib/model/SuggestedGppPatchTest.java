package it.polimi.gpplib.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SuggestedGppPatchTest {

    @Test
    public void testGettersAndSetters() {
        SuggestedGppPatch patch = new SuggestedGppPatch();
        patch.setName("Patch Name");
        patch.setBtIds(Arrays.asList("BT-1", "BT-2"));
        patch.setDependsOn("Depends Patch");
        patch.setPath("some/path");
        patch.setValue("<xml/>");
        patch.setOp("add");
        patch.setDescription("desc");
        patch.setLotId("LOT-1");

        assertEquals("Patch Name", patch.getName());
        assertEquals(Arrays.asList("BT-1", "BT-2"), patch.getBtIds());
        assertEquals("Depends Patch", patch.getDependsOn());
        assertEquals("some/path", patch.getPath());
        assertEquals("<xml/>", patch.getValue());
        assertEquals("add", patch.getOp());
        assertEquals("desc", patch.getDescription());
        assertEquals("LOT-1", patch.getLotId());
    }

    @Test
    public void testAllArgsConstructor() {
        List<String> btIds = Collections.singletonList("BT-805-Lot");
        SuggestedGppPatch patch = new SuggestedGppPatch(
                "Green Patch",
                btIds,
                "Parent Patch",
                "cac:ProcurementProject",
                "<cac:ProcurementAdditionalType></cac:ProcurementAdditionalType>",
                "replace",
                "A description",
                "LOT-2"
        );

        assertEquals("Green Patch", patch.getName());
        assertEquals(btIds, patch.getBtIds());
        assertEquals("Parent Patch", patch.getDependsOn());
        assertEquals("cac:ProcurementProject", patch.getPath());
        assertEquals("<cac:ProcurementAdditionalType></cac:ProcurementAdditionalType>", patch.getValue());
        assertEquals("replace", patch.getOp());
        assertEquals("A description", patch.getDescription());
        assertEquals("LOT-2", patch.getLotId());
    }

    @Test
    public void testToString() {
        SuggestedGppPatch patch = new SuggestedGppPatch(
                "Patch Name",
                Arrays.asList("BT-1", "BT-2"),
                "Depends Patch",
                "some/path",
                "<xml/>",
                "add",
                "desc",
                "LOT-1"
        );
        String str = patch.toString();
        assertTrue(str.contains("Patch Name"));
        assertTrue(str.contains("BT-1"));
        assertTrue(str.contains("Depends Patch"));
        assertTrue(str.contains("some/path"));
        assertTrue(str.contains("<xml/>"));
        assertTrue(str.contains("add"));
        assertTrue(str.contains("desc"));
        assertTrue(str.contains("LOT-1"));
    }

    @Test
    public void testEqualsAndHashCode() {
        List<String> btIds = Arrays.asList("BT-1", "BT-2");
        SuggestedGppPatch patch1 = new SuggestedGppPatch(
                "Patch Name", btIds, "Depends Patch", "some/path", "<xml/>", "add", "desc", "LOT-1"
        );
        SuggestedGppPatch patch2 = new SuggestedGppPatch(
                "Patch Name", btIds, "Depends Patch", "some/path", "<xml/>", "add", "desc", "LOT-1"
        );
        SuggestedGppPatch patch3 = new SuggestedGppPatch(
                "Other Name", btIds, "Depends Patch", "some/path", "<xml/>", "add", "desc", "LOT-1"
        );

        assertEquals(patch1, patch2);
        assertEquals(patch1.hashCode(), patch2.hashCode());
        assertNotEquals(patch1, patch3);
    }

    @Test
    public void testEquals_reflexive() {
        List<String> btIds = Arrays.asList("BT-1", "BT-2");
        SuggestedGppPatch patch = new SuggestedGppPatch(
                "Patch Name", btIds, "Depends Patch", "some/path", "<xml/>", "add", "desc", "LOT-1"
        );
        assertEquals(patch, patch);
    }

    @Test
    public void testEquals_null() {
        List<String> btIds = Arrays.asList("BT-1", "BT-2");
        SuggestedGppPatch patch = new SuggestedGppPatch(
                "Patch Name", btIds, "Depends Patch", "some/path", "<xml/>", "add", "desc", "LOT-1"
        );
        assertNotEquals(patch, null);
    }

    @Test
    public void testEquals_differentClass() {
        List<String> btIds = Arrays.asList("BT-1", "BT-2");
        SuggestedGppPatch patch = new SuggestedGppPatch(
                "Patch Name", btIds, "Depends Patch", "some/path", "<xml/>", "add", "desc", "LOT-1"
        );
        assertNotEquals(patch, "some string");
    }

    @Test
    public void testEquals_fieldNulls() {
        SuggestedGppPatch patch1 = new SuggestedGppPatch(
                null, null, null, null, null, null, null, null
        );
        SuggestedGppPatch patch2 = new SuggestedGppPatch(
                null, null, null, null, null, null, null, null
        );
        assertEquals(patch1, patch2);
        assertEquals(patch1.hashCode(), patch2.hashCode());

        SuggestedGppPatch patch3 = new SuggestedGppPatch(
                "Patch Name", null, null, null, null, null, null, null
        );
        assertNotEquals(patch1, patch3);
    }

    @Test
    public void testEquals_nameInequality() {
        SuggestedGppPatch a = new SuggestedGppPatch("A", null, null, null, null, null, null, null);
        SuggestedGppPatch b = new SuggestedGppPatch("B", null, null, null, null, null, null, null);
        assertNotEquals(a, b);

        SuggestedGppPatch c = new SuggestedGppPatch(null, null, null, null, null, null, null, null);
        SuggestedGppPatch d = new SuggestedGppPatch("B", null, null, null, null, null, null, null);
        assertNotEquals(c, d);
    }

    @Test
    public void testEquals_btIdsInequality() {
        SuggestedGppPatch a = new SuggestedGppPatch("N", Arrays.asList("A"), null, null, null, null, null, null);
        SuggestedGppPatch b = new SuggestedGppPatch("N", Arrays.asList("B"), null, null, null, null, null, null);
        assertNotEquals(a, b);

        SuggestedGppPatch c = new SuggestedGppPatch("N", null, null, null, null, null, null, null);
        SuggestedGppPatch d = new SuggestedGppPatch("N", Arrays.asList("B"), null, null, null, null, null, null);
        assertNotEquals(c, d);
    }

    @Test
    public void testEquals_dependsOnInequality() {
        SuggestedGppPatch a = new SuggestedGppPatch("N", null, "A", null, null, null, null, null);
        SuggestedGppPatch b = new SuggestedGppPatch("N", null, "B", null, null, null, null, null);
        assertNotEquals(a, b);

        SuggestedGppPatch c = new SuggestedGppPatch("N", null, null, null, null, null, null, null);
        SuggestedGppPatch d = new SuggestedGppPatch("N", null, "B", null, null, null, null, null);
        assertNotEquals(c, d);
    }

    @Test
    public void testEquals_pathInequality() {
        SuggestedGppPatch a = new SuggestedGppPatch("N", null, null, "A", null, null, null, null);
        SuggestedGppPatch b = new SuggestedGppPatch("N", null, null, "B", null, null, null, null);
        assertNotEquals(a, b);

        SuggestedGppPatch c = new SuggestedGppPatch("N", null, null, null, null, null, null, null);
        SuggestedGppPatch d = new SuggestedGppPatch("N", null, null, "B", null, null, null, null);
        assertNotEquals(c, d);
    }

    @Test
    public void testEquals_valueInequality() {
        SuggestedGppPatch a = new SuggestedGppPatch("N", null, null, null, "A", null, null, null);
        SuggestedGppPatch b = new SuggestedGppPatch("N", null, null, null, "B", null, null, null);
        assertNotEquals(a, b);

        SuggestedGppPatch c = new SuggestedGppPatch("N", null, null, null, null, null, null, null);
        SuggestedGppPatch d = new SuggestedGppPatch("N", null, null, null, "B", null, null, null);
        assertNotEquals(c, d);
    }

    @Test
    public void testEquals_opInequality() {
        SuggestedGppPatch a = new SuggestedGppPatch("N", null, null, null, null, "A", null, null);
        SuggestedGppPatch b = new SuggestedGppPatch("N", null, null, null, null, "B", null, null);
        assertNotEquals(a, b);

        SuggestedGppPatch c = new SuggestedGppPatch("N", null, null, null, null, null, null, null);
        SuggestedGppPatch d = new SuggestedGppPatch("N", null, null, null, null, "B", null, null);
        assertNotEquals(c, d);
    }

    @Test
    public void testEquals_descriptionInequality() {
        SuggestedGppPatch a = new SuggestedGppPatch("N", null, null, null, null, null, "A", null);
        SuggestedGppPatch b = new SuggestedGppPatch("N", null, null, null, null, null, "B", null);
        assertNotEquals(a, b);

        SuggestedGppPatch c = new SuggestedGppPatch("N", null, null, null, null, null, null, null);
        SuggestedGppPatch d = new SuggestedGppPatch("N", null, null, null, null, null, "B", null);
        assertNotEquals(c, d);
    }

    @Test
    public void testEquals_lotIdInequality() {
        SuggestedGppPatch a = new SuggestedGppPatch("N", null, null, null, null, null, null, "A");
        SuggestedGppPatch b = new SuggestedGppPatch("N", null, null, null, null, null, null, "B");
        assertNotEquals(a, b);

        SuggestedGppPatch c = new SuggestedGppPatch("N", null, null, null, null, null, null, null);
        SuggestedGppPatch d = new SuggestedGppPatch("N", null, null, null, null, null, null, "B");
        assertNotEquals(c, d);
    }
}
