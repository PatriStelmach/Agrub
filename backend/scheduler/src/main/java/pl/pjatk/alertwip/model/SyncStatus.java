package pl.pjatk.alertwip.model;

public enum SyncStatus {
    PENDING,    // Oczekuje na wysłanie do zewnętrznego systemu
    SYNCED,     // Pomyślnie zsynchronizowano
    FAILED      // Błąd podczas synchronizacji (np. API Zabbixa nie odpowiedziało)
}