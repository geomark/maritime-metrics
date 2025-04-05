package com.geomark.maritimemetrics.repository;

import com.geomark.maritimemetrics.model.DataQualityIssue;
import com.geomark.maritimemetrics.model.VesselMetrics;
import com.geomark.maritimemetrics.model.VesselMetricsKey;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.Map;


/**
 * Reactive Repository interface for VesselMetrics.
 */
public interface VesselMetricsReactiveRepository extends ReactiveCassandraRepository<VesselMetrics, VesselMetricsKey> {

    @Query("SELECT * FROM vessel_metrics WHERE vessel_id = ?0  AND isvalid = true ALLOW FILTERING")
    Flux<VesselMetrics> fetchValidByVesselId(String vesselId);

    @Query("SELECT * FROM vessel_metrics WHERE vessel_id = ?0  AND isvalid = false ALLOW FILTERING")
    Flux<VesselMetrics> fetchInvalidByVesselId(String vesselId);

    @Query("SELECT * FROM vessel_metrics WHERE vessel_id = ?0  AND isvalid = false and dataqualityissues  CONTAINS ?1 ALLOW FILTERING")
    Flux<VesselMetrics> fetchInvalidByVesselIdAndDataQualityIssue(String vesselId, DataQualityIssue issue);

    @Query("SELECT * FROM vessel_metrics WHERE vessel_id = ?0 AND timestamp >= ?1 AND timestamp <= ?2 AND isvalid = true ALLOW FILTERING")
    Flux<VesselMetrics> fetchByVesselIdAndTimestampBetween(String vesselId, Instant start, Instant end);

    @Query("SELECT vessel_id, AVG(proposedspeed - actualspeed) AS compliance from maritime.vessel_metrics  WHERE isvalid = true  GROUP BY vessel_id ALLOW FILTERING;")
    Flux<Map<Double, Object>> fetchVehicleRankings();


}
