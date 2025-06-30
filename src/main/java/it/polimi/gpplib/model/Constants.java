package it.polimi.gpplib.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Constants {
    public static final String DOMAIN_KNOWLEDGE_GPP_CRITERIA_PATH = "domain_knowledge/gpp_criteria.json";
    public static final String DOMAIN_KNOWLEDGE_GPP_DOCS_PATH = "domain_knowledge/gpp_criteria_docs.json";
    public static final String DOMAIN_KNOWLEDGE_GPP_PATCHES_PATH = "domain_knowledge/gpp_patches_data.json";

    // eForms SDK paths
    public static final String EFORMS_SDK_UBL_COMMON_AGGREGATE_COMPONENTS_XSD_PATH_TEMPLATE = "eForms-SDK/v{version}/schemas/common/UBL-CommonAggregateComponents-2.3.xsd";
    public static final String EFORMS_SDK_DEFAULT_VERSION = "1.13";
    public static final String EFORMS_SDK_UBL_COMMON_AGGREGATE_COMPONENTS_XSD_PATH = "eForms-SDK/v1.13/schemas/common/UBL-CommonAggregateComponents-2.3.xsd";
    public static final String TYPE_NAME_PROCUREMENT_PROJECT = "ProcurementProjectType";

    public static final String PATH_PROCUREMENT_PROJECT = "cac:ProcurementProject";
    public static final String PATH_MAIN_CPV = "cac:ProcurementProject/cac:MainCommodityClassification";
    public static final String PATH_ADDITIONAL_CPVS = "cac:ProcurementProject/cac:AdditionalCommodityClassification";
    public static final String PATH_EFORMS_SDK_VERSION = "cbc:CustomizationID";

    public static final String PATH_STRATEGIC_PROCUREMENT_NONE = "cac:ProcurementProject/cac:ProcurementAdditionalType[cbc:ProcurementTypeCode/@listName='strategic-procurement' and cbc:ProcurementTypeCode='none']";
    public static final String PATH_STRATEGIC_PROCUREMENT_ENV_IMP = "cac:ProcurementProject/cac:ProcurementAdditionalType[cbc:ProcurementTypeCode/@listName='strategic-procurement' and cbc:ProcurementTypeCode='env-imp']";

    // ??++
    public static final String PATH_AWARD_CRITERION_NODE_TEMPLATE = "cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion[cbc:Name='{arg0}']";

    public static final String PATH_AWARD_CRITERION_TYPE = "cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion/cbc:AwardingCriterionTypeCode[@listName='award-criterion-type']";
    public static final String PATH_AWARD_CRITERION_NAME = "cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion/cbc:Name";
    public static final String PATH_AWARD_CRITERION_DESCRIPTION = "cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion/cbc:Description";
    public static final String PATH_AWARD_CRITERION_WEIGHT_CODE = "/*/cac:ProcurementProjectLot[cbc:ID/@schemeName='Lot']/cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion/ext:UBLExtensions/ext:UBLExtension/ext:ExtensionContent/efext:EformsExtension/efac:AwardCriterionParameter[efbc:ParameterCode/@listName='number-weight']/efbc:ParameterCode";
    public static final String PATH_AWARD_CRITERION_WEIGHT = "/*/cac:ProcurementProjectLot[cbc:ID/@schemeName='Lot']/cac:TenderingTerms/cac:AwardingTerms/cac:AwardingCriterion/cac:SubordinateAwardingCriterion/ext:UBLExtensions/ext:UBLExtension/ext:ExtensionContent/efext:EformsExtension/efac:AwardCriterionParameter[efbc:ParameterCode/@listName='number-weight']/efbc:ParameterNumeric";

    public static final String AWARD_CRITERION_WEIGHT_CODE_IDENTIFIER = "number-weight";
    public static final String AWARD_CRITERION_WEIGHT_CODE = "per-exa";

    // ND-Lot
    public static final String PATH_LOT = "cac:ProcurementProjectLot[cbc:ID/@schemeName='Lot']";
    // Q? ND or BT?
    public static final String PATH_IN_LOT_ID = "cbc:ID[@schemeName='Lot']";
    // ND-LotMainClassification
    public static final String PATH_IN_LOT_MAIN_CPV = "cac:ProcurementProject/cac:MainCommodityClassification";
    // ND-LotAdditionalClassification
    public static final String PATH_IN_LOT_ADDITIONAL_CPVS = "cac:ProcurementProject/cac:AdditionalCommodityClassification";

    // BT-702
    public static final String PATH_NOTICE_LANGUAGE = "cbc:NoticeLanguageCode";

    public static final String AMBITION_LEVEL_CORE = "core";
    public static final String AMBITION_LEVEL_COMPREHENSIVE = "comprehensive";
    public static final String AMBITION_LEVEL_BOTH = "both";

    public static final String OP_CREATE = "create";
    public static final String OP_UPDATE = "update";
    public static final String OP_REMOVE = "remove";

    // notice these are lowercase
    public static final String CRITERION_TYPE_AWARD_CRITERIA = "award criteria";
    public static final String CRITERION_TYPE_SELECTION_CRITERIA = "selection criteria";
    public static final String CRITERION_TYPE_CONTRACT_PERFORMANCE_CLAUSE = "contract performing clause";
    public static final String CRITERION_TYPE_TECHNICAL_SPECIFICATION = "technical specification";

    public static final String PATCH_NAME_GPP_CRITERIA_SOURCE = "Green Public Procurement Criteria";
    // TODO: rename this one to env impact in the sheet
    public static final String PATCH_NAME_ENVIRONMENTAL_IMPACT = "Green Procurement";
    // TODO: define better the expected description for this patch
    public static final String PATCH_NAME_STRATEGIC_PROCUREMENT_ENV_IMP = "Strategic Procurement: Reduction of environmental impacts";
    public static final String PATCH_NAME_STRATEGIC_PROCUREMENT_NONE = "Strategic Procurement: None";

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

    public static final String PLACEHOLDER_WEIGHT = "{dynamic-weight}";

    public static final String AWARD_CRITERIA_TYPE_QUALITY = "quality";

    public static final String TENDERER_REQ_CODE_ENV_MANAGEMENT = "slc-abil-mgmt-env";
    public static final String TENDERER_REQ_CODE_ENV_CERTIFICATE = "slc-sche-env-cert-indep";

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
