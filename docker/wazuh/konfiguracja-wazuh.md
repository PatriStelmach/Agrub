# Konfiguracja Środowiska Wazuh dla Projektu AlertWIP

Niniejszy dokument opisuje kroki niezbędne do zainicjowania środowiska po pobraniu repozytorium, wywoływania testowych alertów oraz integracji z API na potrzeby backendu.

## 1. Uruchomienie środowiska
Będąc w katalogu z plikiem `docker-compose.yml`, zbuduj i uruchom kontenery w tle:
`docker compose up -d --build`

---

## 2. Inicjalizacja Bezpieczeństwa (Wymagane przy pierwszym starcie)
Po uruchomieniu kontenerów należy zainicjować bazę Indexera. Bez tego krok endpointy API będą zwracać błędy.

**Krok A: Inicjalizacja Indexera**
Wykonaj poniższą komendę w terminalu:
`docker exec -it -u root wazuh-indexer bash -c "export JAVA_HOME=/usr/share/wazuh-indexer/jdk && /usr/share/wazuh-indexer/plugins/opensearch-security/tools/securityadmin.sh -cd /usr/share/wazuh-indexer/opensearch-security/ -icl -nhnv -cacert /usr/share/wazuh-indexer/certs/root-ca.pem -cert /usr/share/wazuh-indexer/certs/admin.pem -key /usr/share/wazuh-indexer/certs/admin-key.pem -h localhost"`

**Krok B: Restart Menedżera**
Aby menedżer podłączył się do nowo zainicjalizowanej bazy, zrestartuj jego procesy:
`docker restart wazuh-manager`
`docker exec -it wazuh-manager /etc/init.d/filebeat restart`

---

## 3. Wywoływanie Alertów (Testowanie na sucho)

Poniższe komendy symulują wystąpienie zagrożeń, co pozwala przetestować parsowanie logów w aplikacji Java.

**Symulacja ataku na Agencie (Brute Force SSH):**
`docker exec -it wazuh-agent bash -c "echo '$(date '+%b %d %H:%M:%S') prod-agent-01 sshd[123]: Failed password for root from 8.8.8.8 port 22 ssh2' >> /var/log/auth.log"`

**Ręczne wstrzyknięcie logu błędu do Menedżera:**
`docker exec -it wazuh-manager bash -c 'echo "$(date "+%Y/%m/%d %H:%M:%S") wazuh-modulesd:database: ERROR: [TEST-ALERT] Krytyczne włamanie do systemu!" >> /var/ossec/logs/ossec.log'`

---

## 4. Integracja i Odpytywanie API (Curl)

**A. Pobranie Tokenu Autoryzacyjnego (Bearer)**
Zwraca token wymagany do autoryzacji kolejnych zapytań (dane logowania: `wazuh:wazuh`).
`curl -u wazuh:wazuh -k -X GET "https://localhost:55000/security/user/authenticate"`

**B. Odczyt surowych logów (Używane przez Spring Boot)**
Zwraca strumień logów operacyjnych menedżera, na których opiera się system powiadomień AlertWIP.
`curl -k -H "Authorization: Bearer <TWÓJ_TOKEN>" -X GET "https://localhost:55000/manager/logs?limit=100"`

**C. Pobieranie listy Agentów**
`curl -k -H "Authorization: Bearer <TWÓJ_TOKEN>" -X GET "https://localhost:55000/agents?select=id,name,status,ip"`

---

## 5. Wymagane wpisy w `application.properties`
Aby backend łączył się z API Wazuha, upewnij się, że posiadasz następującą konfigurację:
```properties
wazuh.api.url=https://localhost:55000
wazuh.api.user=wazuh
wazuh.api.password=wazuh
wazuh.api.ignore-ssl=true