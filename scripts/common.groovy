def writeEnvToFile(workspace, copyArtifact, envVars) {
    if (!copyArtifact) {
        def spawnDir = "${workspace}/spawn"
        def filePath = "${spawnDir}/env_list.txt"
        def envContent = envVars.collect { key, value ->
            "${key}=${value}"
        }.join("\n") // Convert the map to "key=value" lines

        // Ensure the directory exists
        new File(spawnDir).mkdirs()

        writeFile file: filePath, text: envContent
    }
}

