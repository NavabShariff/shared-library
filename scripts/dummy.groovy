def configLabelMasterNode(Map args) {
    
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