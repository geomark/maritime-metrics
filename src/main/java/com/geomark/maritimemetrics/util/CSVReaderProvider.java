package com.geomark.maritimemetrics.util;

import com.geomark.maritimemetrics.model.VesselMetrics;
import com.geomark.maritimemetrics.model.VesselMetricsKey;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * This class is responsible for parsing CSV files containing vessel metrics
 * and converting them into a stream of VesselMetrics objects.
 * It uses the FastCSV library for reading CSV files.
 */
@Slf4j
public class CSVReaderProvider {


    /**
     *  Private constructor to prevent instantiation.
     */
    private CSVReaderProvider() {
        // Prevent instantiation
    }


    /**
     * Factory method that reads the CSV file and converts it to a stream of CsvRecord objects.
     *
     * @param file the CSV file to read
     * @return a Flux of CsvRecord objects
     * @throws IOException
     */
    public static Flux<CsvRecord> ofReader(MultipartFile file) throws IOException {
        CSVReaderProvider parser = new CSVReaderProvider();
        return Flux.fromIterable(parser.reader(file.getInputStream()));
    }


    /**
     * Reads the CSV file and converts it to a stream of CsvRecord objects.
     * @param csvFile the CSV file to read
     * @return a CsvReader object
     * @throws IOException
     */
    private CsvReader<CsvRecord> reader(InputStream csvFile) {
        // Read the CSV file and convert it to a list of VesselMetrics objects
        return CsvReader.builder()
                .skipEmptyLines(true)
                .detectBomHeader(true)
                .fieldSeparator(',')
                .ofCsvRecord(csvFile);
    }

    /**
     * Parses a CSV record into a VesselMetrics object.
     * @param line  the CSV record to parse
     * @return a VesselMetrics object
     */
    public static VesselMetrics parseMetrics(CsvRecord line) {
        // Parse the CSV line into a VesselMetrics object
        // This is a placeholder for actual parsing logic

        log.info("Entered Line" + line.getStartingLineNumber());

        if(line.getStartingLineNumber() == 1){
            // Skip the header line
            return  new VesselMetrics();
        }

        String vessel_code  = line.getField(0);
        String datetime  = line.getField(1);
        String latitude  = line.getField(2);
        String longitude  = line.getField(3);
        String power  = line.getField(4);
        String fuel_consumption = line.getField(5);
        String actual_speed_overground = line.getField(6);
        String proposed_speed_overground  = line.getField(7);
        String predicted_fuel_consumption  = line.getField(8);

        VesselMetrics newItem = new VesselMetrics();

        VesselMetricsKey key = new VesselMetricsKey();
        key.setVesselId(vessel_code);


        Instant timestamp =  LocalDateTime.parse(datetime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone(java.time.ZoneId.of("UTC"))
                .toInstant();

        key.setTimestamp(timestamp);

        newItem.setKey(key);

        if(latitude != null && !latitude.equals("NULL")){
            newItem.setLatitude(Double.parseDouble(latitude));
        }

        if(longitude != null && !longitude.equals("NULL")){
            newItem.setLongitude(Double.parseDouble(longitude));
        }
        if(predicted_fuel_consumption != null  && !predicted_fuel_consumption.equals("NULL")){
            newItem.setFuelConsumption(Double.parseDouble(predicted_fuel_consumption));
        }
        if(power != null  && !power.equals("NULL")){
            newItem.setEngineRpm(Double.parseDouble(power));
        }
        if(fuel_consumption != null  && !fuel_consumption.equals("NULL")){
            newItem.setFuelConsumption(Double.parseDouble(fuel_consumption));
        }
        if(actual_speed_overground != null  && !actual_speed_overground.equals("NULL")){
            newItem.setActualSpeed(Double.parseDouble(actual_speed_overground));
        }
        if(proposed_speed_overground != null  && !proposed_speed_overground.equals("NULL")){
            newItem.setProposedSpeed(Double.parseDouble(proposed_speed_overground));
        }

        return newItem;
    }

}
