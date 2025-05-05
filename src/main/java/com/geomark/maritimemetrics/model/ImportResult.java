package com.geomark.maritimemetrics.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public record ImportResult(AtomicLong totalTimeElapsed,
                           AtomicLong validRecords,
                           AtomicLong ingestionErrors,
                           List<String> errorMessages) {

    @Override
    public AtomicLong totalTimeElapsed(){
        return new AtomicLong(System.currentTimeMillis() - totalTimeElapsed.get());
    }

}
