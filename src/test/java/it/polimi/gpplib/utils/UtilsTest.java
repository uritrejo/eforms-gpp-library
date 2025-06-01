package it.polimi.gpplib.utils;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testMatchingCpvs_exactMatch() {
        List<String> noticeCpvs = List.of("12345678", "87654321");
        List<String> criteriaCpvs = List.of("12345678");
        List<String> result = Utils.matchingCpvs(noticeCpvs, criteriaCpvs);
        assertEquals(List.of("12345678"), result);
    }

    @Test
    public void testMatchingCpvs_childMatch() {
        List<String> noticeCpvs = List.of("12340000", "56780000");
        List<String> criteriaCpvs = List.of("1234");
        List<String> result = Utils.matchingCpvs(noticeCpvs, criteriaCpvs);
        assertEquals(List.of("12340000"), result);
    }

    @Test
    public void testMatchingCpvs_childMatch1() {
        List<String> noticeCpvs = List.of("12340000", "39144444", "45233293");
        List<String> criteriaCpvs = List.of("39100000", "45233293");
        List<String> result = Utils.matchingCpvs(noticeCpvs, criteriaCpvs);
        assertEquals(List.of("39144444", "45233293"), result);
    }

    @Test
    public void testMatchingCpvs_noMatch() {
        List<String> noticeCpvs = List.of("11111111");
        List<String> criteriaCpvs = List.of("2222");
        List<String> result = Utils.matchingCpvs(noticeCpvs, criteriaCpvs);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testHasMatchingCpvs_true() {
        List<String> noticeCpvs = List.of("12345678");
        List<String> criteriaCpvs = List.of("1234");
        assertTrue(Utils.hasMatchingCpvs(noticeCpvs, criteriaCpvs));
    }

    @Test
    public void testHasMatchingCpvs_false() {
        List<String> noticeCpvs = List.of("11111111");
        List<String> criteriaCpvs = List.of("2222");
        assertFalse(Utils.hasMatchingCpvs(noticeCpvs, criteriaCpvs));
    }

    @Test
    public void testLoadXmlString_notice() {
        String resourcePath = "notices_furniture/00152724_2025.xml";
        String xml = Utils.loadXmlString(resourcePath);
        System.out.println(xml);
        assertNotNull("XML string should not be null", xml);
        assertTrue("XML string should contain ContractNotice root element", xml.contains("<ContractNotice"));
    }
}
