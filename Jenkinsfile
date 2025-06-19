pipeline {
  agent any

  tools {
    jdk 'JDK21-sante'
    maven 'maven-sante'
  }

  environment {
    SONAR_TOKEN = credentials('SONAR_TOKEN')
    JAVA_HOME = tool name: 'JDK21-sante', type: 'hudson.model.JDK'
    PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    SONAR_HOST_URL = 'http://172.17.0.2:9000'  // <- IP réelle du conteneur SonarQube

  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn clean verify'
      }
      post {
        always {
          script {
            try {
              // junit '**/target/surefire-reports/*.xml'
            } catch (Exception e) {
              echo "⚠️ Aucun fichier de test trouvé."
            }
          }
          // publishCoverage adapters: [coberturaAdapter('**/target/site/jacoco/jacoco.xml')]
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
          withSonarQubeEnv('MySonarQube') {
            sh '''
              echo "🔐 Analyse SonarQube en cours..."
              mvn sonar:sonar \
                -Dsonar.projectKey=monappli_sante \
                -Dsonar.host.url=$SONAR_HOST_URL \
                -Dsonar.login=$SONAR_TOKEN \
                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

              echo "📂 Listing de target/sonar :"
              ls -la target/sonar || echo "❌ Dossier introuvable"

              echo "📄 Contenu de report-task.txt :"
              cat target/sonar/report-task.txt || echo "❌ report-task.txt introuvable"
            '''
          }
        }
      }
    }

    stage('Check JAVA_HOME') {
      steps {
        sh 'echo JAVA_HOME=$JAVA_HOME'
        sh 'java -version'
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 30, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Package & Deploy') {
      when {
        expression { currentBuild.currentResult == 'SUCCESS' }
      }
      steps {
        sh 'docker build -t monappli-sante:${env.BUILD_NUMBER} .'
        sh 'docker push monappli-sante:${env.BUILD_NUMBER}'
      }
    }
  }

  post {
    failure {
      echo "❌ Le pipeline a échoué – corrigez les erreurs et relancez."
    }
  }
}
