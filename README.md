# Agrub

Agrub is an integrated system for monitoring and managing security incidents and IT infrastructure failures. The project consists of a web application (administrative panel) and integrates [...]

## System Requirements

Before running the project, make sure the following tools are installed on your system:

- Git
- Docker with Docker Compose

## Technology Stack

The system was designed and implemented using the following technologies:

- Backend: Java with Spring Boot framework support (process management, task scheduling, and webhook handling).
- Frontend web application: JavaScript / TypeScript based on Vue.js (dynamic administrator panel with real-time event streaming support).
- Database and cache: MySQL 8.0 as the main relational data store and Redis as a key-value database for fast caching and session management.
- Containerization: Docker and Docker Compose to ensure complete isolation, reproducibility, and easy deployment of the entire environment.

## Environment Structure (Docker Compose)

The entire application stack is run automatically within a defined container structure, divided into layers:

### Application Layers

- alert-backend: Main Spring Boot application container responsible for business logic, authorization, and alert reception.
- alert-frontend: Nginx server serving the Vue.js client application.
- mysql-app: MySQL relational database storing user configuration, groups, and incident history.
- redis-cache: Fast Redis cache optimizing queries and backend performance.

## Installation and Launch

1. Clone the project repository:

```bash
git clone https://github.com/PatriStelmach/Agrub.git
cd Agrub
```

2. Run the containers:
```bash
docker compose up -d
```

## Functionality Description

### 1. Flexible Authentication

The system enables user login in two independent ways:
- Classic authorization based on the local MySQL database of the Agrub system.
- Integration with Active Directory / LDAP directory services (domain user mapping).

### 2. Support for External Systems (Wazuh, Zabbix, Nagios)

Agrub acts as a central aggregation point for leading monitoring and security platforms. From the panel, it is possible to:
- Wazuh: Receive security alerts (SIEM) and verify operational logs from agents.
- Zabbix & Nagios: Monitor infrastructure status, automatic problem synchronization, and the ability to directly approve incidents (ACK) or close tickets without the need [...]

### 3. Custom Plugins

The ability to extend system capabilities by running custom scripts and plugins that respond to incidents or execute recurring tasks. The system supports languages:
- Bash
- Python
- PowerShell

### 4. Access Control and Permission Management

- User Groups: The ability to create group structures for precise regulation of permission levels and alert visibility for individual teams (e.g., SOC, Network, DevOps [...]
- API Keys: Generation of dedicated authentication tokens enabling integration of external systems through the webhook mechanism.

### 5. Advanced Charts and Analytics

The panel provides a set of metrics to facilitate analysis of team efficiency:
- Total number of registered alerts.
- Total number of alerts from a selected time period broken down by severity levels.
- Average time required to take (approve) an alert over the selected time period.
- Average time required to fully resolve and close a ticket during the selected time period.
- Comparative summary of response time to full incident resolution time during the selected time period.

### 6. Central Operations Panel and Event Audit

The system offers advanced incident review modules and complete tracking of operator actions in real time:
- Active alerts panel: View showing current, unresolved failures and security breaches flowing from connected systems. Enables immediate response, assignment [...]
- Alert history: Complete archive of all events that have been closed or resolved. Allows filtering and searching historical anomalies for analytical purposes.
- Alert action history: Detailed event log for each individual alert. Records the exact lifecycle of a ticket – from its detection, through status changes, assignment [...]
- User action history: Audit module recording the activity of individual users and administrators in the system. Allows verification of who did what actions and when in the s[...]

## System Configuration

All key environmental parameters are managed from the settings section in the application.

### Security Settings
- Password lifetime (days): default 90
- Access token lifetime (minutes): default 1440
- Refresh token lifetime (hours): default 168

### Active Directory / LDAP Configuration

- Active Directory Domain
- Active Directory Server URL
- LDAP Base Distinguished Name (Base DN)
- LDAP User Distinguished Name Template (User DN Pattern)

### Email Notification Settings (SMTP)

- SMTP Server
- SMTP Port
- SMTP User
- SMTP Service Status (Enabled/Disabled)

### Notification Settings
- External systems synchronization frequency (seconds): 60
- Maximum execution time for custom scripts (seconds): 30

## Authors
- Mateusz Andrzejak
- Patri Stelmach
- Kamil Kornatowski
- Błażej Majchrzak

# Integration Guide for Alert System with External Systems

## Preparing Authorization Keys

- To ensure proper communication between external systems and the Alert system, it is necessary to create API keys.
- Go to the **API Keys** tab, generate a new key (e.g., "Zabbix Webhook") and copy it; it is recommended to create a separate key for each use case.

# Zabbix Integration with AlertWIP Platform

To enable proper system communication, we need to configure active reading from the AlertWIP side (**Pull Mode**) and instant notification sending by Zabbix (**Push Mode**).

## Step 1: Prepare Authorization Key

*  Log in to the Zabbix administrator account, go to **Users -> API tokens**, create a new token and copy its value.

## Step 2: Configure Connection in AlertWIP (Pull Mode)

This allows AlertWIP to query Zabbix for alert status and close them when the problem is resolved.

1. In the **AlertWIP** panel, go to **System Settings**.
2. Select the ZABBIX field
3. Update the following keys:
   - `enabled`: `true`
   - `url`: `http://<ZABBIX_IP>:<PORT>/api_jsonrpc.php`
   - `New API key`: `<paste the token generated in Zabbix in Step 1>`

## Step 3: Configure Webhook in Zabbix (Push Mode)

1. In the **Zabbix** panel, go to **Administration -> Media types**.
2. Click **Create media type**.
3. Fill in the basic information:
   - **Name:** `AlertWIP Webhook`
   - **Type:** `Webhook`
4. In the **Parameters** section, define macro mapping:

| Parameter Name | Value                                                  |
| :------------- | :----------------------------------------------------- |
| `eventid`      | `{EVENT.ID}`                                           |
| `host`         | `{HOST.NAME}`                                          |
| `name`         | `{EVENT.NAME}`                                         |
| `severity`     | `{EVENT.NSEVERITY}`                                    |
| `status`       | `{EVENT.STATUS}`                                       |
| `URL`          | `http://<AGRUB_IP>:<PORT>/api/webhooks/zabbix/alert`   |
| `ApiKey`       | `<key_generated_in_Agrub>`                             |

5. In the **Script** field, paste the following code:

```javascript
try {
    var params = JSON.parse(value),
        req = new HttpRequest(),
        response;

    req.addHeader('Content-Type: application/json');
    req.addHeader('X-API-KEY: ' + params.ApiKey);
    
    response = req.post(params.URL, JSON.stringify(params));

    if (req.getStatus() !== 200) {
        throw 'Response code: ' + req.getStatus();
    }

    return 'OK';
} catch (error) {
    Zabbix.log(3, 'Spring Webhook failed: ' + error);
    throw 'Failed with error: ' + error;
}
```

6. At the top, click the Message templates tab.
7. Click Add and in the Message field paste:
```json
{
"eventid": "{EVENT.ID}",
"host": "{HOST.NAME}",
"name": "{EVENT.NAME}",
"severity": "{EVENT.NSEVERITY}",
"status": "{EVENT.STATUS}"
}
```

## Step 4: Assign Webhook to Actions in Zabbix

1. Go to **Users -> Users**.
2. Select a user (or create a dedicated "Agrub User") to whom notifications are attached.
3. Go to the Media tab, click Add and select the Agrub Webhook created earlier as Type. In the sendto field, enter your chosen name. Save the changes.
4. Go to Alerts -> Actions -> Trigger actions.
5. Select Create action and name it.
6. In the Operations field, click "Add" and select the user configured in the previous steps, then check Send only to Agrub-Webhook.
7. Repeat these same steps for Recovery Operations.

# Wazuh Integration with AlertWIP Platform

- Due to Wazuh operating as a system that reports active events rather than active monitoring with problems and recovery, integration with the Wazuh system works only in PULL mode [...]
- Fields:
	- Enabled
	- URL
	- Username
  are currently unused

## Step 1: Create Integration Script

On the server (inside the Wazuh container), create a file in the integrations directory:

```Bash
nano /var/ossec/integrations/custom-alertwip
```

And paste into it:

```python
#!/var/ossec/framework/python/bin/python3

import sys
import json
import urllib.request


alert_file = sys.argv[1]
url = "http://<YOUR_AGRUB_IP>:<PORT>/api/webhooks/wazuh"
api_key = "<YOUR_API_KEY>"

with open(alert_file) as f:
    alert = json.load(f)

rule_desc = alert.get("rule", {}).get("description", "Unknown Alert")
level = alert.get("rule", {}).get("level", 0)
agent_name = alert.get("agent", {}).get("name", "Unknown Agent")
full_log = alert.get("full_log", "No Details")
event_id = alert.get("id", "0")

payload = {
    "externalEventId": event_id,
    "name": f"{agent_name}: {rule_desc}",
    "message": full_log,
    "status": "PROBLEM",
    "severity": level,
    "host": agent_name
}

req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers={'Content-Type': 'application/json', 'X-API-KEY': api_key})

try:
    urllib.request.urlopen(req)
except Exception as e:
    with open("/var/ossec/logs/integrations.log", "a") as log:
        log.write(f"AlertWIP Integration error: {str(e)}\n")
```

Key step: Grant the script execution permissions and appropriate owner:

```Bash
chmod 750 /var/ossec/integrations/custom-alertwip
chown root:ossec /var/ossec/integrations/custom-alertwip
```

## Step 2: Wazuh Configuration (ossec.conf)

Add the following section to the file /var/ossec/etc/ossec.conf (inside the <ossec_config> block):

```XML
<integration>
    <name>custom-alertwip</name>
    <level>5</level>
    <hook_url>http://<AGRUB_IP>:<PORT>/api/webhooks/wazuh</hook_url>
    <alert_format>json</alert_format>
</integration>
```

## Step 3: Restart Services

To apply the changes, restart the Wazuh manager:

```Bash
systemctl restart wazuh-manager
```

# Nagios Integration with AlertWIP

The following instructions configure an integration that sends alerts from Nagios.

## Step 1: Create Sending Script

Create a file `/opt/nagios/libexec/agrub_webhook.sh` and paste the following content:

```Bash
#!/bin/bash
API_URL="http://<AGRUB_IP>:<PORT>/api/webhooks/nagios"
API_KEY="<YOUR_API_KEY>"

NOTIFICATIONTYPE=$1
HOSTNAME=$2
SERVICEDESC=$3
SERVICESTATE=$4
SERVICEOUTPUT=$5

if [ "$NOTIFICATIONTYPE" == "RECOVERY" ] || [ "$SERVICESTATE" == "OK" ] || [ "$SERVICESTATE" == "UP" ]; then
    FINAL_STATUS="RESOLVED"
else
    FINAL_STATUS="PROBLEM"
fi

JSON_PAYLOAD=$(cat <<EOF
{
  "externalEventId": "$SERVICEDESC",
  "name": "$HOSTNAME: $SERVICEDESC",
  "message": "$SERVICEOUTPUT",
  "status": "$FINAL_STATUS",
  "severity": "$SERVICESTATE",
  "host": "$HOSTNAME"
}
EOF
)

curl -s -o /dev/null -w "%{http_code}" -X POST "$API_URL" \
     -H "Content-Type: application/json" \
     -H "X-API-KEY: $API_KEY" \
     -d "$JSON_PAYLOAD"
```

Remember to grant execution permissions: `chmod +x /opt/nagios/libexec/agrub_webhook.sh`

## Step 2: Command Definition

Add the following definitions to the file `/opt/nagios/etc/objects/commands.cfg`:

```Bash
# Service alert command
define command {
    command_name    notify-service-by-agrub
    command_line    /opt/nagios/libexec/agrub_webhook.sh "$NOTIFICATIONTYPE$" "$HOSTNAME$" "$SERVICEDESC$" "$SERVICESTATE$" "$SERVICEOUTPUT$"
}

# Host alert command
define command {
    command_name    notify-host-by-agrub
    command_line    /opt/nagios/libexec/agrub_webhook.sh "$NOTIFICATIONTYPE$" "$HOSTNAME$" "HOST DOWN" "$HOSTSTATE$" "$HOSTOUTPUT$"
}
```

## Step 3: Contact Configuration

Add an integration user in `/opt/nagios/etc/objects/contacts.cfg`:

```Bash
define contact {
    contact_name                    agrub_api
    use                             generic-contact
    alias                           Agrub Backend Integration
    service_notification_commands   notify-service-by-agrub
    host_notification_commands      notify-host-by-agrub
    email                           unused@unused.com
}
```
