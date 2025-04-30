package com.geomark.maritimemetrics.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;


/**
 *  This class represents the primary key for the VesselMetrics table in Cassandra.
 *  It consists of two fields: vesselId and timestamp.
 */
@Data
@PrimaryKeyClass
@ToString
public class VesselMetricsKey implements Serializable {
    @PrimaryKeyColumn(name = "vessel_id", type = PrimaryKeyType.PARTITIONED)
    private String vesselId;

    @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant timestamp;
}