def writeEnvToFile(Map args) {
    println "writeEnvToFile"
    println args

    def workspace = args.WORKSPACE
    def copyArtifact = args.COPY_ARTIFACT
    def envVars = args.ENV_VARS ?: []

    if (copyArtifact == "false") {
        def envContent = ""

        // Dynamically build the environment variables content
        envVars.each { varName ->
            def varValue = env.getProperty(varName) ?: ''
            envContent += "${varName}=${s}\n"
        }

        // Write to file
        sh """
        mkdir -p ${workspace}/spawn
        rm -rf ${workspace}/spawn/env_list.txt
        printf "${envContent}" >> ${workspace}/spawn/env_list.txt
        """
    }
}