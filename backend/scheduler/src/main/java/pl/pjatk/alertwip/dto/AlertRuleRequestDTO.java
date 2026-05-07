package pl.pjatk.alertwip.dto;

import pl.pjatk.alertwip.model.MatchType;

public record AlertRuleRequestDTO (
    Long userGroupId,
    String sourcePattern,
    MatchType sourceType,
    String contentPattern,
    MatchType contentType,
    String originPattern,
    MatchType originMatchType,
    String subjectPattern,
    MatchType subjectMatchType,
    int minSeverity,
    boolean playSound
){}
