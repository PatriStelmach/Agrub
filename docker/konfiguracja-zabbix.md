### 1. Uruchomienie zabbix 
1. docker compose up -d
2. Po odczekaniu kilku minut (za pierwszym razem baza danych się długo wczytuje) odpalamy `http://localhost:9999`.
3. Login: `Admin` Hasło: `zabbix`

## 2. Dodanie hosta do zabbix
1. `Data collection` -> `Hosts` -> `Create host`.
2. Host name: `test-node` - (musi pasować do hostname z docker-compose.yml).
3. Templates: `Linux by Zabbix agent.`
4. Host groups: `Linux servers`.
5. Interfaces: `Add` -> `Agent`. 
	1. DNS name: `test-linux-node`.
	2. Connect to: `DNS`.
6. Zatwierdzamy -> `Add`.

Jeśli host dodał się poprawnie po chwili powinna zaświećić się ikona `ZBX` na zielono.

## 3. Obniżenie progów alertu

Aby nie katować komputera obniżamy próg wywołania alertu na obciażenie CPU, RAM i dysku.

1. `Data Collection` -> `Hosts`.
2. Kliknij na nazwe hosta: `test-node`.
3. W górnym menu wybierz `Macros`.
4. Przełacz opcje z `Host macros` na `Inherited and host macros`.
5. `{$CPU.UTIL.CRIT}` -> `Change` -> wartość ustaw na `5` lub `1` jeśli chcesz aby alert wyświetlał się ciagle.
6. `{$MEMORY.UTIL.MAX}` -> `Change` -> wartość ustaw na `15` lub `1` jeśli chcesz aby alert wyświetlał się ciagle.
7.  `{$VFS.FS.PUSED.MAX.CRIT}` -> `Change` -> wartość ustaw na `10` lub `1` jeśli chcesz aby alert wyświetlał się ciagle.

## 4. Obniżenie czasu wywołania triggera 

1. `Data Collection` -> `Templates`.
2. W name wpisz: `Linux by Zabbix agent` i kliknij w ten szablon na liście.
3. Wybierz `Triggers` dla Template `Linux by Zabbix agent`.
4. W name wpisz: `CPU` i kliknij w ten `Apply`.
5. Kliknij w `Linux: High CPU utilization`.
6. W polu `Expression` zamień:
```text
min(/Linux by Zabbix agent/system.cpu.util,5m)>{$CPU.UTIL.CRIT}
```
na:
```
last(/Linux by Zabbix agent/system.cpu.util)>{$CPU.UTIL.CRIT}
```

7. `Update`.
8. W name wpisz: `memory` i kliknij w ten `Apply`.
9. Kliknij w `Linux: High memory utilization`.
10. W polu `Expression` zamień:
```text
min(/Linux by Zabbix agent/vm.memory.utilization,5m)>{$MEMORY.UTIL.MAX}
```
na:
```
last(/Linux by Zabbix agent/vm.memory.utilization)>{$MEMORY.UTIL.MAX}
```

11. `Update`.

## 5. Konfiguracja zabbix-agent i symulacja awarii

1. Zalogowanie jako root do konternera.
```bash
docker compose exec --user root -it test-linux-node bash
```

2. Aktualizacja i pobranie pakietu stress.
```bash 
apt-get update && apt-get install -y stress 
```

3. Wywołanie alertu o obciążeniu procesora

```bash
stress --cpu 2 --timeout 300
```
**obciążenie 2 wątków na 5 minut**

4. W ciagu minuty powinien pojawić się alert (zabbix agent jest odpytywany co minutę).

