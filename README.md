@"
# RankBook LMS & CRM Test Automation Framework

An enterprise hybrid test automation framework engineered for the RankBook Learning Management System (LMS) and CRM lead management pipeline. Built with Java 17, Selenium WebDriver, Microsoft Playwright, TestNG, and Supabase REST backend seeding.

---

## Architecture & Design Highlights

- **Dual UI Engines**: Combines **Selenium WebDriver 4** for rich DOM interactions and drag-and-drop state verification with **Microsoft Playwright** for ultra-fast, headless student dashboard checks.
- **Sub-Second Database Seeding**: Employs REST Assured against Supabase PostgREST and Admin Auth endpoints (\`SupabaseUserSeeder\`, \`SupabaseLeadBulkSeeder\`) to provision valid users and leads in milliseconds, bypassing brittle UI setup forms.
- **Kanban Board State Validation**: Automates drag-and-drop column transitions across the CRM lifecycle (*New Lead* → *Attempted Contact*), including duplicate rejection assertions.
- **CI/CD Ready**: Configured for fully headless runs via Maven Surefire, featuring custom TestNG listeners that capture failure screenshots and compile ExtentReports 5 dashboards.

---

## Tech Stack

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java | 17 (LTS) |
| **UI Engines** | Selenium WebDriver / Playwright | 4.21.0 / 1.47.0 |
| **API & Database** | REST Assured / PostgreSQL JDBC | 5.4.0 / 42.7.3 |
| **Test Runner** | TestNG | 7.10.2 |
| **Reporting** | ExtentReports | 5.1.2 |
| **Build Tool** | Apache Maven | 3.9+ |

---

## Test Execution

### 1. Run Complete Regression Suite (Headless)
\`\`\`bash
mvn clean test "-DsuiteXmlFile=testng.xml" "-Dheadless=true"
\`\`\`

### 2. Run Focused Test Suites
\`\`\`bash
# CRM Pipeline & Lead Management
mvn test "-Dtest=CrmKanbanPipelineTest,CrmLeadManagementTest"

# Playwright E2E Flow
mvn test "-Dtest=FastLeadSeedingToLmsPlaywrightTest"
\`\`\`

---

## Reports & Artifacts
After execution, interactive HTML dashboards are generated at:
- **ExtentReports**: \`reports/index.html\`
- **Surefire Reports**: \`target/surefire-reports/index.html\`
"@ | Out-File -Encoding utf8 README.md
|
Then commit and push it to your GitHub:
git add README.md
git commit -m "docs: add comprehensive framework overview and execution guide"
git push origin main
