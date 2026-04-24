package pl.pjatk.alertwip.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GlobalProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Wewnętrzny klucz backendowy do zapobiegania duplikatów (np. "SCRIPT-42" albo "[ZABBIX] web01 - CPU")
    private String uniqueKey;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String source;

    private String status = "Sent"; // "Sent" | "In Process" | "Done"

    private int severity = 1;

    // Automatycznie tworzy tabelę powiązaną dla listy stringów
    @ElementCollection
    @CollectionTable(name = "alert_technician_groups", joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "group_name")
    private List<String> technicianGroups = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime closedAt;

    // --- GETTERY I SETTERY ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUniqueKey() { return uniqueKey; }
    public void setUniqueKey(String uniqueKey) { this.uniqueKey = uniqueKey; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }

    public List<String> getTechnicianGroups() { return technicianGroups; }
    public void setTechnicianGroups(List<String> technicianGroups) { this.technicianGroups = technicianGroups; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}