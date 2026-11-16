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
