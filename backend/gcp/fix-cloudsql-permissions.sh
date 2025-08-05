#!/bin/bash

# Fix Cloud SQL permissions for the service account

PROJECT_ID="grocery-automation-467922"
SERVICE_ACCOUNT="grocery-app-deployer@grocery-automation-467922.iam.gserviceaccount.com"

echo "🔧 Fixing Cloud SQL permissions for service account..."
echo "Service Account: $SERVICE_ACCOUNT"

# Grant Cloud SQL Client role
echo "Granting Cloud SQL Client role..."
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/cloudsql.client"

# Grant Cloud SQL Instance User role (additional permission)
echo "Granting Cloud SQL Instance User role..."
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/cloudsql.instanceUser"

# Grant Cloud SQL Viewer role (to see instances)
echo "Granting Cloud SQL Viewer role..."
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/cloudsql.viewer"

echo "✅ Permissions granted successfully!"
echo ""
echo "The service account now has:"
echo "- cloudsql.client (connect to instances)"
echo "- cloudsql.instanceUser (use instances)"
echo "- cloudsql.viewer (view instances)"
echo ""
echo "Please redeploy your Cloud Run service for the changes to take effect."