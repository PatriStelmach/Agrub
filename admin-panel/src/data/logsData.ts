import type {LogObject} from "@/types/types";

const systems = ["Zabbix", "Wazuh", "Nagios"];

const pluginHeaders: Record<string, string[]> = {
  "bash script": ["Script execution started", "Iteration completed", "Script finished successfully"],
  "python monitor": ["CPU usage check completed", "Threshold exceeded", "Monitoring cycle finished"],
  "node service": ["Service health check", "API endpoint called", "Service started"],
  "docker watcher": ["Container status check", "Docker event detected", "Container state changed"],
  "log analyzer": ["Log analysis completed", "Error pattern found", "Anomaly detected"],
  "cpu monitor": ["CPU load check", "High CPU usage alert", "CPU usage normal"],
  "memory checker": ["Memory check completed", "Memory usage normal", "Low memory warning"],
  "network scanner": ["Port scan completed", "Open port detected", "Network scan finished"],
  "disk monitor": ["Disk space check", "Low disk space warning", "Disk usage normal"],
  "process watcher": ["Process list updated", "New process detected", "Process terminated"],
  "file auditor": ["File integrity check", "File change detected", "Audit completed"],
  "health checker": ["Health check failed", "Service recovery", "Health check passed"],
  "SSL tracker": ["SSL certificate expiring", "Certificate valid", "Certificate check"],
  "error catcher": ["Error detected", "Exception caught", "Error log analysis"],
  "access logger": ["User login", "Access granted", "Access denied"],
  "db connector": ["Database connection", "Query executed", "Connection lost"],
  "queue watcher": ["Queue depth check", "Message processed", "Queue empty"],
  "backup scheduler": ["Backup completed", "Backup failed", "Backup started"],
  "API gateway": ["API request", "Gateway timeout", "Request processed"],
  "metrics aggregator": ["Metrics collected", "Export failed", "Aggregation complete"],
  "session manager": ["Session created", "Session expired", "Token generated"],
  "rate limiter": ["Rate limit hit", "Request allowed", "Throttle applied"],
  "cache warmer": ["Cache refreshed", "Cache hit", "Cache miss"],
  "load balancer": ["Server selected", "Health check", "Load balanced"],
  "audit trail": ["Audit event", "User action", "Compliance check"],
  "alert router": ["Alert routed", "Notification sent", "Escalation triggered"],
};

const logContents = [
  "Operation completed successfully",
  "An error occurred during execution",
  "Warning: threshold exceeded",
  "Information: process started",
  "Debug: processing data",
  "Critical: system failure",
  "Notice: configuration updated",
  "Error: connection timeout",
  "Info: service restarted",
  "Warning: resource usage high",
];

const groups = ["Network Team", "Security Team", "System Administrators", "DevOps", "Support Team"];

export const logsData: LogObject[] = Array.from({ length: 100 }, (_, i) => {
  const isSystem = i < 30;
  let source: string;
  let header: string;
  
  if (isSystem) {
    source = systems[i % 3]!;
    const systemHeaders: Record<string, string[]> = {
      Zabbix: ["Host down", "CPU high usage", "Disk full", "Service stopped", "Trigger activated", "Item not supported", "Network traffic high", "Low available memory", "Agent unavailable", "Template applied"],
      Wazuh: ["Security alert", "Intrusion detected", "Malware found", "Firewall blocked", "Access denied", "Vulnerability found", "Rootkit detected", "File integrity changed", "Policy violation", "Attack detected"],
      Nagios: ["Service critical", "Host down", "Notification sent", "Flapping detected", "Service recovery", "Soft state", "Hard state", "Acknowledgment added", "Scheduled downtime", "Event handler executed"],
    };
    const headers = systemHeaders[source] ?? ["Event logged"];
    header = headers[i % headers.length]!;
  } else {
    const pluginIndex = (i - 30) % 26;
    const pluginNames = Object.keys(pluginHeaders);
    const pluginName = pluginNames[pluginIndex]!;
    source = pluginName;
    const pluginHeaderList = pluginHeaders[pluginName]!;
    header = pluginHeaderList[i % pluginHeaderList.length]!;
  }

  const daysAgo = Math.floor(i * 0.5);
  const hoursAgo = Math.floor(Math.random() * 24);
  const createdAt = new Date();
  createdAt.setDate(createdAt.getDate() - daysAgo);
  createdAt.setHours(createdAt.getHours() - hoursAgo);

  return {
    id: i + 1,
    header,
    content: logContents[i % logContents.length]!,
    source,
    technicianGroups: [groups[i % groups.length]!],
    createdAt,
  };
});