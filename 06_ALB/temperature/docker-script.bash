# 1. Set environment variables for the Temperature API
export DOCKER_USERNAME="shartazfeeham"
export APP_IMAGE_NAME="temperature-api"
export TAG="latest"

# 2. Build the Docker Image using buildx for multi-platform support
echo "Building multi-arch image: $DOCKER_USERNAME/$APP_IMAGE_NAME:$TAG"

# Fix: Use docker buildx build with the --platform and --push flags
# This creates a manifest that works on both Mac (ARM64) and EC2 (AMD64)
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t $DOCKER_USERNAME/$APP_IMAGE_NAME:$TAG \
  --push .

# 3. Log in to Docker Hub (No change, but ensure you are logged in)
echo "Logging into Docker Hub..."
docker login

# 4. Push the Image to Docker Hub
# Note: The push is handled in step 2 by the '--push' flag, but we keep this for consistency.
echo "Pushing image to Docker Hub (Handled by buildx)..."
# docker push $DOCKER_USERNAME/$APP_IMAGE_NAME:$TAG  # No longer strictly necessary after buildx --push

# 5. Run the container locally (Port 8010)
echo "Running the container locally on port 8010..."
docker run -d -p 8010:8010 --name temperature-service $DOCKER_USERNAME/$APP_IMAGE_NAME:$TAG

# 6. Test the running container with the new endpoint
echo "Test command (endpoint is /api/current-temperature):"
curl -X GET http://localhost:8010/api/current-temperature