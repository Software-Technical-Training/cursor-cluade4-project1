# Cloud Run Deployment Instructions

Your Docker image has been successfully pushed to Artifact Registry! Now let's deploy it to Cloud Run.

## Image Details
- **Image URL**: `us-west2-docker.pkg.dev/grocery-automation-467922/grocery-automation-backend/backend:latest`
- **Registry Location**: us-west2
- **Project**: grocery-automation-467922

## Deployment Steps

### Option 1: Deploy via Google Cloud Console (Recommended)

1. **Go to Cloud Run**
   - Navigate to [Cloud Run Console](https://console.cloud.google.com/run)
   - Make sure you're in the correct project: `grocery-automation-467922`

2. **Create Service**
   - Click **"CREATE SERVICE"**
   - Select **"Deploy one revision from an existing container image"**
   - Click **"SELECT"** and navigate to:
     - Artifact Registry → us-west2 → grocery-automation-backend → backend → latest
   - Or paste: `us-west2-docker.pkg.dev/grocery-automation-467922/grocery-automation-backend/backend:latest`

3. **Configure Service**
   - **Service name**: `grocery-automation-backend`
   - **Region**: `us-west2` (same as your Artifact Registry)
   - **CPU allocation**: Select "CPU is always allocated"
   - **Autoscaling**:
     - Minimum instances: 1
     - Maximum instances: 10
   - **Authentication**: 
     - ✅ **Allow unauthenticated invocations** (for now, to test the API)

4. **Container Configuration** (Click "Container, Networking, Security")
   - **Container port**: 8080
   - **Memory**: 2 GiB
   - **CPU**: 2
   - **Environment Variables** (Add these):
     - `SPRING_PROFILES_ACTIVE` = `cloud`
     - `PORT` = `8080`
   - **Service account**: Select `grocery-app-deployer@grocery-automation-467922.iam.gserviceaccount.com`

5. **Deploy**
   - Click **"CREATE"**
   - Wait for deployment (usually takes 1-2 minutes)

### Option 2: Deploy using gcloud (if you have it installed)

```bash
gcloud run deploy grocery-automation-backend \
  --image=us-west2-docker.pkg.dev/grocery-automation-467922/grocery-automation-backend/backend:latest \
  --region=us-west2 \
  --platform=managed \
  --allow-unauthenticated \
  --port=8080 \
  --memory=2Gi \
  --cpu=2 \
  --min-instances=1 \
  --max-instances=10 \
  --set-env-vars="SPRING_PROFILES_ACTIVE=cloud,PORT=8080" \
  --service-account=grocery-app-deployer@grocery-automation-467922.iam.gserviceaccount.com
```

## After Deployment

Once deployed, Cloud Run will provide you with a public URL like:
`https://grocery-automation-backend-xxxxx-uw.a.run.app`

You can then:
1. Access the Swagger UI at: `https://your-cloud-run-url/swagger-ui/index.html`
2. Test the health endpoint: `https://your-cloud-run-url/actuator/health`
3. Start making API calls to your backend

## Security Note

Currently, we're allowing unauthenticated access for testing. In production, you should:
1. Enable authentication
2. Use API keys or OAuth2
3. Configure CORS for your frontend domain

## Next Steps

After successful deployment:
1. Set up Cloud Build for automatic deployments from GitHub
2. Configure a custom domain (optional)
3. Set up monitoring and logging
4. Migrate from H2 to Cloud SQL for production database