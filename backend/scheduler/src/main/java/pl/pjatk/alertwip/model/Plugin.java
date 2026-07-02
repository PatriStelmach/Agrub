package pl.pjatk.alertwip.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Plugin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private int severity;

    private String description;

    private String name;
    @Column(updatable = false)
    private String creator;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Integer weight;

    @PrePersist
    protected void onCreate(){
        //data
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        //rozmiar
        if (this.code != null){
            this.weight = this.code.length();
        } else {
            this.weight = 0;
        }
    }

    @Column(updatable = false)
    private String language; // "py", "ps", "sh"

    private boolean isActive;

    @Column(columnDefinition = "TEXT")
    private String code;

    @ElementCollection
    private List<String> tags;


    public String getDescription() {
        return description;
    }

    public void setDescription(String decription) {
        this.description = decription;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }


    public String getLanguage() {
        return language;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}