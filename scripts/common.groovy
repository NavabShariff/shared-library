def writeEnvToFile(workspace, copyArtifact, dynamicVars) {
    if (copyArtifact == "false") {
        def envList = []

        // Add dynamic variables to the list
        dynamicVars.each { varName ->
            envList << "${varName}=${env.getProperty(varName) ?: ''}"
        }

        // Write to the file
        sh """
        mkdir -p ${workspace}/spawn
        rm -rf ${workspace}/spawn/env_list.txt
        printf "${envList.join('\\n')}\\n" >> ${workspace}/spawn/env_list.txt
        """
    }
}
