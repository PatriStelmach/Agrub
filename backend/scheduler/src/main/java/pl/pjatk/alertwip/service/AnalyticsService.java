package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AlertsBySeverityDTO;
import pl.pjatk.alertwip.dto.ChartDataPointDTO;
import pl.pjatk.alertwip.model.GlobalProblem;
import pl.pjatk.alertwip.model.TimeGranularity;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final GlobalProblemRepository problemRepository;

    public AnalyticsService(GlobalProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    // 1. WYKRES: Ilość alertów w czasie (Oś Y = Ilość sztuk)
    public List<AlertsBySeverityDTO> getAlertsCount(LocalDateTime start, LocalDateTime end, TimeGranularity granularity) {
        List<GlobalProblem> problems = problemRepository.findAllByCreatedAtBetween(start, end);

        Map<Long, Map<Integer, Long>> grouped = problems.stream()
                .collect(Collectors.groupingBy(
                        p -> getBucketTimestamp(p.getCreatedAt(), granularity),
                        TreeMap::new,
                        Collectors.groupingBy(
                                GlobalProblem::getSeverity,
                                Collectors.counting()
                        )
                ));

        return grouped.entrySet().stream()
                .map(e -> new AlertsBySeverityDTO(e.getKey(), e.getValue()))
                .toList();
    }

    // 2. WYKRES: Średni czas zamknięcia (Oś Y = SEKUNDY)
    public List<ChartDataPointDTO> getAverageCloseTime(LocalDateTime start, LocalDateTime end, TimeGranularity granularity) {
        List<GlobalProblem> problems = problemRepository.findAllByCreatedAtBetween(start, end);

        Map<Long, Double> grouped = problems.stream()
                .filter(p -> "Done".equals(p.getStatus()) && p.getClosedAt() != null)
                .collect(Collectors.groupingBy(
                        p -> getBucketTimestamp(p.getCreatedAt(), granularity),
                        TreeMap::new,
                        Collectors.averagingDouble(p -> Duration.between(p.getCreatedAt(), p.getClosedAt()).getSeconds())
                ));

        return mapToDataPoints(grouped);
    }

    // 3. WYKRES: Średni czas do pierwszego ACK (Oś Y = SEKUNDY)
    public List<ChartDataPointDTO> getAverageAckTime(LocalDateTime start, LocalDateTime end, TimeGranularity granularity) {
        List<GlobalProblem> problems = problemRepository.findAllByCreatedAtBetween(start, end);

        Map<Long, Double> grouped = problems.stream()
                .filter(p -> p.isAcknowledged() && p.getAcknowledgedAt() != null)
                .collect(Collectors.groupingBy(
                        p -> getBucketTimestamp(p.getCreatedAt(), granularity),
                        TreeMap::new,
                        Collectors.averagingDouble(p -> Duration.between(p.getCreatedAt(), p.getAcknowledgedAt()).getSeconds())
                ));

        return mapToDataPoints(grouped);
    }

    // --- METODY POMOCNICZE ---

    private List<ChartDataPointDTO> mapToDataPoints(Map<Long, Double> map) {
        return map.entrySet().stream()
                .map(e -> new ChartDataPointDTO(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0))
                .toList();
    }

    private Long getBucketTimestamp(LocalDateTime date, TimeGranularity granularity) {
        if (date == null) return 0L;

        LocalDateTime truncated = switch (granularity) {
            case DAY -> date.truncatedTo(ChronoUnit.DAYS); // Ucina do 00:00:00
            case WEEK -> date.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS); // Cofa do najbliższego poniedziałku 00:00:00
            case MONTH -> date.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS); // Cofa do 1. dnia miesiąca 00:00:00
        };

        return truncated.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}