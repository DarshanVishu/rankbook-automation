pipeline {
    agent any

    parameters {
        string(
            name: 'SUITE_XML',
            defaultValue: 'src/test/resources/testng.xml',
            description: 'Path to TestNG XML (e.g. src/test/resources/testng.xml)'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browsers in headless mode'
        )
        string(
            name: 'CRM_URL',
            defaultValue: 'http://localhost:3001',
            description: 'RankBook CRM URL'
        )
        string(
            name: 'LMS_URL',
            defaultValue: 'http://localhost:3001',
            description: 'RankBook LMS URL'
        )
        string(
            name: 'SUPABASE_URL',
            defaultValue: 'http://localhost:54321',
            description: 'Local Supabase API endpoint'
        )
    }

    environment {
        PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = '1'
        ALLURE_RESULTS_DIR = 'target/allure-results'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timeout(time: 45, unit: 'MINUTES')
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Toolchain & Suites') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            java -version
                            mvn -version
                            ls -la src/test/resources/ || true
                        '''
                    } else {
                        bat '''
                            java -version
                            call mvn -version
                            dir src\\test\\resources
                        '''
                    }
                }
            }
        }

        stage('Execute Unified CRM & LMS Suite') {
            steps {
                script {
                    def suiteArg = fileExists(params.SUITE_XML.trim()) ? "-DsuiteXmlFile=${params.SUITE_XML.trim()}" : ""
                    def mvnCmd = "mvn clean test " +
                                 "${suiteArg} " +
                                 "-Dheadless=${params.HEADLESS} " +
                                 "-DcrmUrl=${params.CRM_URL.trim()} " +
                                 "-DlmsUrl=${params.LMS_URL.trim()} " +
                                 "-DsupabaseUrl=${params.SUPABASE_URL.trim()} " +
                                 "-Dallure.results.directory=${ALLURE_RESULTS_DIR}"

                    if (isUnix()) {
                        sh "${mvnCmd}"
                    } else {
                        bat "call ${mvnCmd}"
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (isUnix()) {
                    sh '''
                        if [ -d "allure-results" ] && [ "$(ls -A allure-results 2>/dev/null)" ]; then
                            mkdir -p target/allure-results
                            cp -r allure-results/* target/allure-results/
                        fi
                        if [ -d "target/allure-results" ]; then
                            mvn io.qameta.allure:allure-maven:2.12.0:report
                        fi
                    '''
                } else {
                    bat '''
                        if exist allure-results (
                            if not exist target\\allure-results mkdir target\\allure-results
                            xcopy /E /I /Y allure-results target\\allure-results >nul
                        )
                        if exist target\\allure-results (
                            call mvn io.qameta.allure:allure-maven:2.12.0:report
                        )
                    '''
                }
            }

            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            archiveArtifacts artifacts: 'target/site/allure-maven-plugin/**, target/playwright-traces/**, target/surefire-reports/**',
                             allowEmptyArchive: true
        }
    }
}
