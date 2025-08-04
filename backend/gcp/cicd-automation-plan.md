# CI/CD Automation Plan for Grocery Automation Backend

## Overview
This plan outlines the steps to implement automated deployment to Google Cloud Run whenever code is pushed to the GitHub repository, and to migrate from H2 in-memory database to Cloud SQL.

## Part A: Automated Deployment Pipeline

### Step 1: Enable Required APIs and Set Up Service Account
1. Enable Cloud Build API in GCP Console
2. Grant necessary permissions to Cloud Build service account:
   - Cloud Run Admin
   - Service Account User
   - Artifact Registry Writer

### Step 2: Create Cloud Build Configuration
Create `cloudbuild.yaml` in the backend directory with the following stages:
1. Build Docker image
2. Push to Artifact Registry
3. Deploy to Cloud Run

### Step 3: Connect GitHub Repository
1. Set up Cloud Build GitHub App
2. Connect the repository: `Software-Technical-Training/cursor-cluade4-project1`
3. Create a trigger for the main branch

### Step 4: Configure Build Substitutions
- PROJECT_ID
- REGION
- SERVICE_NAME
- ARTIFACT_REGISTRY_REPO

## Part B: Database Migration to Cloud SQL

### Step 1: Create Cloud SQL Instance
1. Choose PostgreSQL 15
2. Configure:
   - Instance ID: `grocery-automation-db`
   - Region: us-west2 (same as Cloud Run)
   - Machine type: db-f1-micro (for POC)
   - Storage: 10GB SSD

### Step 2: Update Application Configuration
1. Add PostgreSQL driver dependency to pom.xml
2. Create new application properties for cloud database
3. Update entity configurations for PostgreSQL

### Step 3: Configure Cloud Run Connection
1. Enable Cloud SQL Admin API
2. Add Cloud SQL connection to Cloud Run service
3. Update environment variables with database credentials

### Step 4: Data Migration
1. Create database schema
2. Update DataSeeder for initial data
3. Test all endpoints with new database

## Implementation Order

### Phase 1: CI/CD Setup (Today)
1. Create cloudbuild.yaml
2. Set up service account permissions
3. Connect GitHub repository
4. Create build trigger
5. Test automated deployment

### Phase 2: Database Migration (Next Session)
1. Create Cloud SQL instance
2. Update application dependencies and configuration
3. Configure Cloud Run to Cloud SQL connection
4. Migrate and test

## Benefits
- **Automated Deployments**: Every push to main branch automatically deploys
- **Persistent Data**: Move from in-memory to persistent PostgreSQL database
- **Production Ready**: Proper CI/CD pipeline and database setup
- **Cost Efficient**: Using minimal resources for POC

## Estimated Time
- CI/CD Setup: 1-2 hours
- Database Migration: 2-3 hours

## Next Action
Start with creating the cloudbuild.yaml file and setting up the necessary permissions in GCP.