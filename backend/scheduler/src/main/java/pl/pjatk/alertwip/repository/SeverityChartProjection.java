package pl.pjatk.alertwip.repository;

public interface SeverityChartProjection {
    Long getBucketTimestamp();
    Integer getSeverity();
    Long getTotalCount();
}