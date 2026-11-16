pipeline {
    agent any

    parameters {
        choice(
            name: 'SUITE_XML',
            choices: [
                'src/test/resources/testng-crm-smoke.xml',
                'src/test/resources/testng-crm-web.xml',
                'src/test/resources/testng-crm-rls-api.xml',
                'src/test/resources/testng-crm-all.xml'
            ],
            description: 'Select the RankBook CRM TestNG Suite XML to execute'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browsers in headless mode'
        )
        string(
            name: 'BASE_URL',
            defaultValue: 'http://localhost:3001',
            description: 'Target RankBook CRM Next.js URL (default: local port 3001)'
        )
        string(
            name: 'SUPABASE_URL',
            defaultValue: 'http://localhost:54321',
            description: 'Target Supabase API endpoint for RLS/API tests (default: local port 54321)'
        )
    }

    environment {
        PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = '1'
        ALLURE_RESULTS_DIR = 'target/allure-results'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timeout(time: 40, unit: 'MINUTES')
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Toolchain & Local Stack') {
            steps {
                script {
                    def checkCmd = """
                        java -version
                        mvn -version
                        curl -s -o /dev/null -w "CRM App (3001): %{http_code}\\n" ${params.BASE_URL} || echo "Warning: CRM App not reachable at ${params.BASE_URL}"
                        curl -s -o /dev/null -w "Supabase API (54321): %{http_code}\\n" ${params.SUPABASE_URL}/rest/v1/ || echo "Warning: Supabase API not reachable at ${params.SUPABASE_URL}"
                    """
                    if (isUnix()) {
                        sh checkCmd
                    } else {
                        bat """
                            java -version
                            mvn -version
                            curl -s -o NUL -w "CRM App (3001): %%{http_code}\n" ${params.BASE_URL} || echo Warning: CRM App not reachable at ${params.BASE_URL}
                            curl -s -o NUL -w "Supabase API (54321): %%{http_code}\n" ${params.SUPABASE_URL}/rest/v1/ || echo Warning: Supabase API not reachable at ${params.SUPABASE_URL}
                        """
                    }
                }
            }
        }

        stage('Execute CRM TestNG Suite') {
            steps {
                script {
                    def mvnCmd = "mvn clean test " +
                                 "-DsuiteXmlFile=${params.SUITE_XML} " +
                                 "-Dheadless=${params.HEADLESS} " +
                                 "-DbaseUrl=${params.BASE_URL.trim()} " +
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
                        mvn allure:report
                    '''
                } else {
                    bat '''
                        if exist allure-results (
                            if not exist target\\allure-results mkdir target\\allure-results
                            xcopy /E /I /Y allure-results target\\allure-results >nul
                        )
                        mvn allure:report
                    '''
                }
            }

            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            archiveArtifacts artifacts: 'target/allure-report/**, target/playwright-traces/**, target/surefire-reports/**',
                             allowEmptyArchive: true
        }
    }
}
