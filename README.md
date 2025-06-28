# eForms GPP Library

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](#license)

A comprehensive Java library for analyzing and enhancing public procurement notices with **Green Public Procurement (GPP)** criteria and environmental sustainability recommendations.

## 🌱 What is Green Public Procurement?

Green Public Procurement (GPP) is a process whereby public authorities seek to procure goods, services, and works with a reduced environmental impact throughout their life cycle. This library helps transform standard procurement notices into environmentally conscious ones by:

-   Analyzing procurement content using **CPV codes** (Common Procurement Vocabulary)
-   Identifying relevant **EU GPP criteria** and environmental requirements
-   Suggesting concrete improvements and patches
-   Automatically applying environmental enhancements to notices

## ✨ Key Features

-   📄 **eForms Standard Support** - Load and parse procurement notices from XML (eForms standard)
-   🔍 **Smart Analysis** - Analyze notices to identify relevant GPP criteria based on CPV codes and procurement categories
-   🌿 **Environmental Enhancement** - Suggest improvements and patches for better environmental sustainability
-   🔧 **Automated Patching** - Apply patches to create enhanced notice versions with GPP compliance
-   📚 **Comprehensive Knowledge Base** - Domain knowledge management for GPP documents, criteria, and patches
-   🎯 **Ambition Levels** - Support for Core and Comprehensive GPP criteria levels
-   🏗️ **Lot-based Analysis** - Per-lot analysis for complex multi-lot procurements

## 🚀 Quick Start

### Prerequisites

-   Java 21 or higher
-   Maven 3.6 or higher

### Installation

Add the library to your Maven project:

```xml
<dependency>
    <groupId>it.polimi.gpplib</groupId>
    <artifactId>eforms-gpp-library</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import it.polimi.gpplib.*;
import it.polimi.gpplib.model.*;

// Create analyzer with default configuration
GppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer();

// Load a procurement notice from XML
String xmlString = Files.readString(Paths.get("notice.xml"));
Notice notice = analyzer.loadNotice(xmlString);

// Analyze the notice
GppAnalysisResult result = analyzer.analyzeNotice(notice);
System.out.println("Found " + result.getRelevantGppDocuments().size() + " relevant GPP documents");
System.out.println("Suggested " + result.getSuggestedGppCriteria().size() + " GPP criteria");

// Get specific patches to improve environmental compliance
List<SuggestedGppPatch> patches = analyzer.suggestPatches(notice, result.getSuggestedCriteria());
System.out.println("Generated " + patches.size() + " improvement patches");

// Apply patches to create an enhanced, environmentally-conscious notice
Notice enhancedNotice = analyzer.applyPatches(notice, patches);

// Export the improved notice
String enhancedXml = enhancedNotice.toXmlString();
Files.writeString(Paths.get("enhanced_notice.xml"), enhancedXml);
```

### Advanced Configuration

```java
// Use custom domain knowledge files
GppNoticeAnalyzer customAnalyzer = new DefaultGppNoticeAnalyzer(
    "path/to/gpp_documents.json",
    "path/to/gpp_criteria.json",
    "path/to/gpp_patches.json"
);
```

## 📊 How It Works

### 1. Notice Analysis

The library examines procurement notices and extracts:

-   **CPV codes** (Common Procurement Vocabulary) for categorization
-   **Lot structure** for multi-lot procurements
-   **Procurement project details**

### 2. GPP Matching

Based on CPV codes, the system:

-   Identifies relevant **EU GPP documents** (e.g., "EU GPP Criteria for Furniture")
-   Matches **environmental criteria** appropriate for the procurement category
-   Considers **ambition levels** (Core vs Comprehensive)

### 3. Enhancement Suggestions

The library generates:

-   **Technical specifications** for environmental requirements
-   **Award criteria** for environmental performance evaluation
-   **Selection criteria** for vendor environmental capabilities
-   **Contract performance clauses** for ongoing environmental compliance

### 4. Automated Patching

Patches are applied to enhance the notice with:

-   Environmental award criteria
-   Green technical specifications
-   Sustainability requirements
-   Extended warranty periods for durable goods

## 🏗️ Architecture

```
eforms-gpp-library/
├── src/main/java/it/polimi/gpplib/
│   ├── GppNoticeAnalyzer.java           # Main interface
│   ├── DefaultGppNoticeAnalyzer.java    # Primary implementation
│   ├── model/                           # Data models
│   │   ├── Notice.java                  # Procurement notice representation
│   │   ├── GppAnalysisResult.java       # Analysis results
│   │   ├── SuggestedGppCriterion.java   # GPP criteria suggestions
│   │   └── SuggestedGppPatch.java       # Enhancement patches
│   └── utils/                           # Utility services
│       ├── GppDomainKnowledgeService.java
│       ├── GppPatchSuggester.java
│       └── GppPatchApplier.java
└── src/main/resources/
    ├── domain_knowledge/                # GPP knowledge base
    │   ├── gpp_criteria_docs.json      # GPP document definitions
    │   ├── gpp_criteria.json           # Detailed criteria specifications
    │   └── gpp_patches_data.json       # Enhancement patches
    └── examples/                        # Sample notices
        ├── subco_81.xml                # Real-world example
        └── dummy.xml                   # Test example
```

## 🛠️ Development

### Building the Project

```bash
# Compile the project
mvn compile

# Run all tests
mvn test

# Build JAR file
mvn package

# Generate complete documentation
mvn site
```

### Generate Documentation

```bash
# Generate JavaDoc API documentation
mvn javadoc:javadoc

# View documentation
open target/site/apidocs/index.html
```

### Run Tests

```bash
# Run unit tests with coverage
mvn clean test

# View coverage report
open target/site/jacoco/index.html
```

## 📚 Domain Knowledge

The library includes comprehensive domain knowledge covering:

-   **🇪🇺 EU GPP Criteria** - Official European Union Green Public Procurement criteria
-   **📋 CPV Code Mapping** - Links between procurement categories and environmental criteria
-   **🎯 Ambition Levels** - Core and Comprehensive environmental requirements
-   **🔧 Ready-to-Use Patches** - Pre-built enhancements for common procurement scenarios

### Supported Procurement Categories

The library currently supports GPP enhancement for various procurement categories including:

-   Furniture and refurbishment services
-   Office equipment and supplies
-   Construction and building materials
-   Transport and logistics services
-   And many more...

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Setup

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes and add tests
4. Ensure all tests pass: `mvn test`
5. Commit your changes: `git commit -m 'Add amazing feature'`
6. Push to the branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

## 📖 Examples

### Analyzing a Furniture Procurement Notice

```java
// Load a furniture procurement notice
Notice furnitureNotice = analyzer.loadNotice(furnitureXml);

// The analysis will identify relevant GPP criteria such as:
// - Low chemical residue upholstery coverings
// - Extended warranty periods
// - Refurbishment requirements
// - Sustainable material specifications

GppAnalysisResult result = analyzer.analyzeNotice(furnitureNotice);
```

### Working with Multi-Lot Procurements

```java
// For notices with multiple lots, analysis is performed per-lot
Notice multiLotNotice = analyzer.loadNotice(complexNoticeXml);
GppAnalysisResult result = analyzer.analyzeNotice(multiLotNotice);

// Each suggested criterion includes lot-specific information
for (SuggestedGppCriterion criterion : result.getSuggestedGppCriteria()) {
    System.out.println("Lot " + criterion.getLotId() + ": " + criterion.getName());
}
```

## 🔧 Configuration

### Ambition Levels

The library supports different ambition levels for GPP criteria:

-   **Core** - Basic environmental requirements suitable for most procurements
-   **Comprehensive** - Advanced environmental requirements for ambitious green procurement

### Custom Domain Knowledge

You can provide custom domain knowledge files:

```java
DefaultGppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer(
    "custom/gpp_documents.json",     // Custom GPP documents
    "custom/gpp_criteria.json",      // Custom criteria definitions
    "custom/gpp_patches.json"        // Custom enhancement patches
);
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🎓 Academic Context

This library was developed as part of research at **Politecnico di Milano** focusing on the digitalization and automation of Green Public Procurement processes. It aims to bridge the gap between environmental policy and practical procurement implementation.

## 📞 Support

-   📧 **Issues**: [GitHub Issues](https://github.com/your-org/eforms-gpp-library/issues)
-   📖 **Documentation**: [API Documentation](target/site/apidocs/index.html)
-   🎓 **Academic**: Contact Politecnico di Milano

---

_Making public procurement greener, one notice at a time_ 🌱
