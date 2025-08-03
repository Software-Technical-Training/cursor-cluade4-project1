# GCP Artifact Registry Setup Instructions

## Prerequisites
- Docker Desktop installed and running ✓
- GCP Project: grocery-automation-467922 ✓
- Service Account: grocery-app-deployer@grocery-automation-467922.iam.gserviceaccount.com ✓

## Step 1: Create Artifact Registry Repository

1. Go to [Google Cloud Console - Artifact Registry](https://console.cloud.google.com/artifacts)
2. Make sure you're in the correct project: `grocery-automation-467922`
3. Click **"+ CREATE REPOSITORY"**
4. Fill in the details:
   - **Name**: `grocery-automation-backend`
   - **Format**: Docker
   - **Mode**: Standard
   - **Location type**: Region
   - **Region**: Choose your preferred region (e.g., `us-central1`)
   - **Encryption**: Google-managed encryption key
5. Click **"CREATE"**

## Step 2: Configure Docker Authentication

After creating the repository, you'll need to configure Docker to authenticate with Artifact Registry.

1. In the Artifact Registry page, click on your newly created repository
2. Click on **"SETUP INSTRUCTIONS"** at the top
3. You'll see commands like:
   ```bash
   gcloud auth configure-docker us-central1-docker.pkg.dev
   ```

Since you don't have gcloud installed, we'll use an alternative approach:

## Step 3: Using Service Account for Authentication

We'll create a helper script to authenticate Docker with your service account:

1. Create a file called `docker-auth.sh` with the authentication script
2. Run it to configure Docker authentication

## Step 4: Tag and Push Docker Image

Once authenticated, we'll:
1. Tag your local image for Artifact Registry
2. Push it to the registry

## What I Need From You:

1. **Create the Artifact Registry repository** following Step 1 above
2. **Tell me the region you selected** (e.g., us-central1, us-east1, etc.)
3. Then I'll create the authentication script and help you push the image

## Note on gcloud SDK

While not required for this deployment, installing Google Cloud SDK would make many operations easier. You can install it later from:
https://cloud.google.com/sdk/docs/install

For now, we'll work around it using Docker authentication helpers and the Cloud Console.