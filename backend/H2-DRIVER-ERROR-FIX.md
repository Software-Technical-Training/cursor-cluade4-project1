# Fix for "Driver org.h2.Driver is not suitable" Error

## The Problem
You're seeing this error because the H2 console is using the wrong saved settings profile.

## Quick Fix

### Step 1: At the H2 Console Login Page
1. Look for the **"Saved Settings"** dropdown at the top
2. It's probably set to "Generic H2 (Server)" or something else
3. **Change it to: `Generic H2 (Embedded)`**

### Step 2: Verify Auto-Populated Values
After selecting "Generic H2 (Embedded)", the fields should auto-populate to:
- **Driver Class**: `org.h2.Driver`
- **JDBC URL**: `jdbc:h2:mem:testdb` 
- **User Name**: `sa`
- **Password**: (empty)

### Step 3: Connect
Click the "Connect" button.

## Alternative Method: Manual Entry

If the dropdown doesn't work, clear all fields and enter manually:

1. **Clear the "Saved Settings" dropdown** (select the blank option)
2. Enter these exact values:
   ```
   Driver Class: org.h2.Driver
   JDBC URL: jdbc:h2:mem:testdb
   User Name: sa
   Password: 
   ```
3. Click "Test Connection" first
4. If successful, click "Connect"

## Why This Happens

The H2 console has different saved settings for different connection modes:
- **Generic H2 (Server)** - For H2 running as a separate server process
- **Generic H2 (Embedded)** - For H2 running embedded in your application (← You need this!)
- **Generic H2 (In-Memory)** - Sometimes available, also works

Your Spring Boot app uses H2 in **embedded mode** with an **in-memory database**, so you must use the embedded driver settings.

## Still Not Working?

Try accessing the console with this direct URL that includes the JDBC URL as a parameter:
```
http://localhost:8080/h2-console?jsessionid=&settings=Generic+H2+(Embedded)&setting=Generic+H2+(Embedded)&name=Generic+H2+(Embedded)&driver=org.h2.Driver&url=jdbc:h2:mem:testdb&user=sa&password=
```

## Using External Database Tools

If you're using IntelliJ IDEA, DBeaver, or other database tools:

### IntelliJ IDEA:
1. Add Data Source → H2
2. Connection type: **Embedded**
3. Connection mode: **In-memory**
4. Database: `testdb`
5. User: `sa`

### DBeaver:
1. New Connection → H2 Embedded
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Username: `sa`
4. Leave password empty

## Verify H2 Version
Your project uses H2 version 2.1.214. Make sure any external tool supports this version. 