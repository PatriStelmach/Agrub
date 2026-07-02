package pl.pjatk.alertwip.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TaskExecutionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ScheduledTask task; //jakie to zadanie było

    private LocalDateTime executionTime; //kiedy odpalone

    @Column(columnDefinition = "TEXT")
    private String output; //Pełny log odpowiedzi (jakoś to później sparsujemy)

    private String status; // status odpowiedzi

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduledTask getTask() {
        return task;
    }

    public void setTask(ScheduledTask task) {
        this.task = task;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
