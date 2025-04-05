package com.geomark.maritimemetrics.model;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;


@Data
@PrimaryKeyClass
public class VesselMetricsKey implements Serializable {
    @PrimaryKeyColumn(name = "vessel_id", type = PrimaryKeyType.PARTITIONED)
    private String vesselId;

    @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant timestamp;
}