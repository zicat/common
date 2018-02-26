stage 'Compile & Test & Deploy'
node('master') {
    checkout scm
    try {
        sh "mvn deploy"
    } catch(e) {
        currentBuild.result = "FAILED"
        throw e
    } finally {
        notifyBuild(currentBuild.result)
    }
    stash 'working-copy'
    step([$class: 'CoberturaPublisher', autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile:"target/site/cobertura/coverage.xml", failNoReports: false, failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false])
}

stage 'Upload Sonar'
node('master') {
    sh "mvn sonar:sonar"
    unstash 'working-copy'
}

def notifyBuild(String buildStatus = 'STARTED') {
  buildStatus = buildStatus ?: 'SUCCESS'
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """
    <div style='font-family:微软雅黑'><p> Hi All:</p></div>
    <div style='text-indent:35px;font-family:微软雅黑'><p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p></div>
    <div style='text-indent:35px;font-family:微软雅黑'><p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p></div>
    <div style='font-family:微软雅黑'><p>Thx</p></div>"""

  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }

  emailext (
      subject: subject,
      body: details,
      to:'nesc-sh.mis.neweggec.developer.bigdata@newegg.com',
      recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )
}