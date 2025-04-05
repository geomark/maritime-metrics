package com.geomark.maritimemetrics.repository;

import com.geomark.maritimemetrics.model.VesselMetrics;
import com.geomark.maritimemetrics.model.VesselMetricsKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * Non-reactive Repository interface for VesselMetrics.
 */
public interface VesselMetricsRepository extends CassandraRepository<VesselMetrics, VesselMetricsKey> {

    @Query("SELECT * FROM vessel_metrics WHERE vessel_id = ?0  AND isvalid = true ALLOW FILTERING")
    Slice<VesselMetrics> fetchValidByVesselId(String vesselId, Pageable pageable);

}
