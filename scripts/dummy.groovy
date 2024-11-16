def getNginxContainerId(Map args) {
    echo "Starting NGINX container and retrieving container ID..."

    // Step 1: Run the NGINX container
    def containerId = sh(
        script: "docker run -d --name nginx_test nginx:latest",
        returnStdout: true
    ).trim()

    echo "NGINX container started with ID: ${containerId}"

    // Step 2: Return the container ID
    return containerId
}
