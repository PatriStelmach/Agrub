package pl.pjatk.alertwip.service.adapter;

import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;

@Component
public class DummyAdapter implements AlertSourceAdapter {

    @Override
    public boolean supports(String originType) {
        return originType == null || originType.equalsIgnoreCase("SCRIPT");
    }

    @Override
    public boolean sendAction(ActionRequestDTO request, GlobalProblem problem) {
        // olewamy i dajemy tylko do bazy bo nie idzie to dalej
        System.out.println("[DummyAdapter] Akcja lokalna. Brak zewnętrznego API dla systemu: " + problem.getOriginType());
        return true;
    }
}