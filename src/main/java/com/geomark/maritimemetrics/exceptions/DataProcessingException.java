package com.geomark.maritimemetrics.exceptions;


/**
 *  This class represents a custom exception that is thrown when there is an error
 *  during the processing of vessel metrics data.
 */
public class DataProcessingException extends Exception {

    public DataProcessingException(String message) {
        super(message);
    }

    public DataProcessingException(String errorProcessingMetrics, Throwable e) {
        super(errorProcessingMetrics, e);
    }
}
