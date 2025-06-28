# Domain Knowledge Management

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

# Generate JSON files
python generate_json.py

# Deactivate when done
deactivate
```

## 🔄 Workflow

### For New Contributors

1. **Clone the repository**
2. **Follow Quick Setup** (above)
3. **Edit Excel files** in `excel_sources/`
4. **Generate JSON** with the script
5. **Copy to resources** and test
6. **Commit only** Excel sources and final JSON files

### What Gets Committed vs Generated

✅ **Commit these:**

-   Excel source files (`excel_sources/*.xlsx`)
-   Python scripts (`scripts/*.py`)
-   Requirements file (`scripts/requirements.txt`)
-   Final JSON files in main resources (`src/main/resources/domain_knowledge/`)

❌ **Don't commit these:**

-   Virtual environment (`scripts/venv/`)
-   Generated files (`generated/*.json`)
