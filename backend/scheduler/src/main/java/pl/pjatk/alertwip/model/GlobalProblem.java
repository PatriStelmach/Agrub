package pl.pjatk.alertwip.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "global_problem", indexes = {
        @Index(name = "idx_unique_key_id", columnList = "uniqueKey, id DESC")
})
public class GlobalProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uniqueKey;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String source;
    private String originType;
    private String status = "Sent"; // "Sent" | "In Process" | "Done"
    private int severity = 1;

    // NOWOŚĆ: Memoizacja ról i powiadomień
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alert_technician_groups", joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "group_name")
    private List<String> technicianGroups = new ArrayList<>();

    private boolean requiresNotification = false; // Flaga dla dźwięku/popupu

    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    // --- GETTERY I SETTERY ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUniqueKey() { return uniqueKey; }
    public void setUniqueKey(String uniqueKey) { this.uniqueKey = uniqueKey; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public void setMessage(String content) { this.message = content; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }
    public List<String> getTechnicianGroups() { return technicianGroups; }
    public void setTechnicianGroups(List<String> technicianGroups) { this.technicianGroups = technicianGroups; }
    public boolean isRequiresNotification() { return requiresNotification; }
    public void setRequiresNotification(boolean requiresNotification) { this.requiresNotification = requiresNotification; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}