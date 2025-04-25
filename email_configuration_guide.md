# Gmail SMTP Configuration Guide for Triply

## Prerequisites
1. A Gmail account (triply@gmail.com)
2. Access to Gmail account settings

## Step 1: Enable Less Secure Apps OR Create App Password
### Option A: Enable Less Secure Apps (not recommended)
1. Go to: https://myaccount.google.com/lesssecureapps
2. Turn "Allow less secure apps" to ON

### Option B: Create App Password (recommended)
1. Enable 2FA on your Gmail account
2. Go to: https://myaccount.google.com/apppasswords
3. Select "Mail" as the app
4. Select "Other" as the device and enter "Triply"
5. Copy the generated 16-character password

## Step 2: Enable IMAP Access
1. Go to Gmail Settings → Forwarding and POP/IMAP
2. Enable IMAP access
3. Save changes

## Configuration Changes Needed

### application-dev.yml Updates
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: triply@gmail.com
    password: [your-password-or-app-password]
    properties:
      mail:
        smtp:
          auth: true
          starttls.enable: true
```

### EmailServiceImpl.java Updates
```java
message.setFrom("triply@gmail.com"); // Add this line before send()
```

## Testing
1. Send a test email
2. Check spam folder if not received
3. Verify "From" address appears correctly

## Troubleshooting
- If emails fail: Check Gmail security alerts
- If authentication fails: Verify password/app password
- If emails go to spam: Set up SPF/DKIM records

## Rollback
To revert to Mailtrap, simply uncomment the Mailtrap configuration and comment out the Gmail configuration.