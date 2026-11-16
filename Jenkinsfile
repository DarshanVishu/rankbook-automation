pipeline {
    agent any

    parameters {
        choice(
            name: 'SUITE_XML',
            choices: [
                'src/test/resources/testng.xml',
                'src/test/resources/testng-smoke.xml',
                'src/test/resources/testng-crm.xml',
                'src/test/resources/testng-lms.xml',
                'src/test/resources/testng-all.xml'
            ],
            description: 'Select the combined CRM + LMS TestNG Suite XML to execute'
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

        stage('Verify Toolchain') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            java -version
                            mvn -version
                        '''
                    } else {
                        bat '''
                            java -version
                            mvn -version
                        '''
                    }
                }
            }
        }

        stage('Execute Unified CRM & LMS Suite') {
            steps {
                script {
                    def mvnCmd = "mvn clean test " +
                                 "-DsuiteXmlFile=${params.SUITE_XML} " +
                                 "-Dheadless=${params.HEADLESS} " +
                                 "-DcrmUrl=${params.CRM_URL.trim()} " +
                                 "-DlmsUrl=${params.LMS_URL.trim()} " +
                                 "-DsupabaseUrl=${params.SUPABASE_URL.trim()} " +
                                 "-Dallure.results.directory=${ALLURE_RESULTS_DIR}"

                    if (isUnix()) {
                        sh "${mvnCmd}"
                    } else {
                        bat "${mvnCmd}"
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
                        mvn io.qameta.allure:allure-maven:2.12.0:report
                    '''
                } else {
                    bat '''
                        if exist allure-results (
                            if not exist target\\allure-results mkdir target\\allure-results
                            xcopy /E /I /Y allure-results target\\allure-results >nul
                        )
                        mvn io.qameta.allure:allure-maven:2.12.0:report
                    '''
                }
            }

            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            archiveArtifacts artifacts: 'target/allure-report/**, target/playwright-traces/**, target/surefire-reports/**',
                             allowEmptyArchive: true
        }
    }
}
