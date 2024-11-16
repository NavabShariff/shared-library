def getNginxContainerId() {
    echo "Starting NGINX container and retrieving container ID..."
    
    // Run the NGINX container in detached mode
    def containerId = sh(
        script: "docker run -d --name nginx_test nginx:latest",
        returnStdout: true
    ).trim()
    
    echo "NGINX container started with ID: ${containerId}"
    return containerId
}