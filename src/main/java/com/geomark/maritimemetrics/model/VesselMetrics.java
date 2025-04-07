package com.geomark.maritimemetrics.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

/**
 * This class represents the metrics of a vessel (Cassandra Table).
 */
@Data
@Table("vessel_metrics")
@ToString
public class VesselMetrics {
    @PrimaryKey
    private VesselMetricsKey key;

    private Double actualSpeed;
    private Double proposedSpeed;
    private Double fuelConsumption;
    private Double engineRpm;
    private Double latitude;
    private Double longitude;
    private Boolean isvalid;

    private List<DataQualityIssue> dataQualityIssues;
}
