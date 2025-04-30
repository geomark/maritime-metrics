package com.geomark.maritimemetrics.model;

import java.time.Instant;

/**
 * This class represents the speed difference between the actual and proposed speed of a vessel.
 * It contains a timestamp and the speed difference value.
 *
 * @param timestamp the timestamp of the point
 * @param v the speed difference value
 */
public record SpeedDifference(Instant timestamp, double v) {

}
