pipeline {
    agent any

    parameters {
        string(name: 'REPO_URL', defaultValue: 'https://github.com/yourusername/yourrepo.git', description: 'כתובת ה-Repository')
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'שם הענף (branch) להרצה')
    }

    environment {
        // מגדירים כאן משתני סביבה לפי הצורך
        // לדוגמה: PATH או JAVA_HOME במידת הצורך
    }

    options {
        timeout(time: 5, unit: 'MINUTES') // הגבלת זמן לכל בלוק
    }

    triggers {
        cron('30 5 * * *')   // הרצת הפייפליין כל יום בשעה 05:30
        cron('0 14 * * *')   // הרצת הפייפליין כל יום בשעה 14:00
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out from ${params.REPO_URL} branch ${params.BRANCH_NAME}"
                // מורידים את הקוד מה-Repo לפי הפרמטרים שהוגדרו
                checkout([$class: 'GitSCM',
                          branches: [[name: params.BRANCH_NAME]],
                          userRemoteConfigs: [[url: params.REPO_URL]]])
            }
        }

        stage('Compile') {
            steps {
                echo 'Starting compilation stage'
                sh 'mvn compile'
                echo 'Compilation stage completed successfully'
            }
        }

        stage('Test') {
            steps {
                echo 'Starting test stage'
                sh 'mvn test'
                echo 'Test stage completed successfully'
            }
        }
    }

    post {
        always {
            echo 'This will always run, after any build status.'
        }
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
