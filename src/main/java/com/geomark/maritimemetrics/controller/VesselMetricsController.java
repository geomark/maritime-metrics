package com.geomark.maritimemetrics.controller;

import com.geomark.maritimemetrics.model.DataQualityIssue;
import com.geomark.maritimemetrics.model.ImportResult;
import com.geomark.maritimemetrics.model.SpeedDifference;
import com.geomark.maritimemetrics.model.VesselMetrics;
import com.geomark.maritimemetrics.service.VesselMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * This controller handles HTTP requests related to vessel metrics.
 */
@RestController
@RequestMapping("/api/vessels")
@RequiredArgsConstructor
public class VesselMetricsController {

    private final VesselMetricsService metricsService;

    @MessageMapping("/ingestionResults")
    public ImportResult notifiyComplete(ImportResult message) throws Exception {
        return message;
    }



    @Operation(tags = "Task 0 (Initial ingest) ", summary = "Ingests a CSV file containing vessel metrics.")
    @PostMapping(value = "/ingest", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<Void> ingestMetrics(@RequestPart MultipartFile file) throws IOException {
        metricsService.processAndSaveMetrics(file);
        return Mono.empty();
    }


    @Operation(tags = "Task 1", summary = "Returns a list of speed differences for a given vessel.")
    @GetMapping("/{vesselId}/speed-differences")
    public Slice<SpeedDifference> getSpeedDifferences(@PathVariable String vesselId,
                                                      @RequestParam(required = false) int pageNo,
                                                      @RequestParam(required = false) int pageSize,
                                                      @RequestParam(required = false) Sort.Direction sortBy) {
        return metricsService.getSpeedDifferences(vesselId, pageNo, pageSize, sortBy);
    }


    @Operation(tags = "Task 2", summary = "Returns a Map of data quality issues for a given vessel.")
    @GetMapping("/{vesselId}/data-issues")
    public Mono<Map<DataQualityIssue, Long>> getDataQualityIssues(@PathVariable String vesselId) {
        return metricsService.getDataQualityIssues(vesselId);
    }


    @Operation(tags = "Task 3", summary = "Returns vehicle compliance statistics.")
    @GetMapping("/vehicle-compliance-stats")
    public Flux<Map<Double, Object>> vehicleComplianceStats() {
        return metricsService.vehicleComplianceStats();
    }

    @Operation(operationId = "4", tags = "Task 4", summary = "Returns a list of vessel metrics for a given vessel and time range.")
    @GetMapping("/{vesselId}/vessel-metrics")
    public Flux<VesselMetrics> getVesselMetrics(@PathVariable String vesselId,
                                                @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime from,
                                                @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime to) {
        Instant start = from.toInstant(java.time.ZoneOffset.UTC);
        Instant end = to.toInstant(java.time.ZoneOffset.UTC);
        return metricsService.getVesselMetrics(vesselId, start, end);
    }


    @Operation(tags = "Task 5", summary = " Returns a Map of grouped data quality issues for a given vessel.")
    @GetMapping("/{vesselId}/grouped-by-data-issues")
    public Mono<Map<DataQualityIssue, List<VesselMetrics>>> groupProblematicRecords(@PathVariable String vesselId,
                                                                                    @RequestParam(required = false) DataQualityIssue issue) {
        return metricsService.groupVesselMetricsByDataQualityIssue(vesselId, issue);
    }

}