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
            name: 'BASE_URL',
            defaultValue: 'http://localhost:3000',
            description: 'Target LMS/CRM URL (change to http://localhost:3001 if running on 3001)'
        )
        string(
            name: 'SUPABASE_URL',
            defaultValue: 'http://localhost:54321',
            description: 'Local Supabase API endpoint'
        )
    }

    environment {
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

        stage('Pre-Flight Server Check') {
            steps {
                script {
                    if (isUnix()) {
                        sh """
                            java -version
                            mvn -version
                            curl -sf ${params.BASE_URL.trim()} > /dev/null || (echo "ERROR: App is not running at ${params.BASE_URL}. Start Next.js first." && exit 1)
                        """
                    } else {
                        bat """
                            java -version
                            call mvn -version
                            curl -sf ${params.BASE_URL.trim()} > NUL || (echo ERROR: App is not running at ${params.BASE_URL}. Start Next.js first. && exit /b 1)
                        """
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
                                 "-DbaseUrl=${params.BASE_URL.trim()} " +
                                 "-Durl=${params.BASE_URL.trim()} " +
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
