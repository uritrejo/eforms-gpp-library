package it.polimi.gpplib.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Constants {
    public static final String AMBITION_LEVEL_CORE = "core";
    public static final String AMBITION_LEVEL_COMPREHENSIVE = "comprehensive";
    public static final String AMBITION_LEVEL_BOTH = "both";

    public static final String OP_CREATE = "create";
    public static final String OP_UPDATE = "update";

    public static final String PATCH_NAME_GPP_CRITERIA_SOURCE = "Green Public Procurement Criteria";
    // TODO: rename this one to env impact in the sheet
    public static final String PATCH_NAME_ENVIRONMENTAL_IMPACT = "Green Procurement";
    // TODO: define better the expected description for this patch
    public static final String PATCH_NAME_STRATEGIC_PROCUREMENT = "Strategic Procurement: Reduction of environmental impacts";

    public static final String PATCH_DESCRIPTION_STRATEGIC_PROCUREMENT = "GPP criteria will be used to evaluate the proposals, accounting for environmental impact";

    public static final String TAG_ARG0 = "arg0";
    public static final String TAG_ARG1 = "arg1";
    public static final String TAG_ARG2 = "arg2";
    public static final String TAG_ARG3 = "arg3";
    public static final String TAG_ARG4 = "arg4";
    public static final String TAG_ARG5 = "arg5";
    public static final String TAG_ARG6 = "arg6";

    public static final String TAG_LANGUAGE = "language";
    public static final String TAG_ENGLISH = "EN";

    public static final Map<String, String> NAMESPACE_MAP;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("default", "urn:oasis:names:specification:ubl:schema:xsd:ContractNotice-2");
        map.put("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        map.put("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
        map.put("efac", "http://data.europa.eu/p27/eforms-ubl-extension-aggregate-components/1");
        map.put("efbc", "http://data.europa.eu/p27/eforms-ubl-extension-basic-components/1");
        map.put("efext", "http://data.europa.eu/p27/eforms-ubl-extensions/1");
        map.put("ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        map.put("xsd", "http://www.w3.org/2001/XMLSchema");
        map.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        NAMESPACE_MAP = Collections.unmodifiableMap(map);
    }

    private Constants() {
    } // Prevent instantiation
}
