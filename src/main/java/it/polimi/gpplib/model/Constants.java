package it.polimi.gpplib.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Constants {
    public static final String PATH_MAIN_CPV = "cac:ProcurementProject/cac:MainCommodityClassification";
    public static final String PATH_ADDITIONAL_CPVS = "cac:ProcurementProject/cac:AdditionalCommodityClassification";
    // ND-Lot
    public static final String PATH_LOT = "cac:ProcurementProjectLot[cbc:ID/@schemeName='Lot']";
    // Q? ND or BT?
    public static final String PATH_IN_LOT_ID = "cbc:ID[@schemeName='Lot']";
    // ND-LotMainClassification
    public static final String PATH_IN_LOT_MAIN_CPV = "cac:ProcurementProject/cac:MainCommodityClassification";
    // ND-LotAdditionalClassification
    public static final String PATH_IN_LOT_ADDITIONAL_CPVS = "cac:ProcurementProject/cac:AdditionalCommodityClassification";

    public static final String AMBITION_LEVEL_CORE = "core";
    public static final String AMBITION_LEVEL_COMPREHENSIVE = "comprehensive";
    public static final String AMBITION_LEVEL_BOTH = "both";

    public static final String OP_CREATE = "create";
    public static final String OP_UPDATE = "update";

    // notice these are lowercase
    public static final String CRITERION_TYPE_AWARD_CRITERIA = "award criteria";
    public static final String CRITERION_TYPE_SELECTION_CRITERIA = "selection criteria";
    public static final String CRITERION_TYPE_CONTRACT_PERFORMANCE_CLAUSE = "contract performing clause";
    public static final String CRITERION_TYPE_TECHNICAL_SPECIFICATION = "technical specification";

    public static final String PATCH_NAME_GPP_CRITERIA_SOURCE = "Green Public Procurement Criteria";
    // TODO: rename this one to env impact in the sheet
    public static final String PATCH_NAME_ENVIRONMENTAL_IMPACT = "Green Procurement";
    // TODO: define better the expected description for this patch
    public static final String PATCH_NAME_STRATEGIC_PROCUREMENT = "Strategic Procurement: Reduction of environmental impacts";

    public static final String PATCH_NAME_AWARD_CRITERION = "Award Criterion";
    public static final String PATCH_NAME_SELECTION_CRITERIA = "Selection Criteria";
    public static final String PATCH_NAME_CONTRACT_PERFORMANCE_CLAUSE = "Contract Performance Clause";

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

    public static final Map<String, String> CRITERION_TYPE_TO_PATCH_NAME;
    static {
        Map<String, String> map = new HashMap<>();
        map.put(CRITERION_TYPE_AWARD_CRITERIA, PATCH_NAME_AWARD_CRITERION);
        map.put(CRITERION_TYPE_SELECTION_CRITERIA, PATCH_NAME_SELECTION_CRITERIA);
        map.put(CRITERION_TYPE_CONTRACT_PERFORMANCE_CLAUSE, PATCH_NAME_CONTRACT_PERFORMANCE_CLAUSE);
        // TODO: revise the logic of adding technical specifications as award criteria
        map.put(CRITERION_TYPE_TECHNICAL_SPECIFICATION, PATCH_NAME_AWARD_CRITERION);
        CRITERION_TYPE_TO_PATCH_NAME = Collections.unmodifiableMap(map);
    }

    private Constants() {
    } // Prevent instantiation
}
