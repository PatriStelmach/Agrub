package pl.pjatk.alertwip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.pjatk.alertwip.model.ActionType;

public record ActionRequestDTO (
        @JsonProperty("author")
        String author,

        @JsonProperty("message")
        String message,

        @JsonProperty("ack")
        Boolean acknowledge,

        @JsonProperty("newSeverity")
        Integer newSeverity
) {}