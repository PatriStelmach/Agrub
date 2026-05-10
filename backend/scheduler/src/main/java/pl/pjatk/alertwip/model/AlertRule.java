package pl.pjatk.alertwip.model;

import jakarta.persistence.*;

@Entity
public class AlertRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserGroup userGroup;

    // Widocznosc
    private String sourcePattern;
    @Enumerated(EnumType.STRING)
    private MatchType sourceType = MatchType.CONTAINS;

    private String contentPattern;
    @Enumerated(EnumType.STRING)
    private MatchType contentType = MatchType.CONTAINS;

    private String subjectPattern;
    @Enumerated(EnumType.STRING)
    private MatchType subjectMatchType = MatchType.CONTAINS;

    private String originPattern;
    @Enumerated(EnumType.STRING)
    private MatchType originMatchType = MatchType.CONTAINS;


    //notyfikacje
    private int minSeverity = 1;
    private boolean playSound = false;

    // --- GETTERY I SETTERY ---

    public String getSubjectPattern() {
        return subjectPattern;
    }

    public void setSubjectPattern(String subjectPattern) {
        this.subjectPattern = subjectPattern;
    }

    public MatchType getSubjectMatchType() {
        return subjectMatchType;
    }

    public void setSubjectMatchType(MatchType subjectMatchType) {
        this.subjectMatchType = subjectMatchType;
    }

    public String getOriginPattern() {
        return originPattern;
    }

    public void setOriginPattern(String originPattern) {
        this.originPattern = originPattern;
    }

    public MatchType getOriginMatchType() {
        return originMatchType;
    }

    public void setOriginMatchType(MatchType originMatchType) {
        this.originMatchType = originMatchType;
    }

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