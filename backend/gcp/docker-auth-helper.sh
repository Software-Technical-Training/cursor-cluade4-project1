#!/bin/bash

# Docker Authentication Helper for Google Artifact Registry
# This script authenticates Docker with Artifact Registry using a service account key

# Check if service account key exists
if [ ! -f "grocery-automation-467922-a5fc9a566328.json" ]; then
    echo "Error: Service account key file not found!"
    echo "Please ensure the JSON key file is in the gcp directory"
    exit 1
fi

# Set variables
PROJECT_ID="grocery-automation-467922"
REGION="${1:-us-west2}"  # Default to us-west2 if not provided
KEY_FILE="grocery-automation-467922-a5fc9a566328.json"

echo "Authenticating Docker with Google Artifact Registry..."
echo "Project: $PROJECT_ID"
echo "Region: $REGION"

# Create Docker config directory if it doesn't exist
mkdir -p ~/.docker

# Use the service account key to generate an access token
# This uses Docker's credential helper
export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"

# Login to Artifact Registry using the service account key
cat $KEY_FILE | docker login -u _json_key --password-stdin https://${REGION}-docker.pkg.dev

if [ $? -eq 0 ]; then
    echo "✅ Docker authentication successful!"
    echo ""
    echo "You can now push images to:"
    echo "${REGION}-docker.pkg.dev/${PROJECT_ID}/grocery-automation-backend"
else
    echo "❌ Docker authentication failed!"
    exit 1
fi