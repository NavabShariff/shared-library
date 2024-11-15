def writeEnvToFile(workspace, copyArtifact) {
    sh """
    if [ "${copyArtifact}" = "false" ]; then
        mkdir -p ${workspace}/spawn
        rm -rf ${workspace}/spawn/env_list.txt
        printf "
BUILD_NUMBER
JOB_NAME
BUILD_URL
WORKSPACE
DASH_UID
AWS_DEFAULT_REGION
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_REGION
AWS_ZONE
AWS_INSTANCE_NAME
AWS_INSTANCE_TYPE
" > ${workspace}/spawn/env_list.txt
    fi
    """
}

return this


def cleanVcenter(Map args) {
    echo "Cleaning with args: ${args}"
    

    def command = """
    docker run --rm \
    --env-file ${args.WORKSPACE}/spawn/env_list.txt \
    -v ${args.WORKSPACE}:${args.WORKSPACE} \
    -v /var/run/docker.sock:/var/run/docker.sock ${args.SPAWN_IMAGE} \
    spawn --verbose --config-dir ${args.WORKSPACE}/spawn \
    clean vcenter \
    --vcenter-name "${args.VCENTER_NAME}"
    """
    
    sh command
}

