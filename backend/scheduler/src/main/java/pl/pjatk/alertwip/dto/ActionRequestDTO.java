package pl.pjatk.alertwip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.pjatk.alertwip.model.ActionType;

public record ActionRequestDTO (
        @JsonProperty("author")
        String author,

        @JsonProperty("message")
        String message,

        @JsonProperty("ack") // To zmapuje Twoje 'ack: boolean' z frontu
        Boolean acknowledge,

        @JsonProperty("newSeverity") // To zmapuje 'newSeverity: number'
        Integer newSeverity
) {}