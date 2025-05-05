package com.geomark.maritimemetrics.service;


import com.geomark.maritimemetrics.model.VesselMetrics;
import com.geomark.maritimemetrics.repository.VesselMetricsReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.function.Consumer;

/**
 * This service is responsible for processing VesselMetrics data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VesselProcessorService implements Consumer<List<VesselMetrics>> {

    private final VesselMetricsReactiveRepository reactiveRepository;

    private final VesselMetricsValidationService validationService;



    /**
     * Processes the input list of VesselMetrics.
     * This method is called when the input list is ready for processing.
     *
     * @param o the input argument
     */
    @Override
    public void accept(List<VesselMetrics> o) {
        List<VesselMetrics> revList = o.reversed();
        VesselMetrics current = revList.removeFirst();

        calculateDerivedMetrics(current, revList);
        validationService.validateMetrics(current);

        log.info("Processing Point: {}", current.getKey());
        reactiveRepository.insert(current)
                .doOnSuccess(savedMetric -> {
                    log.info("Saved metric: {}", savedMetric);
                })
                .doOnError(e -> {
                    log.error("Error saving metric: {}", e.getMessage());
                }).subscribe();
    }


    /**
     * Calculates derived metrics for the given VesselMetrics object.
     *
     * @param metric  the VesselMetrics object to calculate derived metrics for
     * @param context the context in which to calculate the derived metrics
     */
    private void calculateDerivedMetrics(VesselMetrics metric, List<VesselMetrics> context) {
        context.stream()
                .filter(mtr -> mtr.getKey().getVesselId().equals(metric.getKey().getVesselId()))
                .filter(mtr -> mtr.getLatitude() != null || mtr.getLongitude() != null)
                .findFirst().ifPresent(mtr -> {
                    if (metric.getLatitude() != null && metric.getLongitude() != null && mtr.getLatitude() != null && mtr.getLongitude() != null) {
                        double actualDistance = calculateDistance(metric.getLatitude(), metric.getLongitude(), mtr.getLatitude(), mtr.getLongitude());
                        double timeDifference = (metric.getKey().getTimestamp().toEpochMilli() - mtr.getKey().getTimestamp().toEpochMilli()) / 3600000.0;
                        // in hours
                        double actualSpeed = calculateSpeed(actualDistance, timeDifference);
                        metric.setActualSpeed(actualSpeed);
                    }
                });
    }


    /**
     * Calculates the distance between two geographical points using the Haversine formula.
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);

        return Math.sqrt(x * x + y * y) * 6371;
    }


    /**
     * Calculates the speed based on distance and time.
     *
     * @param distance
     * @param time
     * @return
     */
    private double calculateSpeed(double distance, double time) {
        return distance / time;
    }


}
