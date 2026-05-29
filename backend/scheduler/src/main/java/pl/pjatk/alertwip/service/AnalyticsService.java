package pl.pjatk.alertwip.service;

import org.springframework.stereotype.Service;
import pl.pjatk.alertwip.dto.AlertsBySeverityDTO;
import pl.pjatk.alertwip.dto.ChartDataPointDTO;
import pl.pjatk.alertwip.repository.ChartDataProjection;
import pl.pjatk.alertwip.model.TimeGranularity;
import pl.pjatk.alertwip.repository.GlobalProblemRepository;

import java.time.LocalDateTime;
import java.util.List;

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