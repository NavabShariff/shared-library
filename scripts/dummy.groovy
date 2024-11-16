def getNginxContainerId() {
    echo "Starting NGINX container and retrieving container ID..."
    
    // Run the NGINX container in detached mode
    def containerId = sh(
        script: "docker run -d --name ${args.CONTAINER_NAME} ${args.CONTAINER_IMAGE}",
        returnStdout: true
    ).trim()
    
    echo "NGINX container started with ID: ${containerId}"
    return containerId
}

def removeNginxContainer(containerId) {
    echo "Stopping and removing NGINX container with ID: ${containerId}"
    
    // Stop and remove the container using docker rm -f
    sh(script: "docker rm -f ${containerId}")
    echo "NGINX container with ID ${containerId} has been removed."
}

return this