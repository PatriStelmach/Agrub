package pl.pjatk.alertwip.model;

import java.util.List;

public class LocalScriptDTO {
    private String fileName;
    private String description;
    private List<String> tags;
    private boolean isLog;

    public LocalScriptDTO(String fileName, String description, List<String> tags, boolean isLog) {
        this.fileName = fileName;
        this.description = description;
        this.tags = tags;
        this.isLog = isLog;
    }

    // Gettery i Settery
    public String getFileName() { return fileName; }
    public String getDescription() { return description; }
    public List<String> getTags() { return tags; }
    public boolean isLog() { return isLog; }
}