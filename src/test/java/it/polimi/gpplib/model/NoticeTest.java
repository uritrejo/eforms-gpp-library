package it.polimi.gpplib.model;

import it.polimi.gpplib.utils.XmlUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import java.util.List;

import static org.junit.Assert.*;

public class NoticeTest {

    private Notice notice;

    @Before
    public void setUp() {
        String xml = XmlUtils.getAsXmlString("test_notices/test_notice.xml");
        notice = new Notice(xml);
    }

    @Test(expected = XmlUtils.XmlUtilsException.class)
    public void testConstructorInvalidXml() {
        notice = new Notice("potato");
    }

    @Test
    public void testGetProcurementProjectMainCpv() {
        String mainCpv = notice.getProcurementProjectMainCpv();
        assertEquals("24000000", mainCpv);
    }

    @Test
    public void testGetProcurementProjectAdditionalCpvs_empty() {
        List<String> additional = notice.getProcurementProjectAdditionalCpvs();
        assertNotNull(additional);
        assertTrue(additional.isEmpty());
    }

    @Test
    public void testGetAllProcurementProjectCpvs() {
        List<String> cpvs = notice.getAllProcurementProjectCpvs();
        assertEquals(List.of("24000000"), cpvs);
    }

    @Test
    public void testGetLotIds() {
        List<String> lotIds = notice.getLotIds();
        assertEquals(List.of("LOT-0001"), lotIds);
    }

    @Test
    public void testGetLotNode_existing() {
        Node lotNode = notice.getLotNode("LOT-0001");
        assertNotNull(lotNode);
        assertEquals("ProcurementProjectLot", lotNode.getLocalName());
    }

    @Test
    public void testGetLotNode_nonExisting() {
        Node lotNode = notice.getLotNode("DOES_NOT_EXIST");
        assertNull(lotNode);
    }

    @Test
    public void testDoesPathExistInLot_true() {
        boolean exists = notice.doesPathExistInLot("LOT-0001", "cbc:ID");
        assertTrue(exists);
    }

    @Test
    public void testDoesPathExistInLot_false() {
        boolean exists = notice.doesPathExistInLot("LOT-0001", "cbc:NonExisting");
        assertFalse(exists);
    }

    @Test
    public void testGetLotMainCpv() {
        String mainCpv = notice.getLotMainCpv("LOT-0001");
        assertEquals("24210000", mainCpv);
    }

    @Test
    public void testGetEFormsSdkVersion() {
        String eFormsSdkVersion = notice.getEFormsSdkVersion();
        assertEquals("eforms-sdk-1.14", eFormsSdkVersion);
    }

    @Test
    public void testGetLotAdditionalCpvs() {
        List<String> additional = notice.getLotAdditionalCpvs("LOT-0001");
        assertNotNull(additional);
        assertEquals(List.of("33192000", "33190000"), additional);
    }

    @Test
    public void testGetAllLotCpvs() {
        List<String> cpvs = notice.getAllLotCpvs("LOT-0001");
        assertEquals(List.of("24210000", "33192000", "33190000"), cpvs);
    }

    @Test
    public void testToXmlString() {
        String xml = notice.toXmlString();
        assertNotNull(xml);
        assertTrue(xml.contains("<ContractNotice"));
        assertTrue(xml.contains("<cac:ProcurementProject"));
        assertTrue(xml.contains("<cac:ProcurementProjectLot"));
    }

    @Test
    public void testToString() {
        String str = notice.toString();
        assertNotNull(str);
        assertTrue(str.contains("mainCpv='24000000'"));
        assertTrue(str.contains("lots=[{id='LOT-0001'"));
    }

    @Test
    public void testGetNoticeLanguage() {
        // The test_notice.xml doesn't contain a NoticeLanguageCode element
        String language = notice.getNoticeLanguage();
        assertEquals("ENG", language);
    }

    @Test
    public void testRandomStuff_me() {

        Notice notice = new Notice(XmlUtils.getAsXmlString("test_notices/german_furniture.xml"));

        Boolean hasStrategicProcurementNone = XmlUtils.doesNodeExistAtPath(notice.getLotNode("LOT-0001"),
                Constants.PATH_STRATEGIC_PROCUREMENT_NONE);
        assertTrue(hasStrategicProcurementNone);

        Boolean hasStrategicProcurementEnvImp = XmlUtils.doesNodeExistAtPath(notice.getLotNode("LOT-0001"),
                Constants.PATH_STRATEGIC_PROCUREMENT_ENV_IMP);
        assertFalse(hasStrategicProcurementEnvImp);

        Node none = XmlUtils.getNodeAtPath(notice.getLotNode("LOT-0001"), Constants.PATH_STRATEGIC_PROCUREMENT_NONE);
        String noneAsString = XmlUtils.docToString(none.getOwnerDocument());
        assertNotNull(noneAsString);
    }
}
