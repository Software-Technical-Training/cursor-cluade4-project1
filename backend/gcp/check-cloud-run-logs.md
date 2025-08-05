# How to Check Cloud Run Logs for Errors

## Step 1: Access Cloud Run Logs

1. Go to [Cloud Run Console](https://console.cloud.google.com/run?project=grocery-automation-467922)
2. Click on `grocery-automation-backend`
3. Click on the **"LOGS"** tab

## Step 2: Filter for the Failed Revision

1. Look for logs from revision `grocery-automation-backend-00003-txj`
2. Or click on the "REVISIONS" tab and find the failed revision
3. Click on it and then view its logs

## Common Database Connection Errors to Look For:

### 1. Password Authentication Failed
```
FATAL: password authentication failed for user "postgres"
```
**Fix**: Check that the DB_PASSWORD environment variable is set correctly

### 2. Database Does Not Exist
```
FATAL: database "grocery_automation" does not exist
```
**Fix**: Ensure the database was created in Cloud SQL

### 3. Cloud SQL Connection Failed
```
Could not connect to Cloud SQL instance
```
**Fix**: Ensure Cloud SQL connection is added in the Connections tab

### 4. Missing Cloud SQL Permissions
```
Permission denied to connect to Cloud SQL
```
**Fix**: Grant Cloud SQL Client role to the service account

### 5. Spring Profile Not Active
```
The following profiles are active: cloud
```
**Fix**: Should show "cloudsql" not "cloud"

## Quick Debugging Steps:

1. **Check Environment Variables**
   - Go to the failed revision
   - Click "YAML" view
   - Verify all these are present:
     - SPRING_PROFILES_ACTIVE=cloudsql
     - DB_NAME=grocery_automation
     - DB_USER=postgres
     - DB_PASSWORD=[your-password]
     - CLOUD_SQL_INSTANCE=grocery-automation-467922:us-west2:grocery-automation-db

2. **Check Cloud SQL Connection**
   - In YAML view, look for:
   ```yaml
   annotations:
     run.googleapis.com/cloudsql-instances: grocery-automation-467922:us-west2:grocery-automation-db
   ```

3. **Check Service Account Permissions**
   - Ensure `grocery-app-deployer@grocery-automation-467922.iam.gserviceaccount.com` has:
     - Cloud SQL Client role
     - Logs Writer role

## Most Likely Issues:

Based on the error, the most common causes are:
1. ❌ Password has special characters that need escaping
2. ❌ Cloud SQL instance connection not properly configured
3. ❌ Spring profile not loading the cloudsql properties
4. ❌ Database connection timeout too short