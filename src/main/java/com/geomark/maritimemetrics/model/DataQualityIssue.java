package com.geomark.maritimemetrics.model;

/**
 *  This enum represents different types of data quality issues that can occur
 */
public enum DataQualityIssue {
    MISSING_DATA("Missing data"),
    INVALID_DATA("Invalid data"),
    OUT_OF_BOUNDS("Out of bounds");

    private final String message;

    DataQualityIssue(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
