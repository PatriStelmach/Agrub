package pl.pjatk.alertwip.model;

public enum ActionType {
    ACK,                // Zatwierdzenie (Acknowledge)
    UNACK,              // Cofnięcie zatwierdzenia
    SEVERITY_CHANGE,    // Ręczna zmiana ważności
    COMMENT             // Sam komentarz bez zmiany statusu
}