package pl.pjatk.alertwip.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.alertwip.dto.AlertsBySeverityDTO;
import pl.pjatk.alertwip.dto.ChartDataPointDTO;
import pl.pjatk.alertwip.model.TimeGranularity;
import pl.pjatk.alertwip.service.AnalyticsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/alerts-count")
    public ResponseEntity<List<AlertsBySeverityDTO>> getAlertsCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "DAY") TimeGranularity granularity) {

        return ResponseEntity.ok(analyticsService.getAlertsCount(start, end, granularity));
    }

    @GetMapping("/avg-close-time")
    public ResponseEntity<List<ChartDataPointDTO>> getAvgCloseTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "DAY") TimeGranularity granularity) {

        return ResponseEntity.ok(analyticsService.getAverageCloseTime(start, end, granularity));
    }

    @GetMapping("/avg-ack-time")
    public ResponseEntity<List<ChartDataPointDTO>> getAvgAckTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "DAY") TimeGranularity granularity) {

        return ResponseEntity.ok(analyticsService.getAverageAckTime(start, end, granularity));
    }
}