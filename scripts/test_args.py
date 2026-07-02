# Creator: Tester
# Description: Skrypt do sprawdzania przekazywanych argumentów
# Severity: 1

import sys

print("Uruchomiono skrypt!")
print(f"Liczba przekazanych argumentów: {len(sys.argv) - 1}") 
for i, arg in enumerate(sys.argv[1:], start=1):
    print(f"Argument {i}: {arg}")