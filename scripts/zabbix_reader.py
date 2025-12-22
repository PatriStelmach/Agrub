from pyzabbix import ZabbixAPI
import sys
from datetime import datetime

# --- CONFIG ---
ZABBIX_URL = "http://localhost:9999"
USER = "Admin"
PASSWORD = "zabbix"
API_TOKEN = ""
# --------------------

def connect_zabbix():
    print(f"Łączenie z {ZABBIX_URL}...")
    try:
        zapi = ZabbixAPI(ZABBIX_URL)
        if API_TOKEN:
            zapi.login(api_token=API_TOKEN)
            print("Zalogowano tokenem")
        else:
            zapi.login(USER, PASSWORD)
            print(f"Zalogowano jako użytkownik: {USER}")
        return zapi
    except Exception as e:
        print(f"Błąd połączenia: {e}")
        sys.exit(1)


def format_timestamp(ts):
    return datetime.fromtimestamp(int(ts)).strftime('%Y-%m-%d %H:%M:%S')


def main():
    zapi = connect_zabbix()

    #get alerts
    try:
        problems = zapi.problem.get(
            output=["eventid", "name", "severity", "acknowledged", "clock", "objectid"],
            selectAcknowledges="extend",
            sortfield=["eventid"],
            sortorder="DESC",
            recent=True
        )
    except Exception as e:
        print(f"Błąd pobierania problemów: {e}")
        return

    if not problems:
        print("\nBrak problemów. System czysty.")
        return

    # get hostnames
    # get all triggers id
    trigger_ids = [p['objectid'] for p in problems]

    # get api request about triggers and hosts
    triggers = zapi.trigger.get(
        triggerids=trigger_ids,
        output=["triggerid"],
        selectHosts=["name"]
    )

    # map trigger id -> hostname
    trigger_host_map = {}
    for t in triggers:
        if t['hosts']:
            trigger_host_map[t['triggerid']] = t['hosts'][0]['name']

    print(f"\n⚠Znaleziono {len(problems)} zdarzeń/problemów:\n")

    for p in problems:
        severity_map = ['Not classified', 'Information', 'Warning', 'Average', 'High', 'Disaster']
        severity_name = severity_map[int(p['severity'])]

        # get hostname from map
        host_name = trigger_host_map.get(p['objectid'], "Unknown Host")

        ack_status = "TAK" if int(p['acknowledged']) == 1 else "NIE"

        print(f"ID: {p['eventid']} | Host: {host_name} | Poziom: {severity_name}")
        print(f"Problem: {p['name']}")
        print(f"Czas:    {format_timestamp(p['clock'])}")
        print(f"ACK:     {ack_status}")

        if p['acknowledges']:
            print("Historia komentarzy:")
            for ack in p['acknowledges']:
                ack_time = format_timestamp(ack['clock'])
                author = ack['alias']
                message = ack['message']
                print(f"      [{ack_time}] {author}: {message}")

        print("-" * 60)


if __name__ == "__main__":
    main()