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

def configMapSetenv(Map args) {
    echo "Cleaning with args: ${args}"
    

    def command = """
    sudo docker run -t --net=host \
    -v ${args.WORKSPACE}/spawn/k8s/clusterGroup0/kubeconfig:/tmp/kubeconfig \
    -e KUBECONFIG="${args.KUBECONFIG}"
    -e SSH_USERNAME="${args.SSH_USERNAME}"
    -e SSH_PASSWORD="${args.SSH_PASSWORD}"
    lachlanevenson/k8s-kubectl -n kube-system set env deploy/stork TEST_MODE=true
    """
    
    sh command
}

def GetMasterNode(Map args) {
    echo "Cleaning with args: ${args}"
    

    def masterNode = sh(
        script: """
        sudo docker run -t --net=host \
        -v ${args.WORKSPACE}/spawn/k8s/clusterGroup0/kubeconfig:/tmp/kubeconfig \
        -e KUBECONFIG="${args.KUBECONFIG}"
        -e SSH_USERNAME="${args.SSH_USERNAME}"
        -e SSH_PASSWORD="${args.SSH_PASSWORD}"
        lachlanevenson/k8s-kubectl get node --selector='node-role-kubernetes.io/control-plane' -o jsonpath='{.items[0].metadata.name}'
        """,
        returnStdout: true
    ).trim()
    
    return masterNode
}

// Function to label the master node
def LabelMasterNode(Map args) {
    
    def labelCommand = """
    sudo docker run -t --net=host \
        -v ${args.WORKSPACE}/spawn/k8s/clusterGroup0/kubeconfig:/tmp/kubeconfig  \
        -e KUBECONFIG=/tmp/kubeconfig \
        -e SSH_USERNAME="${args.SSH_USERNAME}" \
        -e SSH_PASSWORD="${args.SSH_PASSWORD}" \
        ${args.SPAWN_IMAGE} \
        lachlanevenson/k8s-kubectl label node ${args.MASTER_NODE} px/enabled=false
    """
    
    // Run the labeling command
    sh(script: labelCommand)
    echo "Master node ${args.MASTER_NODE} labeled successfully."
}