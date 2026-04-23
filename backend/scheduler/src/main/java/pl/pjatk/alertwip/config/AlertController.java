package pl.pjatk.alertwip.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.service.SseNotifService;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final SseNotifService sseService;
    private final GlobalProblemRepository problemRepository;

    public AlertController(SseNotifService sseService, GlobalProblemRepository problemRepository) {
        this.sseService = sseService;
        this.problemRepository = problemRepository;
    }

     //Pobieranie historii
    @GetMapping("/active")
    public List<GlobalProblem> getActiveAlerts() {
        return problemRepository.findAll();
    }

    //Nasłuchiwanie na żywo
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAlerts() {
        // Wypisujemy w konsoli log, żebyś wiedział, kiedy Vue się podłącza
        System.out.println("[SSE] Nowy klient podłączony do strumienia alertów.");
        return sseService.subscribe();
    }
}