#!/bin/bash

# Build and Push Multi-Platform Docker Image for Cloud Run
# This script builds for both AMD64 and ARM64 architectures

# Set variables
PROJECT_ID="grocery-automation-467922"
REGION="us-west2"
IMAGE_NAME="grocery-automation-backend"
REGISTRY_PATH="${REGION}-docker.pkg.dev/${PROJECT_ID}/${IMAGE_NAME}"
REMOTE_TAG="${REGISTRY_PATH}/backend:latest"

echo "🚀 Building multi-platform Docker image for Cloud Run"
echo "Target architectures: linux/amd64, linux/arm64"

# Ensure Docker is in PATH
export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"

# Step 1: Create and use a new builder that supports multi-platform builds
echo ""
echo "Step 1: Setting up Docker buildx for multi-platform builds..."
docker buildx create --name multiplatform-builder --use 2>/dev/null || docker buildx use multiplatform-builder

# Ensure the builder is bootstrapped
docker buildx inspect --bootstrap

# Step 2: Build and push multi-platform image
echo ""
echo "Step 2: Building and pushing multi-platform image..."
echo "This may take a few minutes as it builds for multiple architectures..."

docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --tag $REMOTE_TAG \
  --push \
  .

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Multi-platform image successfully built and pushed!"
    echo "Image URL: $REMOTE_TAG"
    echo ""
    echo "The image now supports both:"
    echo "- linux/amd64 (for Cloud Run)"
    echo "- linux/arm64 (for Apple Silicon)"
    echo ""
    echo "You can now deploy this image to Cloud Run!"
else
    echo "❌ Failed to build multi-platform image!"
    exit 1
fi