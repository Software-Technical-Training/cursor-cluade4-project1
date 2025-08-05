# Manual Cloud Run Update for Cloud SQL Connection

Since gcloud CLI is not installed, follow these steps in the Google Cloud Console:

## Step 1: Go to Cloud Run
1. Open [Cloud Run Console](https://console.cloud.google.com/run?project=grocery-automation-467922)
2. Click on `grocery-automation-backend`

## Step 2: Edit & Deploy New Revision
1. Click **"EDIT & DEPLOY NEW REVISION"** button at the top

## Step 3: Variables & Secrets Tab
Click on the **"Variables & Secrets"** tab and add/update these environment variables:

| Variable Name | Value |
|--------------|-------|
| SPRING_PROFILES_ACTIVE | `cloudsql` |
| DB_NAME | `grocery_automation` |
| DB_USER | `postgres` |
| DB_PASSWORD | `sNhZ8-/vFo:>gfAR` |
| CLOUD_SQL_INSTANCE | `grocery-automation-467922:us-west2:grocery-automation-db` |

## Step 4: Connections Tab
1. Click on the **"Connections"** tab
2. Under **"Cloud SQL connections"**, click **"ADD CONNECTION"**
3. Select your Cloud SQL instance: `grocery-automation-db`
4. It should show: `grocery-automation-467922:us-west2:grocery-automation-db`

## Step 5: Container Tab (verify)
Ensure these settings remain:
- Container port: `8080`
- Memory: `2 GiB`
- CPU: `2`
- Min instances: `1`
- Max instances: `10`

## Step 6: Deploy
1. Scroll down and click **"DEPLOY"**
2. Wait for deployment to complete (1-2 minutes)

## Step 7: Test the Connection
Once deployed, test the database connection:

1. Get your service URL from Cloud Run
2. Visit: `https://[your-service-url]/actuator/health`
3. Look for the database status:
   ```json
   {
     "status": "UP",
     "components": {
       "db": {
         "status": "UP",
         "details": {
           "database": "PostgreSQL",
           "validationQuery": "isValid()"
         }
       }
     }
   }
   ```

## Success Indicators
- ✅ Health check shows `"db": {"status": "UP"}`
- ✅ No database connection errors in logs
- ✅ Application starts successfully
- ✅ API endpoints work as expected

## Troubleshooting
If the database connection fails:
1. Check Cloud Run logs for specific error messages
2. Verify the Cloud SQL instance is running
3. Ensure the service account has Cloud SQL Client role
4. Check that all environment variables are set correctly