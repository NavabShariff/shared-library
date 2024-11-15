def writeEnvToFile(workspace, copyArtifact) {
    sh """
    if [ "${copyArtifact}" = "false" ]; then
        mkdir -p ${workspace}/spawn
        rm -rf ${workspace}/spawn/env_list.txt
        printf "
BUILD_NUMBER="{env.BUILD_NUMBER}"
JOB_NAME="{env.JOB_NAME}"
BUILD_URL="{env.BUILD_URL}"
WORKSPACE="{env.WORKSPACE}"
DASH_UID="navab"
AWS_DEFAULT_REGION="pradeepa"
AWS_ACCESS_KEY_ID="Navab"
AWS_SECRET_ACCESS_KEY="don't know"
AWS_REGION="ap-south-1"
AWS_ZONE="ap-south-1a"
AWS_INSTANCE_NAME="spawn"
AWS_INSTANCE_TYPE="t2.micro"
" > ${workspace}/spawn/env_list.txt
    fi
    """
}

return this


def cleanVcenter(Map args) {
    echo "Cleaning with args: ${args}"
    
    // Test command with a simpler image (nginx or ubuntu)
    def command = """
    docker run --rm \
    --env-file ${args.WORKSPACE}/spawn/env_list.txt \
    -v ${args.WORKSPACE}:${args.WORKSPACE} \
    -v /var/run/docker.sock:/var/run/docker.sock ${args.SPAWN_IMAGE} \
    /bin/bash -c "printenv"
    """
    
    // Run the command (this will print "Hello from <image_name>" to check if the container is running)
    sh command
}

