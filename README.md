# Agrub

Agrub to zintegrowany system monitorowania i zarządzania incydentami bezpieczeństwa oraz awariami infrastruktury IT. Projekt składa się z aplikacji webowej (panelu administracyjnego) oraz powiązanej aplikacji mobilnej, które umożliwiają agregację alertów, zarządzanie nimi w czasie rzeczywistym oraz automatyzację reakcji na zdarzenia z poziomu jednego, spójnego interfejsu.

## Wymagania systemowe

Przed uruchomieniem projektu upewnij się, że w systemie zainstalowane są następujące narzędzia:

- Git
- Docker z Docker Compose

## Stos technologiczny

System został zaprojektowany i zaimplementowany przy użyciu technologii:

- Backend: Java ze wsparciem frameworka Spring Boot (zarządzanie procesami, harmonogramowanie zadań i obsługa webhooków).
- Frontend webowy: JavaScript / TypeScript w oparciu o bibliotekę React (dynamiczny panel administratora z obsługą strumieniowania zdarzeń w czasie rzeczywistym).
- Baza danych i pamięć podręczna: MySQL 8.0 jako główny relacyjny magazyn danych oraz Redis jako baza klucz-wartość do obsługi szybkiego cache'owania i sesji.
- Konteneryzacja: Docker oraz Docker Compose do zapewnienia pełnej izolacji, powtarzalności i łatwości wdrożenia całego środowiska.

## Struktura środowiska (Docker Compose)

Cały stos aplikacyjny uruchamiany jest automatycznie w ramach zdefiniowanej struktury kontenerów, podzielonych na warstwy:

### Warstwa aplikacji

- alert-backend: Główny kontener aplikacji Spring Boot odpowiedzialny za logikę biznesową, autoryzację i odbiór alertów.
- alert-frontend: Serwer Nginx serwujący aplikację kliencką React.
- mysql-app: Relacyjna baza danych MySQL przechowująca konfigurację użytkowników, grup i historię incydentów.
- redis-cache: Szybka pamięć podręczna Redis optymalizująca zapytania i działanie backendu.

## Instalacja i uruchomienie

1. Sklonuj repozytorium projektu:

```bash
git clone https://github.com/PatriStelmach/Agrub.git
cd Agrub
```

2. Uruchom kontenery:
```bash
 docker compose up -d
```


## Opis funkcjonalności
### 1. Elastyczne uwierzytelnianie

System umożliwia logowanie użytkowników na dwa niezależne sposoby:
- Klasyczna autoryzacja na podstawie lokalnej bazy danych MySQL systemu Agrub.
- Integracja z usługami katalogowymi Active Directory / LDAP (mapowanie użytkowników domenowych).

### 2. Wsparcie dla systemów zewnętrznych (Wazuh, Zabbix, Nagios)

Agrub działa jako centralny punkt agregacji dla wiodących platform monitoringu i bezpieczeństwa. Z poziomu panelu możliwe jest:
- Wazuh: Odbieranie alertów bezpieczeństwa (SIEM) oraz weryfikacja logów operacyjnych z agentów.
- Zabbix & Nagios: Monitorowanie statusu infrastruktury, automatyczna synchronizacja problemów oraz możliwość bezpośredniego zatwierdzania incydentów (ACK) lub zamykania zgłoszeń bez konieczności przechodzenia do paneli źródłowych tych systemów.
### 3. Własne wtyczki

Możliwość rozszerzania możliwości systemu poprzez uruchamianie własnych skryptów i pluginów reagujących na incydenty lub wykonujących cykliczne zadania. System wspiera języki:
- Bash
- Python
- PowerShell

### 4. Kontrola dostępu i zarządzanie uprawnieniami

- Grupy użytkowników: Możliwość tworzenia struktur grupowych w celu precyzyjnego regulowania poziomów uprawnień oraz widoczności alertów dla poszczególnych zespołów (np. SOC, Network, DevOps).
- Klucze API: Generowanie dedykowanych tokenów uwierzytelniających umożliwiających integrację zewnętrznych systemów za pomocą mechanizmu webhooków.

### 5. Zaawansowane wykresy i analityka

Panel udostępnia zestaw metryk ułatwiających analizę efektywności pracy zespołu:
- Całkowita liczba zarejestrowanych alertów.
- Całkowita liczba alertów z ostatnich 7 dni z podziałem na stopnie ważności.
- Średni czas potrzebny na podjęcie (zatwierdzenie) alertu w ciągu ostatnich 7 dni.
- Średni czas potrzebny na całkowite rozwiązanie i zamknięcie zgłoszenia w ciągu ostatnich 7 dni.
- Porównawcze zestawienie czasu reakcji do czasu pełnego rozwiązania incydentu w ciągu ostatnich 7 dni.

## Konfiguracja systemu

Wszystkie kluczowe parametry środowiskowe zarządzane są z poziomu sekcji ustawień w aplikacji.
### Ustawienia bezpieczeństwa
- Czas życia hasła (dni): domyślnie 90
- Czas życia tokenu dostępowego (minuty): domyślnie 1440
- Czas życia tokenu odświeżania (godziny): domyślnie 168

### Konfiguracja Active Directory / LDAP

- Domena Active Directory
- URL serwera Active Directory
- Główna ścieżka LDAP (Base DN)
- Szablon nazwy wyróżniającej użytkownika LDAP (User DN Pattern)

### Ustawienia powiadomień pocztowych (SMTP)

- Serwer SMTP
- Port SMTP
- Użytkownik SMTP
- Status usługi SMTP (Włączona/Wyłączona)

## Twórcy
- Mateusz Andrzejak
- Patri Stelmach
- Kamil Kornatowski
- Błażej Majchrzak

# Instrukcja integracji systemu Alert z zewnętrznymi systemami

# Przygotowanie kluczy autoryzacyjnych

- Do poprawnej komunikacji systemów zewnętrznych z systemem Alert, wymagane jest utworzenie kluczy API.
- Przejdź do zakładki **Klucze API**, wygeneruj nowy klucz (np. "Zabbix Webhook") i skopiuj go, zalecane jest stworzenie osobnego klucza dla każdego użytku
# Integracja Zabbix z platformą AlertWIP

Aby systemy mogły się poprawnie komunikować, musimy skonfigurować aktywny odczyt z poziomu AlertWIP (**Tryb Pull**) oraz natychmiastowe wysyłanie powiadomień przez Zabbixa (**Tryb Push**).

## Krok 1: Przygotowanie klucza autoryzacyjnego

*  Zaloguj się na konto administratora w panelu Zabbix, przejdź do **Users -> API tokens**, utwórz nowy token i skopiuj jego wartość.

## Krok 2: Konfiguracja połączenia w AlertWIP (Tryb Pull)

Dzięki temu AlertWIP będzie w stanie odpytywać Zabbixa o status alertów i zamykać je, gdy problem zostanie rozwiązany.

1. W panelu **AlertWIP** przejdź do **Ustawień Systemowych**.
2. Wybierz pole ZABBIX
3. Zaktualizuj poniższe klucze:
   - `enabled`: `true`
   - `url`: `http://<IP_ZABBIXA>:<PORT>/api_jsonrpc.php`
   - `New API key`: `<wklej token wygenerowany w Zabbixie w Kroku 1>`

## Krok 3: Konfiguracja Webhooka w Zabbix (Tryb Push)

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
| `URL`           | `http://<AGRUB_IP>:<PORT>/api/webhooks/zabbix/alert`   |
| `ApiKey`        | `<klucz_wygenerowany_w_Agrub>`                         |

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

1. Przejdź do **Users -> Users**.
2. Wybierz użytkownika (lub stwórz dedykowanego "Agrub User"), do którego przypięte są powiadomienia.
3. Przejdź do zakładki Media, kliknij Add i jako Type wybierz utworzony przed chwilą Agrub Webhook. W polu sendto wpisz wybraną przez siebie nazwę. Zapisz zmiany.
4. Przejdź do Alerts -> Actions -> Trigger actions.
5. Wybierz Create action i nazwij ją
6. W polu Operations kliknij "Add" i wybierz usera skonfigurowanego w poprzednich krokach, po czym zaznacz Send only to Agrub-Webhook
7. Powtórz te same kroki dla Recovery Operations



# Integracja Wazuh z platformą AlertWIP

- Z uwagi na działanie systemu Wazuh jako system powiadamiający o aktywnych zdarzeniach, a nie aktywnego monitoringu z problemami i recovery, integracja z systemem Wazuh działa tylko w trybie PULL. 
- Pola:
	- Enabled
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
    <hook_url>http://<AGRUB_IP>:<PORT>/api/webhooks/wazuh</hook_url>
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

Stwórz plik `/opt/nagios/libexec/agrub_webhook.sh` i wklej poniższą treść:

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

Pamiętaj o nadaniu uprawnień wykonywania: `chmod +x /opt/nagios/libexec/agrub_webhook.sh`

## Krok 2: Definicja komend
Dodaj poniższe definicje do pliku `/opt/nagios/etc/objects/commands.cfg`:
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

## Krok 3: Konfiguracja kontaktu
Dodaj użytkownika integracyjnego w `/opt/nagios/etc/objects/contacts.cfg`:
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


