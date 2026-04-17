package pl.pjatk.alertwip.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Plugin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(updatable = false)
    private String creator;
    @Column(updatable = false)
    private String language; // "py", "ps", "sh"

    private boolean isActive;

    @Column(columnDefinition = "TEXT")
    private String code;

    @ElementCollection
    private List<String> tags;


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
}