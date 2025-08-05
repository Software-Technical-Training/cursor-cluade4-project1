# How to Connect to Cloud SQL PostgreSQL Database

## Option 1: Cloud Shell (Easiest - No Installation Required)

1. Go to [Cloud SQL Instance](https://console.cloud.google.com/sql/instances/grocery-automation-db/overview?project=grocery-automation-467922)
2. Click **"Connect using Cloud Shell"** button
3. Cloud Shell will open with a pre-configured command
4. Enter your password: `sNhZ8-/vFo:>gfAR`
5. You'll be connected to PostgreSQL

### SQL Commands to Check Data:
```sql
-- Connect to your database
\c grocery_automation

-- List all tables
\dt

-- View all users
SELECT * FROM users;

-- View specific user
SELECT * FROM users WHERE email = 'shyam.eranky@gmail.com';

-- Check table structure
\d users

-- Count users
SELECT COUNT(*) FROM users;

-- View other tables
SELECT * FROM grocery_items;
SELECT * FROM stores;
SELECT * FROM devices;
```

## Option 2: Cloud SQL Studio (Web-based Query Editor)

1. In Cloud SQL instance page, click **"Studio"** tab
2. Click **"Sign in with Google"**
3. Select database: `grocery_automation`
4. Run queries in the web interface

## Option 3: Local psql Client

### Install psql (if not installed):
```bash
# macOS
brew install postgresql

# Ubuntu/Debian
sudo apt-get install postgresql-client
```

### Connect using Cloud SQL Proxy:
1. Download Cloud SQL Proxy:
```bash
curl -o cloud-sql-proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.11.4/cloud-sql-proxy.darwin.amd64
chmod +x cloud-sql-proxy
```

2. Run the proxy:
```bash
./cloud-sql-proxy --port 5432 grocery-automation-467922:us-west2:grocery-automation-db
```

3. In another terminal, connect:
```bash
psql -h localhost -U postgres -d grocery_automation
# Enter password when prompted
```

## Option 4: Direct Connection (Less Secure)

1. Get your instance's public IP from Cloud SQL console
2. Add your IP to authorized networks:
   - Go to Cloud SQL instance
   - Click "Edit"
   - Under "Connections" → "Authorized networks"
   - Add your public IP
   - Save

3. Connect:
```bash
psql -h [INSTANCE_PUBLIC_IP] -U postgres -d grocery_automation
```

## Common Queries to Verify Data:

```sql
-- Check all users with details
SELECT id, name, email, phone, created_at, updated_at 
FROM users 
ORDER BY id;

-- Check if Shyam's data exists
SELECT * FROM users WHERE name LIKE '%Shyam%';

-- View database statistics
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Check all tables in the database
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';

-- Exit psql
\q
```

## Expected Results:

You should see:
- Table `users` with 2 records
- User ID 1: John Doe (seeded data)
- User ID 2: Shyam Eranky (your test data)
- Other tables: grocery_items, stores, devices, etc.

## Quick Test from Cloud Shell:

```bash
gcloud sql connect grocery-automation-db --user=postgres --database=grocery_automation
# Enter password
# Then run: SELECT * FROM users;
```