package pl.pjatk.alertwip.dto;

import pl.pjatk.alertwip.model.ActionType;

public record ActionRequestDTO (

    String author,
    ActionType actionType,
    String message

) {}