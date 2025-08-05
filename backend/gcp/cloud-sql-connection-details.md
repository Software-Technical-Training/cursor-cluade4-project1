# Cloud SQL Connection Details

## Instance Information
- **Instance Name**: grocery-automation-db
- **Connection Name**: `grocery-automation-467922:us-west2:grocery-automation-db`
- **Database Version**: PostgreSQL 17.5
- **Region**: us-west2-c
- **Machine Type**: 1 vCPU, 1.7 GB Memory (Enterprise edition)
- **Storage**: 10 GB SSD with auto-increase enabled

## Connection Settings
- **Public IP**: Enabled
- **Private IP**: Disabled
- **Default Port**: 5432
- **Database Name**: grocery_automation (to be created)

## Backup Configuration
- **Backup Tier**: Standard
- **Automated backups**: Enabled
- **Point-in-time recovery**: Enabled
- **Backup retention after deletion**: Enabled
- **Instance deletion prevention**: Enabled

## Security
- **Username**: postgres (default)
- **Password**: [Saved by user]

## Environment Variables for Cloud Run
These will be used when configuring Cloud Run:
```
DB_NAME=grocery_automation
DB_USER=postgres
DB_PASSWORD=[your-saved-password]
CLOUD_SQL_INSTANCE=grocery-automation-467922:us-west2:grocery-automation-db
```

## Notes
- Instance is in Enterprise edition with good backup/recovery features
- Located in us-west2-c (same region as Cloud Run for low latency)
- Vertex AI Integration is disabled (not needed for this project)