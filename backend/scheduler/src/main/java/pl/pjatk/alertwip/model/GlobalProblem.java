package pl.pjatk.alertwip.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GlobalProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // taskId służy jako klucz do powiązania problemu z konkretnym zadaniem
    private Long taskId;
    private String taskName;

    // Zmieniono z description na lastErrorMessage
    @Column(length = 255)
    private String lastErrorMessage;

    // Zmieniono z startTime na occurrenceTime
    private LocalDateTime occurrenceTime;

    // Konstruktor bezargumentowy dla JPA
    public GlobalProblem() {}

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }

    public LocalDateTime getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(LocalDateTime occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    private int severity;

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }
}