package com.geomark.maritimemetrics.exceptions;


public class DataProcessingException extends Exception {

    public DataProcessingException(String message) {
        super(message);
    }

    public DataProcessingException(String errorProcessingMetrics, Throwable e) {
        super(errorProcessingMetrics, e);
    }
}
