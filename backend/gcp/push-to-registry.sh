#!/bin/bash

# Push Docker Image to Google Artifact Registry
# This script tags and pushes the Docker image without requiring gcloud

# Set variables
PROJECT_ID="grocery-automation-467922"
REGION="${1:-us-west2}"  # Default to us-west2 if not provided
IMAGE_NAME="grocery-automation-backend"
LOCAL_TAG="grocery-automation-backend:local"

# Construct the full registry path
REGISTRY_PATH="${REGION}-docker.pkg.dev/${PROJECT_ID}/${IMAGE_NAME}"
REMOTE_TAG="${REGISTRY_PATH}/backend:latest"

echo "🚀 Pushing Docker image to Google Artifact Registry"
echo "Registry: $REGISTRY_PATH"

# Ensure Docker is in PATH
export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"

# Step 1: Authenticate (if not already authenticated)
echo "Step 1: Authenticating with Artifact Registry..."
cd "$(dirname "$0")" && ./docker-auth-helper.sh $REGION

# Step 2: Tag the image
echo ""
echo "Step 2: Tagging image for Artifact Registry..."
docker tag $LOCAL_TAG $REMOTE_TAG

if [ $? -ne 0 ]; then
    echo "❌ Failed to tag image!"
    exit 1
fi

# Step 3: Push the image
echo ""
echo "Step 3: Pushing image to registry..."
docker push $REMOTE_TAG

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Image successfully pushed to Artifact Registry!"
    echo "Image URL: $REMOTE_TAG"
    echo ""
    echo "You can now deploy this image to Cloud Run using:"
    echo "- Image: $REMOTE_TAG"
    echo "- Service name: grocery-automation-backend"
    echo "- Region: $REGION"
else
    echo "❌ Failed to push image!"
    exit 1
fi