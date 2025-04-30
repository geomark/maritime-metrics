package com.geomark.maritimemetrics.service;

import com.geomark.maritimemetrics.exceptions.DataProcessingException;
import com.geomark.maritimemetrics.model.DataQualityIssue;
import com.geomark.maritimemetrics.model.SpeedDifference;
import com.geomark.maritimemetrics.model.VesselMetrics;
import com.geomark.maritimemetrics.repository.VesselMetricsReactiveRepository;
import com.geomark.maritimemetrics.repository.VesselMetricsRepository;
import com.geomark.maritimemetrics.util.CSVReaderProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This service is responsible for implementing the business logic required by the VesselMetricsController
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VesselMetricsService {

    private final VesselMetricsReactiveRepository reactiveRepository;

    private final VesselMetricsRepository repository;

    private final VesselProcessorService processorService;

    private final VesselParserService  parserService;

    /**
     * Processes the CSV file and saves the metrics to the database.
     *
     * @param csvFile the CSV file to process
     * @return a Mono containing the count of processed metrics
     * @throws IOException if an error occurs while reading the file
     */
    public Mono<Long> processAndSaveMetrics(MultipartFile csvFile) throws IOException {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        // Read the CSV file and convert it to a list of VesselMetrics objects

        Flux<List<VesselMetrics>> fl = CSVReaderProvider.ofReader(csvFile)
                .map(parserService::parseMetrics)
                .filter(metric -> metric.getKey() != null)
                .buffer(10, 1)

                .onErrorContinue((throwable, o) -> {
                            log.error("Error processing line: {}", o);
                            log.error("Error message: {}", throwable.getMessage());
                        })
                .doOnComplete(() -> log.info("Completed processing metrics"));
//                .parallel()
//                .runOn(s);

        fl.subscribe(processorService);

        return Mono.empty();

    }


    /**
     * Fetches the speed differences for a given vessel.
     *
     * @param vesselId the ID of the vessel
     * @return a Slice of SpeedDifference objects
     */
    public Slice<SpeedDifference> getSpeedDifferences(String vesselId, int page, int size, Sort.Direction sort) {
        CassandraPageRequest pageable = CassandraPageRequest.of(page, size, sort, "timestamp");

        return repository.fetchValidByVesselId(vesselId, pageable)
                .map(metric -> new SpeedDifference(
                        metric.getKey().getTimestamp(),
                        metric.getActualSpeed() - metric.getProposedSpeed()
                ));

    }

    /**
     * Fetches the data quality issues for a given vessel.
     *
     * @param vesselId the ID of the vessel
     * @return a Mono containing a Map of DataQualityIssue and their counts
     */
    public Mono<Map<DataQualityIssue, Long>> getDataQualityIssues(String vesselId) {
        // Group the data quality issues by their type
        return reactiveRepository.fetchInvalidByVesselId(vesselId)
                .flatMapIterable(VesselMetrics::getDataQualityIssues)
                .collect(Collectors.groupingBy(feature -> feature, Collectors.summingLong(feature -> 1)));

    }

    /**
     * Groups the VesselMetrics by their data quality issues.
     *
     * @param vesselId the ID of the vessel
     * @return a Mono containing a Map of DataQualityIssue and their corresponding VesselMetrics
     */
    public Mono<Map<DataQualityIssue, List<VesselMetrics>>> groupVesselMetricsByDataQualityIssue(String vesselId, DataQualityIssue issue) {
        Flux<VesselMetrics> data;
        if (issue == null) {
            data = reactiveRepository.fetchInvalidByVesselId(vesselId);
        } else {
            data = reactiveRepository.fetchInvalidByVesselIdAndDataQualityIssue(vesselId, issue);
        }

        return data
                .flatMap(vm -> Flux.fromIterable(vm.getDataQualityIssues())
                        .map(feature -> Map.entry(feature, vm)))

                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    /**
     * Fetches the vehicles compliance statistics.
     *
     * @return a Flux of Maps containing the compliance statistics
     */
    public Flux<Map<Double, Object>> vehicleComplianceStats() {

        return reactiveRepository.fetchVehicleRankings();
    }


    public Flux<VesselMetrics> getVesselMetrics(String vesselId, Instant from, Instant to) {
        if (from != null && to != null) {
            return reactiveRepository.fetchByVesselIdAndTimestampBetween(vesselId, from, to);
        } else {
            return reactiveRepository.fetchValidByVesselId(vesselId);
        }
    }
}