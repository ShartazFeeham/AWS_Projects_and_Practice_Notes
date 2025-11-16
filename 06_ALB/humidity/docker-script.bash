# 1. Set environment variables
export DOCKER_USERNAME="shartazfeeham"
export APP_IMAGE_NAME="humidity-api"
export TAG="latest"

# 2. Build the Docker Image using buildx for multi-platform support
echo "Building multi-arch image: $DOCKER_USERNAME/$APP_IMAGE_NAME:$TAG"
# Fix: Use docker buildx build with the --platform and --push flags
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t $DOCKER_USERNAME/$APP_IMAGE_NAME:$TAG \
  --push .

# 3. Log in to Docker Hub (If needed)
echo "Logging into Docker Hub..."
docker login

# 4. Push the Image to Docker Hub (Handled by buildx)
echo "Pushing image to Docker Hub (Handled by buildx)..."

# 5. Run the container locally
echo "Running the container locally on port 8020..."
docker run -d -p 8020:8020 --name humidity-service $DOCKER_USERNAME/$APP_IMAGE_NAME:$TAG

# 6. Test the running container
echo "Test command:"
curl -X GET http://localhost:8020/api/current-humidity