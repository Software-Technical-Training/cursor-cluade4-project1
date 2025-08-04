# Cloud Build Setup Instructions

This guide will help you set up automated deployments using Google Cloud Build.

## Prerequisites
- GCP Project: grocery-automation-467922
- GitHub Repository: Software-Technical-Training/cursor-cluade4-project1
- Cloud Build configuration: backend/gcp/cloudbuild.yaml

## Step 1: Enable Cloud Build API

1. Go to [Cloud Build API](https://console.cloud.google.com/apis/library/cloudbuild.googleapis.com)
2. Click "Enable"

## Step 2: Grant Permissions to Cloud Build Service Account

The Cloud Build service account needs permissions to deploy to Cloud Run and access Artifact Registry.

1. Go to [IAM & Admin](https://console.cloud.google.com/iam-admin/iam)
2. Find the Cloud Build service account: `[PROJECT_NUMBER]@cloudbuild.gserviceaccount.com`
3. Click "Edit" and add these roles:
   - **Cloud Run Admin** - to deploy services
   - **Service Account User** - to act as the service account
   - **Artifact Registry Writer** - to push images

Or run these commands:
```bash
PROJECT_ID="grocery-automation-467922"
PROJECT_NUMBER=$(gcloud projects describe $PROJECT_ID --format="value(projectNumber)")

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com" \
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com" \
  --role="roles/iam.serviceAccountUser"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com" \
  --role="roles/artifactregistry.writer"
```

## Step 3: Connect GitHub Repository

1. Go to [Cloud Build Triggers](https://console.cloud.google.com/cloud-build/triggers)
2. Click "Connect Repository"
3. Select "GitHub (Cloud Build GitHub App)"
4. Authenticate with GitHub
5. Select your repository: `Software-Technical-Training/cursor-cluade4-project1`
6. Click "Connect"

## Step 4: Create Build Trigger

1. After connecting the repository, click "Create Trigger"
2. Configure the trigger:
   - **Name**: `deploy-backend-on-push`
   - **Description**: "Deploy backend to Cloud Run on push to main"
   - **Event**: Push to a branch
   - **Source**: 
     - Repository: Your connected repo
     - Branch: `^main$`
   - **Configuration**:
     - Type: Cloud Build configuration file
     - Location: `/backend/gcp/cloudbuild.yaml`
   - **Substitution variables** (optional overrides):
     - Leave empty to use defaults from cloudbuild.yaml

3. Click "Create"

## Step 5: Test the Pipeline

1. Make a small change to your code
2. Commit and push to the main branch:
   ```bash
   git add .
   git commit -m "Test Cloud Build trigger"
   git push origin main
   ```

3. Monitor the build:
   - Go to [Cloud Build History](https://console.cloud.google.com/cloud-build/builds)
   - You should see a new build triggered
   - Click on it to see detailed logs

## Troubleshooting

### Build Fails with Permission Error
- Ensure Cloud Build service account has all required roles
- Check that the service account in cloudbuild.yaml exists

### Docker Build Fails
- Verify the Dockerfile path is correct: `backend/gcp/Dockerfile`
- Check that all source files are committed to Git

### Deployment Fails
- Verify Cloud Run service exists: `grocery-automation-backend`
- Check region matches: `us-west2`

## Success Indicators

When everything is working:
1. Pushing to main branch triggers a build automatically
2. Build completes successfully (green checkmark)
3. New Cloud Run revision is created
4. Your app is updated with the latest code

## Next Steps

After successful setup:
1. All future pushes to main will auto-deploy
2. You can see build history and logs in Cloud Build
3. Failed builds will not deploy (safety feature)
4. You can manually trigger builds from the console if needed