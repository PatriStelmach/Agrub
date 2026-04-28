package pl.pjatk.alertwip.service.adapter;

import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.model.GlobalProblem;

@Component
public class WazuhAdapter implements AlertSourceAdapter {

    @Override
    public boolean supports(String originType) {
        return "WAZUH".equalsIgnoreCase(originType);
    }

    @Override
    public boolean sendAction(ActionRequestDTO request, GlobalProblem problem) {
        System.out.println("[WAZUH ADAPTER] Żądanie aktualizacji dla " + problem.getUniqueKey());
        return true;
    }
}