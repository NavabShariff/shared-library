def call(workspace, copyArtifact = false) {
    if (!copyArtifact) {
        def filePath = "${workspace}/spawn/env_list.txt"
        def envVariables = [
            "BUILD_NUMBER=${env.BUILD_NUMBER}",
            "JOB_NAME=${env.JOB_NAME}",
            "BUILD_URL=${env.BUILD_URL}",
            "WORKSPACE=${env.WORKSPACE}",
            "DASH_UID=",
            "AWS_ACCESS_KEY_ID=",
            "AWS_SECRET_ACCESS_KEY=",
            "AWS_DEFAULT_REGION=",
            "AWS_REGION=",
            "AWS_ZONE=",
            "AWS_INSTANCE_NAME=",
            "AWS_INSTANCE_TYPE="
        ]

        // Create the directory and write to file
        new File("${workspace}/spawn").mkdirs()
        new File(filePath).withWriter { writer ->
            envVariables.each { writer.writeLine(it) }
        }
        echo "Environment variables written to ${filePath}"
    }
}
