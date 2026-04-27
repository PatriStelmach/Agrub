package pl.pjatk.alertwip.model;

import jakarta.persistence.*;

@Entity
public class AlertRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserGroup userGroup;

    private String sourcePattern;
    @Enumerated(EnumType.STRING)
    private MatchType sourceType = MatchType.CONTAINS;

    private String contentPattern;
    @Enumerated(EnumType.STRING)
    private MatchType contentType = MatchType.CONTAINS;

    private int minSeverity = 1;
    private boolean playSound = false;

    // --- GETTERY I SETTERY ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserGroup getUserGroup() { return userGroup; }
    public void setUserGroup(UserGroup userGroup) { this.userGroup = userGroup; }
    public String getSourcePattern() { return sourcePattern; }
    public void setSourcePattern(String sourcePattern) { this.sourcePattern = sourcePattern; }
    public MatchType getSourceType() { return sourceType; }
    public void setSourceType(MatchType sourceType) { this.sourceType = sourceType; }
    public String getContentPattern() { return contentPattern; }
    public void setContentPattern(String contentPattern) { this.contentPattern = contentPattern; }
    public MatchType getContentType() { return contentType; }
    public void setContentType(MatchType contentType) { this.contentType = contentType; }
    public int getMinSeverity() { return minSeverity; }
    public void setMinSeverity(int minSeverity) { this.minSeverity = minSeverity; }
    public boolean isPlaySound() { return playSound; }
    public void setPlaySound(boolean playSound) { this.playSound = playSound; }
}