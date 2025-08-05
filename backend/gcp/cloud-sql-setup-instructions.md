# Cloud SQL PostgreSQL Setup Instructions

This guide will help you set up Cloud SQL PostgreSQL for your Grocery Automation backend.

## Overview
We'll migrate from H2 in-memory database to Cloud SQL PostgreSQL for persistent data storage.

## Step 1: Create Cloud SQL Instance

### Via Console (Recommended for first-time setup):

1. Go to [Cloud SQL Instances](https://console.cloud.google.com/sql/instances?project=grocery-automation-467922)
2. Click **"CREATE INSTANCE"**
3. Choose **"PostgreSQL"**
4. Configure the instance:
   - **Instance ID**: `grocery-automation-db`
   - **Password**: Set a strong password for the postgres user (save this!)
   - **Database version**: PostgreSQL 15
   - **Region**: `us-west2` (same as Cloud Run)
   - **Zonal availability**: Single zone (for POC)

5. Configure machine type:
   - Click **"SHOW CONFIGURATION OPTIONS"**
   - **Machine type**: 
     - Click "Shared core"
     - Select `db-f1-micro` (1 vCPU, 0.6 GB - good for POC)
   - **Storage**:
     - Type: SSD
     - Capacity: 10 GB
     - Enable automatic storage increases: ✓

6. **Connections**:
   - **Public IP**: ✓ (We'll secure it)
   - **Private IP**: ✗ (requires VPC setup)
   - **Authorized networks**: Don't add any (Cloud Run will connect via Cloud SQL Proxy)

7. **Backups** (optional for POC):
   - Automated backups: You can disable for POC to save costs

8. Click **"CREATE INSTANCE"**

### Via gcloud CLI:
```bash
gcloud sql instances create grocery-automation-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-west2 \
  --network=default \
  --no-backup \
  --database-flags=max_connections=50
```

## Step 2: Create Database and User

After the instance is created (takes ~5 minutes):

1. Go to your instance in Cloud SQL console
2. Click on **"Databases"** tab
3. Click **"CREATE DATABASE"**
   - Name: `grocery_automation`
   - Character set: `UTF8`

4. Create application user (optional, more secure than using postgres):
   ```sql
   -- Connect to the instance first, then run:
   CREATE USER grocery_app WITH PASSWORD 'your-secure-password';
   GRANT ALL PRIVILEGES ON DATABASE grocery_automation TO grocery_app;
   ```

## Step 3: Get Connection Details

1. In Cloud SQL instance details, note:
   - **Connection name**: `grocery-automation-467922:us-west2:grocery-automation-db`
   - **Public IP address**: (shown in overview)

2. These will be used in your application configuration

## Step 4: Enable Cloud SQL Admin API

1. Go to [Cloud SQL Admin API](https://console.cloud.google.com/apis/library/sqladmin.googleapis.com)
2. Click **"ENABLE"** if not already enabled

## Step 5: Grant Cloud Run Access to Cloud SQL

The Cloud Run service account needs permission to connect to Cloud SQL:

```bash
# Grant Cloud SQL Client role to the service account
gcloud projects add-iam-policy-binding grocery-automation-467922 \
  --member="serviceAccount:grocery-app-deployer@grocery-automation-467922.iam.gserviceaccount.com" \
  --role="roles/cloudsql.client"
```

## Connection Information Summary

Save these for the next steps:
- **Instance Connection Name**: `grocery-automation-467922:us-west2:grocery-automation-db`
- **Database Name**: `grocery_automation`
- **Username**: `postgres` (or `grocery_app` if you created it)
- **Password**: (the one you set)
- **Port**: `5432` (PostgreSQL default)

## Next Steps

After creating the Cloud SQL instance:
1. Update application dependencies (add PostgreSQL driver)
2. Create new application properties for cloud database
3. Update Cloud Run service to connect to Cloud SQL
4. Test the connection

## Cost Optimization Tips

For POC/Development:
- Stop the instance when not in use: `gcloud sql instances patch grocery-automation-db --no-activation-policy`
- Start when needed: `gcloud sql instances patch grocery-automation-db --activation-policy=ALWAYS`
- Consider using db-f1-micro (lowest tier)
- Disable automated backups for POC