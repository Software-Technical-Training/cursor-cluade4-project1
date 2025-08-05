# Fix Cloud Run Deployment with Cloud SQL

## Issue: Password Special Characters

Your password contains special characters that need to be URL-encoded for the JDBC connection.

Original password: `sNhZ8-/vFo:>gfAR`
URL-encoded password: `sNhZ8-%2FvFo%3A%3EgfAR`

## Option 1: Update Environment Variable with Encoded Password

1. Go to [Cloud Run Console](https://console.cloud.google.com/run?project=grocery-automation-467922)
2. Click on `grocery-automation-backend`
3. Click **"EDIT & DEPLOY NEW REVISION"**
4. In **Variables & Secrets**, update:
   - `DB_PASSWORD` = `sNhZ8-%2FvFo%3A%3EgfAR` (encoded version)
5. Ensure these are also set:
   - `SPRING_PROFILES_ACTIVE` = `cloudsql`
   - `DB_NAME` = `grocery_automation`
   - `DB_USER` = `postgres`
   - `CLOUD_SQL_INSTANCE` = `grocery-automation-467922:us-west2:grocery-automation-db`
6. In **Connections** tab, ensure Cloud SQL instance is added
7. Deploy

## Option 2: Change Database Password (Simpler)

If encoding doesn't work, consider changing the password to something without special characters:

1. Go to [Cloud SQL Instance](https://console.cloud.google.com/sql/instances/grocery-automation-db/overview?project=grocery-automation-467922)
2. Click on **"Users"** tab
3. Click on `postgres` user
4. Click **"Change Password"**
5. Use a simpler password like: `GroceryAuto2024Secure`
6. Update Cloud Run with the new password

## Option 3: Grant Missing Permissions

If it's a permissions issue, run:
```bash
gcloud projects add-iam-policy-binding grocery-automation-467922 \
  --member="serviceAccount:grocery-app-deployer@grocery-automation-467922.iam.gserviceaccount.com" \
  --role="roles/cloudsql.client"
```

## Debugging Checklist:
- [ ] Check Cloud Run logs for specific error
- [ ] Verify all environment variables are set
- [ ] Confirm Cloud SQL connection is added
- [ ] Check service account has Cloud SQL Client role
- [ ] Try with encoded password
- [ ] Consider changing to simpler password