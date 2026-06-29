# AlertWIP

## Overview

AlertVIP for mobile is an application built in flutter for the purpose of monitoring and notifying about active server events for use with the AlertVIP server agent.

## Getting Started

### Prerequisites

- flutter
- dart

### Setup project

- Clone this repository using `git clone https://github.com/PatriStelmach/AlertVIP`
- cd into mobile_app
- `flutter pub` get to get all the dependencies
- Generate files using Builder Runner (required)
```
flutter pub run build_runner build
```
Make sure you have a connected Android/iOS device/simulator and run the following command to build and run the app in debug mode.

```
flutter run
```

## Features:

### Alerts:
- Active alert monitoring
- Alert History viewing with sorting
- ACKing and commenting alerts
- Sound notifying on highest alert levels

### Scripts:
- Manual script running
- Modifying scheduled scripts

### Profiles:
- Viewing your profile 

### Multi language support:
- Polish 
- English


## Contributors

- Patri Stelmach
- Kamil Kornatowski
- Błażej Majchrzak


# Przygotowanie kluczy autoryzacyjnych

- Do poprawnej komunikacji systemów zewnętrznych z systemem Alert, wymagane jest utworzenie kluczy API.
- Przejdź do zakładki **Klucze API**, wygeneruj nowy klucz (np. "Zabbix Webhook") i skopiuj go, zalecane jest stworzenie osobnego klucza dla każdego użytku
# Integracja Zabbix z platformą AlertWIP

Aby systemy mogły się poprawnie komunikować, musimy skonfigurować aktywny odczyt z poziomu AlertWIP (**Tryb Pull**) oraz natychmiastowe wysyłanie powiadomień przez Zabbixa (**Tryb Push**).

## Krok 1: Przygotowanie klucza autoryzacyjnego

*  Zaloguj się na konto administratora, przejdź do **Administration -> General -> API tokens**, utwórz nowy token i skopiuj jego wartość.

## Krok 2: Konfiguracja połączenia w AlertWIP (Tryb Pull)

Dzięki temu AlertWIP będzie w stanie odpytywać Zabbixa o status alertów i zamykać je, gdy problem zostanie rozwiązany.

1. W panelu **AlertWIP** przejdź do **Ustawień Systemowych**.
2. Wybierz pole ZABBIX
3. Zaktualizuj poniższe klucze:
   - `enabled`: `true`
   - `url`: `http://<IP_ZABBIXA>:<PORT>/api_jsonrpc.php`
   - `New API key`: `<wklej token wygenerowany w Zabbixie w Kroku 1>`

## Krok 3: Konfiguracja Webhooka w Zabbix (Tryb Push)

To kluczowy krok, który poinformuje AlertWIP o nowej awarii w ułamku sekundy po jej wystąpieniu.

1. W panelu **Zabbix** przejdź do **Administration -> Media types**.
2. Kliknij **Create media type**.
3. Wypełnij podstawowe dane:
   - **Name:** `AlertWIP Webhook`
   - **Type:** `Webhook`
4. W sekcji **Parameters** zdefiniuj mapowanie makr:

| Nazwa parametru | Wartość                                                |
| :-------------- | :----------------------------------------------------- |
| `eventid`       | `{EVENT.ID}`                                           |
| `host`          | `{HOST.NAME}`                                          |
| `name`          | `{EVENT.NAME}`                                         |
| `severity`      | `{EVENT.NSEVERITY}`                                    |
| `status`        | `{EVENT.STATUS}`                                       |
| `URL`           | `http://alert-backend:10000/api/webhooks/zabbix/alert` |
| `ApiKey`        | `<klucz_wygenerowany_w_AlertWIP>`                      |

5. W polu **Script** wklej poniższy kod:

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

6. Wejdź u góry w zakładkę Message templates.
7. Kliknij Add i w polu Message wklej:
```json
{
"eventid": "{EVENT.ID}",
"host": "{HOST.NAME}",
"name": "{EVENT.NAME}",
"severity": "{EVENT.NSEVERITY}",
"status": "{EVENT.STATUS}"
}
```

## Krok 4 Przypisanie webhooka do akcji w Zabbix

1. Przejdź do Users -> Users.
2. Wybierz użytkownika (lub stwórz dedykowanego "AlertWIP User"), do którego przypięte są powiadomienia.
3. Przejdź do zakładki Media, kliknij Add i jako Type wybierz utworzony przed chwilą AlertWIP Webhook. W polu sendto wpisz wybraną przez siebie nazwę. Zapisz zmiany.
4. Przejdź do Alerts -> Actions -> Trigger actions.
5. Wybierz Create action i nazwij ją
6. W polu Operations kliknij "Add" i wybierz usera skonfigurowanego w poprzednich krokach, po czym zaznacz Send only to AlertWIP-Webhook
7. Powtórz te same kroki dla Recovery Operations



# Integracja Wazuh z platformą AlertWIP

- Z uwagi na działanie systemu Wazuh jako system powiadamiający o aktywnych zdarzeniach, a nie aktywnego monitoringu z problemami i recovery, integracja z systemem Wazuh działa tylko w trybie PULL. 
- Pola:
	- Enabl
	- URL
	- Username
  są obecnie nieużywane
## Krok 1: Utworzenie skryptu integracyjnego
Na serwerze (wewnątrz kontenera Wazuha) utwórz plik w katalogu integracji:

```Bash
nano /var/ossec/integrations/custom-alertwip
```

I wklej do niego:

```python
#!/var/ossec/framework/python/bin/python3

import sys
import json
import urllib.request


alert_file = sys.argv[1]
url = "http://<YOUR_AGRUB_IP>:PORT/api/webhooks/wazuh"
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

Kluczowy krok: Nadaj skryptowi uprawnienia wykonywania i odpowiedniego właściciela:

```Bash
chmod 750 /var/ossec/integrations/custom-alertwip
chown root:ossec /var/ossec/integrations/custom-alertwip
```

## Krok 2: Konfiguracja Wazuha (ossec.conf)
Dodaj poniższą sekcję do pliku /var/ossec/etc/ossec.conf (wewnątrz bloku <ossec_config>):

```XML
<integration>
    <name>custom-alertwip</name>
    <level>5</level>
    <hook_url>http://alert-backend:10000/api/webhooks/wazuh</hook_url>
    <alert_format>json</alert_format>
</integration>
```

## Krok 3: Restart usług

Aby zmiany weszły w życie, zrestartuj menedżer Wazuha:

```Bash
systemctl restart wazuh-manager
```


# # Integracja Nagios z AlertWIP

Poniższa instrukcja konfiguruje integrację, która przesyła alerty z Nagiosa

## Krok 1: Utworzenie skryptu wysyłającego

Stwórz plik `/opt/nagios/libexec/alertwip_webhook.sh` i wklej poniższą treść:

```Bash
#!/bin/bash
API_URL="http://<YOUR_AGRUB_IP>:<PORT>/api/webhooks/nagios"
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

Pamiętaj o nadaniu uprawnień wykonywania: `chmod +x /opt/nagios/libexec/alertwip_webhook.sh`

## Krok 2: Definicja komend
Dodaj poniższe definicje do pliku `/opt/nagios/etc/objects/commands.cfg`:
```Bash
# Service alert command
define command {
    command_name    notify-service-by-alertwip
    command_line    /opt/nagios/libexec/alertwip_webhook.sh "$NOTIFICATIONTYPE$" "$HOSTNAME$" "$SERVICEDESC$" "$SERVICESTATE$" "$SERVICEOUTPUT$"
}

# Host alert command
define command {
    command_name    notify-host-by-alertwip
    command_line    /opt/nagios/libexec/alertwip_webhook.sh "$NOTIFICATIONTYPE$" "$HOSTNAME$" "HOST DOWN" "$HOSTSTATE$" "$HOSTOUTPUT$"
}
```

## Krok 3: Konfiguracja kontaktu
Dodaj użytkownika integracyjnego w `/opt/nagios/etc/objects/contacts.cfg`:
```Bash
define contact {
    contact_name                    alertwip_api
    use                             generic-contact
    alias                           AlertWIP Backend Integration
    service_notification_commands   notify-service-by-alertwip
    host_notification_commands      notify-host-by-alertwip
    email                           unused@unused.com
}
```


