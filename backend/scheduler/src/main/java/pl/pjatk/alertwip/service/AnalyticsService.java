package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AlertsBySeverityDTO;
import pl.pjatk.alertwip.dto.ChartDataPointDTO;
import pl.pjatk.alertwip.repository.ChartDataProjection;
import pl.pjatk.alertwip.model.TimeGranularity;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;
import pl.pjatk.alertwip.repository.SeverityChartProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final GlobalProblemRepository problemRepository;

    public AnalyticsService(GlobalProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    // Ilość alertów w czasie
    public List<ChartDataPointDTO> getAlertsCount(LocalDateTime start, LocalDateTime end, TimeGranularity granularity) {
        List<ChartDataProjection> results = problemRepository.countAlertsByGranularity(
                start, end, mapGranularity(granularity)
        );
        return mapToDTO(results);
    }

    // sredni czas zamknięcia
    public List<ChartDataPointDTO> getAverageCloseTime(LocalDateTime start, LocalDateTime end, TimeGranularity granularity) {
        List<ChartDataProjection> results = problemRepository.avgCloseTimeByGranularity(
                start, end, mapGranularity(granularity)
        );
        return mapToDTO(results);
    }

    // średni czas do pierwszego ACK
    public List<ChartDataPointDTO> getAverageAckTime(LocalDateTime start, LocalDateTime end, TimeGranularity granularity) {
        List<ChartDataProjection> results = problemRepository.avgAckTimeByGranularity(
                start, end, mapGranularity(granularity)
        );
        return mapToDTO(results);
    }

    public List<AlertsBySeverityDTO> getAlertsCountBySeverity(LocalDateTime start, LocalDateTime end, TimeGranularity granularity) {
        // 1. Pobranie płaskich wyników z bazy MySQL
        List<SeverityChartProjection> rows = problemRepository.countAlertsBySeverityAndGranularity(
                start, end, mapGranularity(granularity)
        );

        // 2. Grupowanie po timestampie (Chrono-porządek zapewnia TreeMap)
        Map<Long, List<SeverityChartProjection>> groupedByTimestamp = rows.stream()
                .collect(Collectors.groupingBy(
                        SeverityChartProjection::getBucketTimestamp,
                        java.util.TreeMap::new,
                        Collectors.toList()
                ));

        // 3. Mapowanie na końcowe DTO z pełną inicjalizacją poziomów severity (0-5)
        return groupedByTimestamp.entrySet().stream()
                .map(entry -> {
                    Long timestamp = entry.getKey();

                    // Przygotowanie bezpiecznej mapy z domyślnymi zerami dla poziomów 0-5
                    Map<Integer, Long> severityMap = new java.util.HashMap<>();
                    for (int i = 0; i <= 5; i++) {
                        severityMap.put(i, 0L);
                    }

                    // Uzupelnienie mapy faktycznymi danymi z bazy
                    for (SeverityChartProjection row : entry.getValue()) {
                        if (row.getSeverity() != null) {
                            severityMap.put(row.getSeverity(), row.getTotalCount());
                        }
                    }

                    return new AlertsBySeverityDTO(timestamp, severityMap);
                })
                .toList();
    }
    

    private String mapGranularity(TimeGranularity granularity) {
        return switch (granularity) {
            case DAY -> "day";
            case WEEK -> "week";
            case MONTH -> "month";
        };
    }

    private List<ChartDataPointDTO> mapToDTO(List<ChartDataProjection> projections) {
        return projections.stream()
                .map(p -> new ChartDataPointDTO(
                        p.getBucketTimestamp(),
                        p.getTotalValue() != null ? p.getTotalValue() : 0.0
                ))
                .toList();
    }
}