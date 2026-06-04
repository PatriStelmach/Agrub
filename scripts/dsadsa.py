#!/usr/bin/env python3
# Creator: System
# Description: stringaaa
# Tags: string
# Severity: 2














import sys
from datetime import datetime

# Pobieramy aktualną minutę
obecna_sekunda = datetime.now().second

# Sprawdzamy, czy jest podzielna przez 2 (reszta z dzielenia wynosi 0)
if obecna_sekunda % 2 == 0:
    sys.exit(0)
else:
    sys.exit(1)