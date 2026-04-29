FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y curl gnupg2 apt-transport-https

# 1. Instalacja agenta
RUN curl -s https://packages.wazuh.com/key/GPG-KEY-WAZUH | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/wazuh.gpg --import && \
    chmod 644 /etc/apt/trusted.gpg.d/wazuh.gpg && \
    echo "deb https://packages.wazuh.com/4.x/apt/ stable main" | tee -a /etc/apt/sources.list.d/wazuh.list && \
    apt-get update && apt-get install -y wazuh-agent=4.7.2-1

# 2. Skrypt startowy z automatyczną konfiguracją
RUN echo '#!/bin/bash\n\
# Podmiana adresu Managera w ossec.conf\n\
sed -i "s/MANAGER_IP/wazuh-manager/" /var/ossec/etc/ossec.conf\n\
\n\
# Rejestracja u Managera\n\
/var/ossec/bin/agent-auth -m wazuh-manager\n\
\n\
# Start usług\n\
/var/ossec/bin/wazuh-control start\n\
\n\
echo "Agent WAZUH produkcyjny wystartował..."\n\
tail -f /var/ossec/logs/ossec.log' > /entrypoint.sh && chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
