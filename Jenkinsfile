pipeline {
	agent any
	stages {
		stage("get back log") {
			steps {
				script {
					sshPublisher(
						publishers: [
							sshPublisherDesc(
								configName: 'ubuntu', 
								transfers: [
									sshTransfer(
										cleanRemote: false, 
										excludes: '', 
										execCommand: '''
cd compose/logger
echo "<meta charset="utf-8"><pre>" > logger.html
sudo docker logs backapiservice >> logger.html
echo "</pre>" >> logger.html''',
										execTimeout: 120000, 
										flatten: false, 
										makeEmptyDirs: false, 
										noDefaultExcludes: false, 
										patternSeparator: '[, ]+', 
										remoteDirectory: 'compose/logger', 
										remoteDirectorySDF: false, 
										removePrefix: '', 
										sourceFiles: ''
									)
								], 
								usePromotionTimestamp: false, 
								useWorkspaceInPromotion: false, 
								verbose: true
							)
						]
					)
				}
			}
		}
	}
}
