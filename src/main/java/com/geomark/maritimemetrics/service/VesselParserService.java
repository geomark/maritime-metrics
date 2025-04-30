package com.geomark.maritimemetrics.service;


import com.geomark.maritimemetrics.model.VesselMetrics;
import com.geomark.maritimemetrics.model.VesselMetricsKey;
import de.siegmar.fastcsv.reader.CsvRecord;
import lombok.extern.slf4j.Slf4j;
import net.iakovlev.timeshape.TimeZoneEngine;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
public class VesselParserService {

    private final TimeZoneEngine engine;

    public VesselParserService(){
        // Initialize the TimeZoneEngine
        this.engine = TimeZoneEngine.initialize();
    }


    /**
     * Parses a CSV record into a VesselMetrics object.
     *
     * @param line the CSV record to parse
     * @return a VesselMetrics object
     */
    public  VesselMetrics parseMetrics(CsvRecord line) {
        // Parse the CSV line into a VesselMetrics object
        // This is a placeholder for actual parsing logic

        log.info("Entered Line" + line.getStartingLineNumber());

        if (line.getStartingLineNumber() == 1) {
            // Skip the header line
            return new VesselMetrics();
        }

        String vessel_code = line.getField(0);
        String datetime = line.getField(1);
        String latitude = line.getField(2);
        String longitude = line.getField(3);
        String power = line.getField(4);
        String fuel_consumption = line.getField(5);
        String actual_speed_overground = line.getField(6);
        String proposed_speed_overground = line.getField(7);
        String predicted_fuel_consumption = line.getField(8);

        VesselMetrics newItem = new VesselMetrics();

        VesselMetricsKey key = new VesselMetricsKey();
        key.setVesselId(vessel_code);

        newItem.setLatitude(csvValueToDouble(latitude));
        newItem.setLongitude(csvValueToDouble(longitude));
        newItem.setFuelConsumption(csvValueToDouble(predicted_fuel_consumption));
        newItem.setEngineRpm(csvValueToDouble(power));
        newItem.setFuelConsumption(csvValueToDouble(fuel_consumption));
        newItem.setActualSpeed(csvValueToDouble(actual_speed_overground));
        newItem.setProposedSpeed(csvValueToDouble(proposed_speed_overground));

        Instant timestamp = LocalDateTime.parse(datetime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone( getZoneIdFromCoordinates(newItem.getLatitude(), newItem.getLongitude()))
                .toInstant();

        key.setTimestamp(timestamp);

        newItem.setKey(key);


        return newItem;
    }


    /**
     * @param value
     * @return
     */
    private  Double csvValueToDouble(String value) {
        if (value == null || value.equals("NULL")) {
            return null;
        }
        return Double.parseDouble(value);
    }

    /**
     * @param latitude
     * @param longitude
     * @return
     */
    private  ZoneId getZoneIdFromCoordinates(Double latitude, Double longitude) {
        if(latitude == null || longitude == null){
            return java.time.ZoneId.of("UTC");
        }
        // Convert latitude and longitude to a ZoneId
        return engine.query(latitude, longitude)
                .orElse(java.time.ZoneId.of("UTC"));
    }

}
