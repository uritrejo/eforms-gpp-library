# eforms-gpp-library

A Java library for analyzing and improving public procurement notices through Green Public Procurement (GPP) criteria and recommendations.

## Features

-   Load and parse procurement notices from XML (eForms standard)
-   Analyze notices to identify relevant GPP criteria based on CPV codes
-   Suggest improvements and patches for better environmental sustainability
-   Apply patches to create enhanced notice versions
-   Comprehensive domain knowledge management for GPP documents, criteria, and patches

## Quick Start

```java
// Create analyzer with default configuration
GppNoticeAnalyzer analyzer = new DefaultGppNoticeAnalyzer();

// Load a notice from XML
Notice notice = analyzer.loadNotice(xmlString);

// Analyze the notice
GppAnalysisResult result = analyzer.analyzeNotice(notice);

// Get suggested patches
List<SuggestedGppPatch> patches = analyzer.suggestPatches(notice, result.getSuggestedCriteria());

// Apply patches to improve the notice
Notice improvedNotice = analyzer.applyPatches(notice, patches);
```

## Documentation

### API Documentation (JavaDoc)

Generate the JavaDoc documentation:

```bash
mvn javadoc:javadoc
```

View the generated documentation by opening `target/site/apidocs/index.html` in your browser.

### Coverage Report

Generate test coverage report:

```bash
mvn clean test
```

View the coverage report at `target/site/jacoco/index.html`.

## Building

```bash
# Compile the project
mvn compile

# Run tests
mvn test

# Build JAR
mvn package

# Generate all documentation
mvn site
```

## Project Structure

-   `src/main/java/it/polimi/gpplib/` - Core library classes
    -   `GppNoticeAnalyzer` - Main interface
    -   `DefaultGppNoticeAnalyzer` - Default implementation
    -   `model/` - Data model classes
    -   `utils/` - Utility classes and services
-   `src/main/resources/` - Configuration and domain knowledge files
-   `src/test/` - Unit tests
