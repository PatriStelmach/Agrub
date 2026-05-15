package pl.pjatk.alertwip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "problem_action")
public class ProblemAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    @JsonIgnore
    private GlobalProblem problem;

    private String author; // Kto wykonał akcję

    @Column(columnDefinition = "TEXT")
    private String message; // Komentarz użytkownika

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = true)
    private Boolean ackUpdate; // true dla ack, false dla unack, null dla bez zmiany

    @Column(nullable = true)
    private Integer previousSeverity;

    @Column(nullable = true)
    private Integer newSeverity;

    @Enumerated(EnumType.STRING)
    private SyncStatus syncStatus = SyncStatus.PENDING;

    // GETTERY I SETTERY
    //-------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GlobalProblem getProblem() {
        return problem;
    }

    public void setProblem(GlobalProblem problem) {
        this.problem = problem;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getAckUpdate() {
        return ackUpdate;
    }

    public void setAckUpdate(Boolean ackUpdate) {
        this.ackUpdate = ackUpdate;
    }

    public Integer getPreviousSeverity() {
        return previousSeverity;
    }

    public void setPreviousSeverity(Integer previousSeverity) {
        this.previousSeverity = previousSeverity;
    }

    public Integer getNewSeverity() {
        return newSeverity;
    }

    public void setNewSeverity(Integer newSeverity) {
        this.newSeverity = newSeverity;
    }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("problemId")
    public Long getProblemId() {
        return problem != null ? problem.getId() : null;
    }
}