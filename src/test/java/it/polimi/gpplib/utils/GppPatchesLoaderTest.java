package it.polimi.gpplib.utils;

import it.polimi.gpplib.model.GppPatch;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GppPatchesLoaderTest {

    @Test
    public void testLoadGppPatches_withTestFile() throws Exception {
        String testFilePath = "domain_knowledge/test_gpp_patches_data.json";
        GppPatchesLoader loader = new GppPatchesLoader(testFilePath);
        List<GppPatch> patches = loader.loadGppPatches();

        assertNotNull(patches);
        assertEquals(2, patches.size());

        GppPatch patch1 = patches.get(0);
        assertEquals("Patch1", patch1.getName());
        assertEquals(List.of("BT-1"), patch1.getBtIds());
        assertNull(patch1.getDependsOn());
        assertEquals("path1", patch1.getPathInLot());
        assertEquals("<xml>value1</xml>", patch1.getValue());

        GppPatch patch2 = patches.get(1);
        assertEquals("Patch2", patch2.getName());
        assertEquals(List.of("BT-2", "BT-3"), patch2.getBtIds());
        assertEquals("Patch1", patch2.getDependsOn());
        assertEquals("path2", patch2.getPathInLot());
        assertEquals("<xml>value2</xml>", patch2.getValue());
    }
}
