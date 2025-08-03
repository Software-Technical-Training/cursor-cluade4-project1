#!/bin/bash

# Build Docker Image specifically for Cloud Run (AMD64 only)
# This is a faster approach that builds only for the required architecture

# Set variables
PROJECT_ID="grocery-automation-467922"
REGION="us-west2"
IMAGE_NAME="grocery-automation-backend"
REGISTRY_PATH="${REGION}-docker.pkg.dev/${PROJECT_ID}/${IMAGE_NAME}"
REMOTE_TAG="${REGISTRY_PATH}/backend:latest"

echo "🚀 Building Docker image for Cloud Run (AMD64 architecture)"

# Ensure Docker is in PATH
export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"

# Step 1: Build for AMD64 architecture only
echo ""
echo "Step 1: Building Docker image for linux/amd64..."
# Build from the parent directory (backend) where the source code is
docker buildx build \
  --platform linux/amd64 \
  --tag $REMOTE_TAG \
  --load \
  -f gcp/Dockerfile \
  ..

if [ $? -ne 0 ]; then
    echo "❌ Failed to build Docker image!"
    exit 1
fi

# Step 2: Push the image
echo ""
echo "Step 2: Pushing image to Artifact Registry..."
docker push $REMOTE_TAG

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Image successfully built and pushed for Cloud Run!"
    echo "Image URL: $REMOTE_TAG"
    echo ""
    echo "Architecture: linux/amd64 (Cloud Run compatible)"
    echo ""
    echo "You can now deploy this image to Cloud Run!"
else
    echo "❌ Failed to push image!"
    exit 1
fi