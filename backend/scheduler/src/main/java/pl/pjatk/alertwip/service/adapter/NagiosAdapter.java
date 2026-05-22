package pl.pjatk.alertwip.service.adapter;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.pjatk.alertwip.dto.ActionRequestDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.service.NagiosApiService;

@Component
public class NagiosAdapter implements AlertSourceAdapter {

    private final NagiosApiService nagiosApiService;

    public NagiosAdapter(NagiosApiService nagiosApiService) {
        this.nagiosApiService = nagiosApiService;
    }

    @Override
    public boolean supports(String originType) {
        return "NAGIOS".equalsIgnoreCase(originType);
    }

    @Override
    public boolean sendAction(ActionRequestDTO request, GlobalProblem problem) {
        if (request.acknowledge() == null || !request.acknowledge()) {
            System.out.println("[NagiosAdapter] Brak flagi ACK. Ignorowanie.");
            return true;
        }

        if (problem.getSource() == null || problem.getExternalEventId() == null) {
            System.err.println("[NagiosAdapter] BŁĄD: Brak nazwy hosta lub ID usługi dla alertu Nagios!");
            return false;
        }

        String host = problem.getSource();
        MultiValueMap<String, String> body = buildCgiPayload(request, problem, host);

        try {
            nagiosApiService.sendCgiCommand(body);
            System.out.println("[NagiosAdapter] Pomyślnie wysłano ACK do Nagiosa dla hosta: " + host);
            return true;

        } catch (Exception e) {
            System.err.println("[NagiosAdapter] Błąd komunikacji z API Nagiosa: " + e.getMessage());
            return false;
        }
    }

    private static MultiValueMap<String, String> buildCgiPayload(ActionRequestDTO request, GlobalProblem problem, String host) {
        String serviceDesc = problem.getExternalEventId();
        String author = (request.author() != null && !request.author().isBlank()) ? request.author() : "AlertWIP API";
        String message = (request.message() != null && !request.message().isBlank()) ? request.message() : "Potwierdzono z zewnętrznego panelu.";

        // Weryfikacja czy problem dotyczy całego Hosta czy tylko Usługi
        boolean isHostAlert = "HOST DOWN".equals(serviceDesc) || serviceDesc.startsWith(host + "_HOST DOWN");
        String cmdTyp = isHostAlert ? "33" : "34";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cmd_typ", cmdTyp);
        body.add("cmd_mod", "2");
        body.add("host", host);
        if (!isHostAlert) {
            body.add("service", serviceDesc);
        }
        body.add("sticky_ack", "on");
        body.add("send_notification", "on");
        body.add("persistent", "on");
        body.add("com_author", author);
        body.add("com_data", message);
        body.add("btnSubmit", "Commit");
        return body;
    }
}