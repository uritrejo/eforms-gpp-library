# Domain Knowledge Management

## Overview

This component bridges GPP (Green Public Procurement) expertise with the technical system by converting structured domain knowledge into machine-readable formats. The domain knowledge captures GPP criteria, documents, and eForm field mappings required for automated GPP compliance integration.

## Domain Knowledge Structure

The domain knowledge consists of three main components, managed through an Excel file (`sources/domain_knowledge.xlsx`) with corresponding sheets:

### 1. GPP Criteria Documents (`GPP Criteria Docs` → `gpp_criteria_docs.json`)

Contains metadata about GPP criteria source documents:

-   **Document Name**: Official document name (e.g., "EU GPP Criteria for Furniture")
-   **Source**: Origin URL for the GPP document collection
-   **Document Reference**: Direct URL to access the document
-   **Publication Date**: Version control through publication date (YYYY-MM-DD format)

### 2. GPP Criteria (`GPP Criteria ENG` → `gpp_criteria.json`)

Individual GPP criteria extracted from the documents:

-   **GPP Document**: Source document reference
-   **GPP Source**: Maps to BT-805 field (`eu`, `national`, `other`, `none`)
-   **Type**: `Award criteria`, `Selection criteria`, `Contract performance clauses`, `Technical specification`
-   **Ambition Level**: `core`, `comprehensive`, or `both`
-   **ID & Name**: Criterion identifier and title from source document
-   **Applicable CPV Codes**: Relevant procurement categories (requires domain expertise)
-   **Environmental Impact Type**: Maps to BT-774 field (`biodiv-eco`, `cir-econ`, `clim-adapt`, `clim-mitig`, `pollu-prev`, `water-mar`, `other`)
-   **Description**: Criterion description for implementation

### 3. eForm Field Mappings (`Patches` → `gpp_patches_data.json`)

Technical mappings for inserting GPP data into eForm notices:

-   **Name**: eForm element name from eForms SDK
-   **BT IDs**: Business Terms covered (reference only)
-   **Depends On**: Parent structure requirements
-   **Path Relative to Lot**: XPath location within notice structure
-   **Value**: XML template with parameter placeholders

## 🚀 Quick Setup

### Prerequisites

-   Python 3.8+ installed
-   Git repository cloned

### First-time Setup

```bash
# Navigate to scripts directory
cd domain_knowledge/scripts

# Create virtual environment
python3 -m venv venv

# Activate virtual environment
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

### Usage

```bash
# Always activate before working
cd domain_knowledge/scripts
source venv/bin/activate

# Generate JSON files from Excel source
python json_generator.py

# Deactivate when done
deactivate
```

## 🔄 Workflow

### For Domain Experts

1. Edit the Excel file (`sources/domain_knowledge.xlsx`)
2. Run the generation script to create JSON files
3. Review generated files in `src/main/resources/domain_knowledge/`
4. Test integration with the library
5. Commit both Excel source and generated JSON files

### For Developers

The generated JSON files are consumed directly by the Java GPP library from the resources directory. No manual intervention required unless updating the generation script.

## Notes

### Expertise Requirements

-   **GPP Knowledge**: Required for criteria extraction, CPV code mapping, and environmental impact classification
-   **eForms Knowledge**: Required for field mapping maintenance (should remain stable across minor SDK versions)

### Maintenance Considerations

-   **Manual Process**: Domain knowledge extraction and mapping requires manual expert input
-   **Version Sensitivity**: GPP documents update every few years; eForms mappings tied to SDK major versions
-   **Language Limitation**: Current implementation focuses on English content
