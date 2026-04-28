package pl.pjatk.alertwip.service.adapter;

import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;

public interface AlertSourceAdapter {

    // Metoda sprawdza, czy ten adapter potrafi obsłużyć dane źródło (np. "ZABBIX")
    boolean supports(String originType);

    // Główna metoda wykonująca akcję w zewnętrznym systemie.
    // Zwraca true jeśli się udało, false w przypadku błędu API.
    boolean sendAction(ActionRequestDTO request, GlobalProblem problem);
}