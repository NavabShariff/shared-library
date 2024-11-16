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
// return this

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

def createVms(Map args) {
    echo "createVms with args: ${args}"
    

    def command = """
    docker run --rm \
    --env-file ${args.WORKSPACE}/spawn/env_list.txt \
    -v ${args.WORKSPACE}:${args.WORKSPACE} \
    -v /var/run/docker.sock:/var/run/docker.sock ${args.SPAWN_IMAGE} \
    spawn --verbose --config-dir ${args.WORKSPACE}/spawn \
    vms \
    --count "${args.NUM_NODES}" \
    --cpu "${args.CPU}" \
    --memory "${args.MEMORY}" --disk-count-px "${args.DISK_COUNT}" \
    --os "${args.OS}" --provisioner "${args.PROVISIONER}" \
    --datacenter "${args.DATA_CENTER}" \
    --networks "${args.NETWORK}" \
    --vcenter-name  "${args.VCENTER_NAME}" \
    --vcenter-cluster "${args.CLUSTER_NAME}" \
    --vcenter-datastore "${DATASTORE}" \
    --vcenter-resource-pool "${args.RESOURCE_POOL_NAME}" \
    --pxPrivateCloud true \
    --pxCloudUser pwx-bat \
    --tags stork-test --test-tags team:stork, level:13, owner:gejain, pipeline-name:storkbackuprestorewithupgrade, pipeline:true
    """
    
    sh command
}

def getNginxContainerId(Map args) {
    echo "Checking whether to start the NGINX container..."
    
    // Check if the argument 'shouldRun' is true
    if (args.shouldRun) {
        echo "Starting NGINX container and retrieving container ID..."
        
        // Run the NGINX container in detached mode
        def containerId = sh(
            script: """
            docker run -d --name ${args.CONTAINER_NAME} ${args.CONTAINER_IMAGE}
            """,
            returnStdout: true
        ).trim()
        
        echo "NGINX container started with ID: ${containerId}"
        return containerId
    } else {
        echo "Skipping NGINX container creation as shouldRun is false."
        return null  // You can return null or any other indicator to show the process was skipped
    }
}


def removeNginxContainer(Map args) {

    def removecontainer = """
    docker rm -f ${args.CONTAINER_ID}
    """    
    // Stop and remove the container using docker rm -f
    sh(script: removecontainer)
    echo "NGINX container with ID ${args.CONTAINER_ID} has been removed."
}

return this