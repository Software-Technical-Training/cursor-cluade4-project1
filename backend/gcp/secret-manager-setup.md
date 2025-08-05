# Setting Up Google Secret Manager for Database Password

This guide will help you store the database password securely and access it from both Cloud Build and Cloud Run.

## Step 1: Enable Secret Manager API

1. Go to [Secret Manager API](https://console.cloud.google.com/apis/library/secretmanager.googleapis.com?project=grocery-automation-467922)
2. Click **"ENABLE"**

## Step 2: Create the Secret

### Via Console:
1. Go to [Secret Manager](https://console.cloud.google.com/security/secret-manager?project=grocery-automation-467922)
2. Click **"CREATE SECRET"**
3. Configure:
   - **Name**: `db-password`
   - **Secret value**: `sNhZ8-/vFo:>gfAR`
   - **Regions**: Leave as "Automatic"
4. Click **"CREATE SECRET"**

### Via Command Line:
```bash
echo -n "sNhZ8-/vFo:>gfAR" | gcloud secrets create db-password \
  --data-file=- \
  --project=grocery-automation-467922
```

## Step 3: Grant Access to Service Accounts

### A. For Cloud Run Service Account:
```bash
# Grant access to the Cloud Run service account
gcloud secrets add-iam-policy-binding db-password \
  --member="serviceAccount:grocery-app-deployer@grocery-automation-467922.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor" \
  --project=grocery-automation-467922
```

### B. For Cloud Build Service Account:
```bash
# Get the Cloud Build service account
PROJECT_NUMBER=$(gcloud projects describe grocery-automation-467922 --format="value(projectNumber)")

# Grant access to Cloud Build
gcloud secrets add-iam-policy-binding db-password \
  --member="serviceAccount:${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor" \
  --project=grocery-automation-467922
```

## Step 4: Update Cloud Run to Use Secret

### Option A: Via Console (Easier)
1. Go to [Cloud Run](https://console.cloud.google.com/run?project=grocery-automation-467922)
2. Click on `grocery-automation-backend`
3. Click **"EDIT & DEPLOY NEW REVISION"**
4. Go to **"Variables & Secrets"** tab
5. Under **"Secret"**, click **"REFERENCE A SECRET"**
6. Configure:
   - **Secret**: `db-password`
   - **Reference method**: "Exposed as environment variable"
   - **Environment variable name**: `DB_PASSWORD`
   - **Version**: "latest"
7. **Remove** the plain text `DB_PASSWORD` from Environment Variables
8. Click **"DEPLOY"**

### Option B: Via Command Line
```bash
gcloud run services update grocery-automation-backend \
  --update-secrets=DB_PASSWORD=db-password:latest \
  --region=us-west2 \
  --project=grocery-automation-467922
```

## Step 5: Update cloudbuild.yaml

Replace the hardcoded password with secret reference:

```yaml
# In the deploy step, change from:
- '--set-env-vars=SPRING_PROFILES_ACTIVE=cloudsql,DB_NAME=grocery_automation,DB_USER=postgres,DB_PASSWORD=${_DB_PASSWORD},CLOUD_SQL_INSTANCE=...'

# To:
- '--set-env-vars=SPRING_PROFILES_ACTIVE=cloudsql,DB_NAME=grocery_automation,DB_USER=postgres,CLOUD_SQL_INSTANCE=grocery-automation-467922:us-west2:grocery-automation-db'
- '--update-secrets=DB_PASSWORD=db-password:latest'
```

## Step 6: Remove Password from Cloud Build Trigger

1. Go to [Cloud Build Triggers](https://console.cloud.google.com/cloud-build/triggers?project=grocery-automation-467922)
2. Edit your trigger
3. Remove the `_DB_PASSWORD` substitution variable (if you added it)
4. Save

## Benefits of This Approach:

✅ **Security**: Password is encrypted at rest
✅ **Audit Trail**: All access is logged
✅ **Version Control**: Can update password without code changes
✅ **Access Control**: Fine-grained permissions
✅ **Works Everywhere**: Both automated and manual deployments

## Testing:

After setup:
1. Manually trigger a build
2. Check that the deployment succeeds
3. Verify the app still connects to the database
4. Check Cloud Run to confirm the secret is referenced (not exposed)

## Future Password Changes:

To change the password:
1. Update it in Cloud SQL
2. Create a new version in Secret Manager
3. Cloud Run will automatically use the latest version