import sys
from zabbix_client import connect_to_zabbix

def acknowledge_event(zapi, event_id, message, close=False):
    """
    Send ACK to Zabbix.
    Action values (bitwise):
    1 - Close problem
    2 - Acknowledge
    4 - Add message
    """
    # Default action: ACK (2) + Message (4) = 6
    action_value = 6
    action_text = "ACK + Comment"

    if close:
        # Close (1) + ACK (2) + Message (4) = 7
        action_value = 7
        action_text = "ACK + Comment + CLOSE"

    print(f"[INFO] Processing ID {event_id}: {action_text}...")

    try:
        zapi.event.acknowledge(
            eventids=event_id,
            action=action_value,
            message=message
        )
        print(f"[SUCCESS] Problem {event_id} updated successfully.")
    except Exception as e:
        print(f"[ERROR] Update failed: {e}")

def print_help():
    help_text = """
Usage: python zabbix_ack.py [OPTIONS] <ID> <"COMMENT"> <1=Close/0=No>

Arguments:
  <ID>                Event ID to acknowledge.
  <"COMMENT">         Optional comment for the acknowledgment.
  <1=Close/0=No>      Optional flag to close the problem (1) or not (0).

Options:
  -h, --help          Show this help message and exit.

Examples:
  python zabbix_ack.py 12345
  python zabbix_ack.py 12345 "Network issue fixed"
  python zabbix_ack.py 12345 "Network issue fixed" 1
"""
    print(help_text)

def main():
    zapi = connect_to_zabbix()

    # Check for help option
    if len(sys.argv) > 1 and sys.argv[1] in ('-h', '--help'):
        print_help()
        return

    # CLI arguments
    if len(sys.argv) > 1:
        try:
            e_id = sys.argv[1]
            # Use default comment if none provided
            msg = sys.argv[2] if len(sys.argv) > 2 else "Auto-ACK from script"

            should_close = False
            if len(sys.argv) > 3 and sys.argv[3] == "1":
                should_close = True

            acknowledge_event(zapi, e_id, msg, should_close)
        except Exception as e:
            print(f"[ERROR] Argument error: {e}")
            print_help()
    else:
        # Interactive mode
        print("\n--- Interactive Mode ---")
        event_id = input("Enter Event ID: ")
        if not event_id:
            print("Cancelled.")
            return

        message = input("Enter comment: ")
        close_input = input("Close problem? (y/N): ")
        should_close = close_input.lower() == 'y'

        acknowledge_event(zapi, event_id, message, should_close)

if __name__ == "__main__":
    main()
