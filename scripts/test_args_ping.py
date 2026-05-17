# Creator: Tester
# Description: Skrypt do sprawdzania przekazywanych argumentów
# Severity: 3

import sys
import subprocess
import platform

def main():
    # 1. Sprawdzenie, czy przekazano argument
    if len(sys.argv) < 2:
        print("BLAD: Nie podano adresu IP ani domeny jako argumentu.")
        print("Uzycie: python ping_check.py <IP_LUB_DOMENA>")
        sys.exit(1)

    ip_or_domain = sys.argv[1]
    print(f"Sprawdzam polaczenie z: {ip_or_domain}...")

    # 2. Dostosowanie flag ping pod system operacyjny (Windows vs Linux/Docker)
    is_windows = platform.system().lower() == "windows"
    
    # -c 1 (Linux) / -n 1 (Windows) -> wyślij tylko 1 pakiet
    # -W 2 (Linux) / -w 2000 (Windows) -> timeout 2 sekundy na odpowiedź
    if is_windows:
        command = ["ping", "-n", "1", "-w", "2000", ip_or_domain]
    else:
        command = ["ping", "-c", "1", "-W", "2", ip_or_domain]

    try:
        # 3. Wykonanie komendy systemowej
        # Przechwytujemy stdout/stderr, żeby wrzucić je do logów w bazie MySQL
        result = subprocess.run(
            command, 
            stdout=subprocess.PIPE, 
            stderr=subprocess.PIPE, 
            text=True
        )

        # 4. Analiza kodu wyjścia systemowego pinga
        if result.returncode == 0:
            print(f"SUKCES: Host {ip_or_domain} dziala prawidlowo.")
            sys.exit(0)
        else:
            print(f"ALARM: Host {ip_or_domain} NIE ODPOWIADA na ping!")
            if result.stdout:
                print(f"Szczegoly systemu:\n{result.stdout.strip()}")
            sys.exit(1)

    except Exception as e:
        print(f"BlaD SYSTEMOWY podczas proby wykonania ping: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main()