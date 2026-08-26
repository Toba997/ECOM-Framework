import groovy.json.JsonOutput

// ---------------------------------------------------------------------------
// Jenkinsfile for ecom-app  (Maven compile + TestNG tests + webhook notify)
//
// What it does on every run:
//   1. Checkout the code
//   2. mvn clean compile
//   3. mvn test (TestNG unit tests)
//   4. POST the result to the Pipeline Office webhook server (Node + DeepSeek)
//
// Required Jenkins plugins:
//   - Pipeline            (bundled)
//   - Git plugin
//   - HTTP Request plugin (https://plugins.jenkins.io/http_request)
//   - GitHub Integration plugin (only for the githubPush() trigger below)
// ---------------------------------------------------------------------------

def notifyWebhook = { String status ->
    def payload = [
        project_name: env.JOB_NAME,
        build_number: env.BUILD_NUMBER,
        build_status: status,
        branch: (env.GIT_BRANCH ?: 'main').replaceFirst('^origin/', ''),
        build_url: env.BUILD_URL
    ]
    try {
        httpRequest(
            url: env.WEBHOOK_URL,
            httpMode: 'POST',
            contentType: 'APPLICATION_JSON',
            requestBody: JsonOutput.toJson(payload),
            customHeaders: [[name: 'X-Webhook-Token', value: env.WEBHOOK_TOKEN]],
            validResponseCodes: '200:299',
            quiet: true
        )
    } catch (err) {
        echo "Webhook notification failed (non-fatal): ${err.getMessage()}"
    }
}

pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        // Must match the values in the webhook server's .env file
        WEBHOOK_URL   = 'http://localhost:3000/api/webhook'
        WEBHOOK_TOKEN = 'change-me-token'
    }

    // To auto-trigger on GitHub pushes:
    //   1. Install the "GitHub Integration" plugin
    //   2. Uncomment the line below
    //   3. In GitHub repo → Settings → Webhooks → add http://<jenkins>:8080/github-webhook/
    // triggers { githubPush() }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                script {
                    if (isUnix()) { sh 'mvn -B clean compile' }
                    else          { bat 'mvn -B clean compile' }
                }
            }
        }

        stage('Test (TestNG)') {
            steps {
                script {
                    if (isUnix()) { sh 'mvn -B test' }
                    else          { bat 'mvn -B test' }
                }
            }
        }
    }

    post {
        success  { script { notifyWebhook('SUCCESS') } }
        failure  { script { notifyWebhook('FAILURE') } }
        unstable { script { notifyWebhook('UNSTABLE') } }
        aborted  { script { notifyWebhook('ABORTED') } }
    }
}
