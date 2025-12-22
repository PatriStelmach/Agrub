from datetime import datetime
from zabbix_client import connect_to_zabbix

def format_timestamp(ts):
    # Convert Unix timestamp to readable format
    return datetime.fromtimestamp(int(ts)).strftime('%Y-%m-%d %H:%M:%S')

def main():
    # Connect to Zabbix
    zapi = connect_to_zabbix()

    # Fetch active problems
    try:
        problems = zapi.problem.get(
            output=["eventid", "name", "severity", "acknowledged", "clock", "objectid"],
            selectAcknowledges="extend",
            sortfield=["eventid"],
            sortorder="DESC",
            recent=True
        )
    except Exception as e:
        print(f"[ERROR] API error: {e}")
        return

    if not problems:
        print("\n[INFO] No problems found. System is clean.")
        return

    # Map trigger IDs to hostnames
    trigger_ids = [p['objectid'] for p in problems]
    triggers = zapi.trigger.get(
        triggerids=trigger_ids,
        output=["triggerid"],
        selectHosts=["name"]
    )

    trigger_host_map = {}
    for t in triggers:
        if t['hosts']:
            trigger_host_map[t['triggerid']] = t['hosts'][0]['name']

    print(f"\n[WARN] Found {len(problems)} events:\n")

    for p in problems:
        severity_map = ['Not classified', 'Information', 'Warning', 'Average', 'High', 'Disaster']
        sev_name = severity_map[int(p['severity'])]
        host_name = trigger_host_map.get(p['objectid'], "Unknown Host")
        ack_status = "YES" if int(p['acknowledged']) == 1 else "NO"

        print(f"ID: {p['eventid']} | Host: {host_name} | Level: {sev_name}")
        print(f"Problem: {p['name']}")
        print(f"Time:    {format_timestamp(p['clock'])}")
        print(f"ACK:     {ack_status}")

        if p['acknowledges']:
            print("   Acknowledgment history:")
            for ack in p['acknowledges']:
                ack_time = format_timestamp(ack['clock'])
                # Zabbix 7.x: 'alias' field changed to 'username'
                author = ack.get('username', ack.get('alias', 'System/Unknown'))
                message = ack['message']
                print(f"      [{ack_time}] {author}: {message}")

        print("-" * 60)

if __name__ == "__main__":
    main()
