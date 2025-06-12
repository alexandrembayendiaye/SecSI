pipeline {
  agent any

  tools {
    // Versions installées auto dans Jenkins
    jdk 'Java21'
    maven 'Maven3'
    sonar 'SonarScanner'
  }

  environment {
    // Token SonarQube stocké dans Credentials
    SONAR_TOKEN = credentials('SONAR_TOKEN')
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        // Compile, teste et génère le rapport JaCoCo
        sh 'mvn clean verify jacoco:report'
      }
      post {
        always {
          // Archive rapports et couverture
          junit '**/target/surefire-reports/*.xml'
          publishCoverage adapters: [coberturaAdapter('**/target/site/jacoco/jacoco.xml')]
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('MySonarQube') {
          sh """
            mvn sonar:sonar \
              -Dsonar.projectKey=monappli_sante \
              -Dsonar.host.url=${SONAR_HOST_URL} \
              -Dsonar.login=${SONAR_TOKEN}
          """
        }
      }
    }

    stage('Quality Gate') {
      steps {
        // Bloque si la gate Sonar échoue
        waitForQualityGate abortPipeline: true
      }
    }

    stage('Package & Deploy') {
      when {
        expression { currentBuild.currentResult == 'SUCCESS' }
      }
      steps {
        // Création de l’artefact Docker et push
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
