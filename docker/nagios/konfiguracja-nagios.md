# Konfiguracja Środowiska Nagios dla Projektu AlertWIP

## 1. Wywoływanie Alertów (Passive Checks)

W celach testowych nie konfigurujemy pełnych agentów NRPE. Zamiast tego "wstrzykujemy" wyniki testów bezpośrednio do potoku poleceń Nagiosa (`nagios.cmd`).

**A. Wywołanie alertu CRITICAL (Status 2):**
Symulacja awarii serwera HTTP:
`docker exec -it nagios bash -c 'printf "[%lu] PROCESS_SERVICE_CHECK_RESULT;localhost;HTTP;2;[TEST] Connection refused - Port 80 nie odpowiada!\n" $(date +%s) > /opt/nagios/var/rw/nagios.cmd'`

**B. Wywołanie alertu WARNING (Status 1):**
Symulacja wysokiego obciążenia procesora:
`docker exec -it nagios bash -c 'printf "[%lu] PROCESS_SERVICE_CHECK_RESULT;localhost;Current Load;1;[TEST] Load average przekroczyl 5.0\n" $(date +%s) > /opt/nagios/var/rw/nagios.cmd'`

**C. Zamykanie alertu / Status OK (Status 0):**
Poinformowanie Nagiosa, że problem został rozwiązany:
`docker exec -it nagios bash -c 'printf "[%lu] PROCESS_SERVICE_CHECK_RESULT;localhost;HTTP;0;[TEST] Serwis HTTP wrocil do normy\n" $(date +%s) > /opt/nagios/var/rw/nagios.cmd'`

---

## 2. Odpytywanie API (Bezpośrednio z Nagiosa)

Nagios udostępnia dane o statusach poprzez skrypty CGI zwracające format JSON.

**Pobranie listy aktywnych problemów (CRITICAL i WARNING):**
`curl -u nagiosadmin:nagiosadmin "http://localhost:8080/nagios/cgi-bin/statusjson.cgi?query=servicelist&servicestatus=critical+warning&details=true"`

Parametry:
* `query=servicelist` - pobiera listę usług.
* `servicestatus=critical+warning` - filtruje tylko błędy i ostrzeżenia.
* `details=true` - dołącza treść błędu (plugin_output).

---

## 3. Integracja z AlertWIP (Konfiguracja)

Backend AlertWIP integruje się z Nagiosem poprzez `NagiosSyncService`. Wymagane są następujące wpisy w bazie danych (zarządzane przez `SettingsSeeder`):

| Klucz | Przykładowa wartość | Opis |
| :--- | :--- | :--- |
| `nagios_enabled` | `true` | Aktywacja modułu Nagios |
| `nagios_url` | `http://localhost:8080/nagios/cgi-bin/statusjson.cgi` | Adres API Nagiosa |
| `nagios_user` | `nagiosadmin` | Użytkownik API (Basic Auth) |
| `nagios_pass_SECRET` | `nagiosadmin` | Hasło API |

**Logika mapowania statusów:**
* Nagios `2 (CRITICAL)` -> AlertWIP `Severity 5`
* Nagios `1 (WARNING)` -> AlertWIP `Severity 3`
* Nagios `0 (OK)` -> AlertWIP `Status Done` (zamknięcie alertu w cache)

---

## 4. API Aplikacji AlertWIP (Endpointy backendowe)

Aplikacja Spring Boot wystawia dedykowane endpointy REST do zarządzania danymi pobranymi z Nagiosa. Poniższe komendy można wykonać w terminalu (na systemie Windows użyj `curl.exe` lub `Invoke-RestMethod`).

**A. Pobranie listy monitorowanych hostów:**
Zwraca listę podpiętych hostów i ich aktualny status.
`curl.exe -X GET "http://localhost:10000/api/nagios/hosts"`

**B. Pobranie wszystkich problemów z Nagiosa (z bazy AlertWIP):**
Zwraca historię i listę aktywnych problemów przypisanych do odpowiednich grup wsparcia.
`curl.exe -X GET "http://localhost:10000/api/nagios/problems"`

**C. Ręczne zamykanie problemu w AlertWIP:**
Rozwiązuje problem po stronie backendu, usuwa go z cache'u (`ActiveAlertCache`) i rozsyła powiadomienie do frontendu przez SSE (`ALERT_RESOLVED`). Wymaga podania ID problemu z bazy danych.
`curl.exe -X PATCH "http://localhost:10000/api/nagios/problems/{id}/close"`