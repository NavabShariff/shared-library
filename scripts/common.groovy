// def writeEnvToFile(workspace, copyArtifact) {
//     sh """
//     if [ "${copyArtifact}" = "false" ]; then
//         mkdir -p ${workspace}/spawn
//         rm -rf ${workspace}/spawn/env_list.txt
//         printf "BUILD_NUMBER=${env.BUILD_NUMBER}\\n" >> ${workspace}/spawn/env_list.txt
//         printf "JOB_NAME=${env.JOB_NAME}\\n" >> ${workspace}/spawn/env_list.txt
//         printf "BUILD_URL=${env.BUILD_URL}\\n" >> ${workspace}/spawn/env_list.txt
//         printf "WORKSPACE=${workspace}\\n" >> ${workspace}/spawn/env_list.txt
//         printf "DASH_UID=\\n" >> ${workspace}/spawn/env_list.txt
//         printf "AWS_ACCESS_KEY_ID=\\n" >> ${workspace}/spawn/env_list.txt
//         printf "AWS_SECRET_ACCESS_KEY=\\n" >> ${workspace}/spawn/env_list.txt
//         printf "AWS_DEFAULT_REGION=\\n" >> ${workspace}/spawn/env_list.txt
//         printf "AWS_REGION=\\n" >> ${workspace}/spawn/env_list.txt
//         printf "AWS_ZONE=\\n" >> ${workspace}/spawn/env_list.txt
//         printf "AWS_INSTANCE_NAME=\\n" >> ${workspace}/spawn/env_list.txt
//         printf "AWS_INSTANCE_TYPE=\\n" >> ${workspace}/spawn/env_list.txt
//     fi
//     """
// }
// return this
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
