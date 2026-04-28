package pl.pjatk.alertwip.service.adapter;

import org.springframework.stereotype.Component;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.ProblemAction;

@Component
public class WazuhAdapter implements AlertSourceAdapter {

    @Override
    public boolean supports(String originType) {
        return "WAZUH".equalsIgnoreCase(originType);
    }

    @Override
    public boolean sendAction(ProblemAction action, GlobalProblem problem) {
        System.out.println("[WAZUH ADAPTER] Żądanie akcji: " + action.getActionType() + " dla " + problem.getUniqueKey());

        // Ponieważ Wazuh nie wymaga aktywnego wysyłania komend "ACK" do API
        // tak jak robi to Zabbix, po prostu zwracamy true,
        // co oznacza, że akcja powiodła się po naszej stronie lokalnie.
        return true;
    }
}