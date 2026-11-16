# RankBook LMS & CRM Test Automation Framework

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Playwright](https://img.shields.io/badge/Playwright-1.50.0-2EAD33?logo=playwright)
![Selenium](https://img.shields.io/badge/Selenium-4.19.1-43B02A?logo=selenium)
![REST Assured](https://img.shields.io/badge/REST_Assured-5.4.0-009688)
![TestNG](https://img.shields.io/badge/TestNG-7.9.0-red)
![PostgreSQL / Supabase](https://img.shields.io/badge/PostgreSQL_&_RLS-Validated-336791?logo=postgresql)
![Allure Report](https://img.shields.io/badge/Allure-2.26.0-yellow)
![CI/CD](https://img.shields.io/badge/GitHub_Actions-Automated-2088FF?logo=githubactions)

An enterprise-grade, modular **End-to-End Test Automation Framework** built for the **RankBook Learning Management System (LMS)** and **Customer Relationship Management (CRM)** platforms. Engineered with **Java 17**, **Playwright**, **Selenium**, **REST Assured**, and **TestNG**, the framework validates multi-role academic workflows, lead-to-enrollment CRM pipelines, Row-Level Security (RLS) access boundaries, and cross-device web responsiveness with automated **Allure Reporting** via **GitHub Actions**.

---

## Key Highlights

- **Dual-Platform Coverage (LMS + CRM):** Unified test architecture covering both **RankBook LMS** (courses, batches, assessments, attendance, student/teacher portals) and **RankBook CRM** (lead capture, counsellor assignment, follow-up pipelines, fee & admission conversion).
- **Role-Based Access & RLS Security Validation:** Automated verification of role isolation across **Admin**, **Counsellor / Sales**, **Teacher / Faculty**, and **Student** accounts, ensuring database Row-Level Security (RLS) policies prevent unauthorized cross-tenant or cross-role data access.
- **Hybrid UI + REST API Execution:** Pairs **REST Assured** API setup/teardown and payload assertions with **Playwright** and **Selenium** UI journeys for fast, deterministic, state-isolated regression runs.
- **Data-Driven Cohort & User Provisioning:** Supports large-scale bulk test data seeding and validation via **Apache POI** (`.xlsx`/`.xls`) and JSON datasets for student, faculty, and lead records.
- **CI/CD & Failure Observability:** Integrated **GitHub Actions** workflows capturing Playwright traces, screenshots, API logs, and Surefire reports consolidated into live **Allure Reports** on **GitHub Pages**.

---

## Tech Stack

| Layer | Technology & Version |
| :--- | :--- |
| **Core Language & Build** | Java 17 (Temurin), Apache Maven |
| **Web & Mobile Web Automation** | Playwright Java `1.50.0`, Selenium WebDriver `4.19.1` |
| **API & Backend Validation** | REST Assured `5.4.0`, Jackson Databind `2.17.0` |
| **Test Orchestration** | TestNG `7.9.0` with XML Suite Routing & Parallel Execution |
| **Test Data Management** | Apache POI `5.2.5` (`.xls` / `.xlsx`), JSON & Environment Configs |
| **Reporting & Diagnostics** | Allure TestNG `2.26.0`, AspectJ Weaver `1.9.22`, Playwright Traces |
| **CI/CD & Hosting** | GitHub Actions (`ubuntu-24.04`), GitHub Pages |

---

## Project Architecture

```text
RankBook-LMS-CRM-Automation/
├── .github/
│   └── workflows/
│       └── regression.yml                     # CI/CD pipeline & Allure GitHub Pages deployment
├── src/
│   ├── main/java/com/rankbook/automation/
│   │   ├── core/
│   │   │   ├── constants/                     # Environment URLs, role credentials & wait timeouts
│   │   │   └── utils/                         # Excel/JSON data readers, auth token & retry helpers
│   │   ├── drivers/
│   │   │   ├── capabilities/                  # Desktop & mobile viewport browser configurations
│   │   │   ├── factory/                       # TargetEngine & DriverFactory routing
│   │   │   └── managers/                      # ThreadLocal Playwright & WebDriver lifecycle
│   │   ├── pages/
│   │   │   ├── lms/                           # LMS Page Objects (Dashboard, Courses, Batches, Assessments)
│   │   │   └── crm/                           # CRM Page Objects (Leads, Pipeline, Counsellor, Admissions)
│   │   └── api/                               # REST Assured specs, auth endpoints & DTO models
│   └── test/
│       ├── java/com/rankbook/automation/tests/
│       │   ├── lms/                           # Student, Teacher & Admin LMS functional suites
│       │   ├── crm/                           # Lead lifecycle, follow-up & conversion tests
│       │   ├── api/                           # API schema, RBAC/RLS & status code assertions
│       │   └── e2e/                           # Cross-system CRM Lead -> LMS Student onboarding flows
│       └── resources/
│           ├── testng-lms.xml                 # LMS regression suite
│           ├── testng-crm.xml                 # CRM regression suite
│           ├── testng-api.xml                 # REST API & RLS security suite
│           ├── testng-e2e.xml                 # Cross-platform CRM-to-LMS E2E suite
│           └── testng-all.xml                 # Master full-regression suite
└── pom.xml

```

---

## Automated Test Coverage

### 1. RankBook CRM (`testng-crm.xml`)

* **Lead Lifecycle Management:** Lead creation, source attribution, deduplication checks, and bulk CSV/Excel import verification.
* **Sales & Counsellor Pipeline:** Stage transitions (`New` -> `Contacted` -> `Demo Scheduled` -> `Converted`), counsellor assignment rules, and SLA follow-up reminders.
* **Fee & Admission Tracking:** Installment/fee structure calculation, payment status updates, and conversion handoff triggers.

### 2. RankBook LMS (`testng-lms.xml`)

* **Admin & Academic Setup:** Course creation, batch scheduling, faculty mapping, and student enrollment management.
* **Faculty / Teacher Portal:** Study material uploads, assignment creation, attendance marking, and gradebook evaluation.
* **Student Portal:** Course catalog access, lecture playback/resource downloads, quiz/assessment submissions, and progress tracking.

### 3. Cross-Platform E2E & RBAC Security (`testng-e2e.xml` / `testng-api.xml`)

* **CRM-to-LMS Onboarding Flow:** Converts a qualified CRM lead into an enrolled LMS student and verifies automated account provisioning and batch access.
* **Role & RLS Boundary Enforcement:** Validates via API and UI that students cannot access admin/faculty routes and counsellors only view assigned lead records.

---

## Getting Started

### Prerequisites

* **Java JDK 17+**
* **Apache Maven 3.8+**
* **Git**

### 1. Clone the Repository

```bash
git clone [https://github.com/DarshanVishu/RankBook-LMS-CRM-Automation.git](https://github.com/DarshanVishu/RankBook-LMS-CRM-Automation.git)
cd RankBook-LMS-CRM-Automation

```

### 2. Install Playwright Browsers (First-Time Setup)

```bash
mvn test-compile exec:java -e \
  -Dexec.classpathScope=test \
  -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="install chromium webkit"

```

*(On Windows PowerShell, wrap `-D` flags in double quotes: `"-Dexec.classpathScope=test" "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install chromium webkit"`)*

### 3. Run Test Suites

**Run LMS Regression Suite:**

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-lms.xml -Dheadless=true

```

**Run CRM Regression Suite:**

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-crm.xml -Dheadless=true

```

**Run API & Security Suite:**

```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-api.xml

```

**Run Cross-Platform CRM-to-LMS E2E Suite:**

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-e2e.xml -Dheadless=true

```

**Run Full Master Regression:**

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-all.xml -Dheadless=true

```

### 4. Generate & View Allure Report

```bash
mvn allure:serve

```

---

## CI/CD Pipeline (GitHub Actions)

1. **Automated Suite Execution:** Runs LMS, CRM, and API suites on every push/PR to `main` or via manual `workflow_dispatch` suite selection.
2. **Trace & Artifact Archiving:** Uploads Playwright execution traces, failure screenshots, and Surefire XML reports for rapid debugging.
3. **Allure Report Publishing:** Generates and deploys the consolidated HTML test report directly to **GitHub Pages**.

---

## Author

**Darsh** — QA Automation & Platform Engineer

* GitHub: [@DarshanVishu](https://github.com/DarshanVishu)

```

```

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
