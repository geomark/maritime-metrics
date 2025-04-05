package com.geomark.maritimemetrics.service;

import com.geomark.maritimemetrics.model.DataQualityIssue;
import com.geomark.maritimemetrics.model.VesselMetrics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This service is responsible for validating the VesselMetrics data.
 * It checks for missing or invalid values and adds data quality issues to the metrics.
 * It can be used to ensure that the data is clean and ready for processing.
 */
@Service
public class VesselMetricsValidationService {

    /**
     * Validates the given VesselMetrics object.
     * Checks for missing or invalid values and adds data quality issues to the metrics.
     *
     * @param metric The VesselMetrics object to validate.
     */
    public void validateMetrics(VesselMetrics metric) {
        List<DataQualityIssue> issues = new ArrayList<>();

        if (metric.getActualSpeed() == null || metric.getProposedSpeed() == null
                || metric.getLatitude() == null || metric.getLongitude() == null) {
            issues.add(DataQualityIssue.MISSING_DATA);
        } else if (metric.getActualSpeed() < 0) {
            issues.add(DataQualityIssue.INVALID_DATA);
        }

        if (metric.getFuelConsumption() != null && metric.getFuelConsumption() < 0) {
            issues.add(DataQualityIssue.INVALID_DATA);
        }

        if (metric.getEngineRpm() != null && metric.getEngineRpm() < 0) {
            issues.add(DataQualityIssue.INVALID_DATA);
        }

        metric.setIsvalid(issues.isEmpty());

        // Add other validations
        metric.setDataQualityIssues(issues);
    }
}
