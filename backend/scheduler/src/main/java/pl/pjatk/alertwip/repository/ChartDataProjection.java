package pl.pjatk.alertwip.repository;

public interface ChartDataProjection {
    Long getBucketTimestamp();
    Double getTotalValue();
}