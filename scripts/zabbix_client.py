# Description: dupa dupa test.
# Tags: network, icmp, latency
# IsLog: false

import os
import sys
from pyzabbix import ZabbixAPI
from dotenv import load_dotenv

load_dotenv()

def connect_to_zabbix():
    """
    Creates and returns an authenticated ZabbixAPI object
    using environment variables.
    """
    url = os.getenv("ZABBIX_URL")
    user = os.getenv("ZABBIX_USER")
    password = os.getenv("ZABBIX_PASSWORD")
    token = os.getenv("ZABBIX_TOKEN")

    if not url:
        print("[ERROR] ZABBIX_URL not found in .env file")
        sys.exit(1)

    print(f"[INFO] Connecting to {url}...")

    try:
        zapi = ZabbixAPI(url)

        if token:
            zapi.login(api_token=token)
            print("[INFO] Logged in (Token)")
        elif user and password:
            zapi.login(user, password)
            print(f"[INFO] Logged in as: {user}")
        else:
            print("[ERROR] No login credentials (USER/PASSWORD or TOKEN) in .env")
            sys.exit(1)

        return zapi
    except Exception as e:
        print(f"[CRITICAL] Connection error with Zabbix: {e}")
        sys.exit(1)
