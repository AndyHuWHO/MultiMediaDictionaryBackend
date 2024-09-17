package com.andywho.multimedia_dictionary.dictionary.dto;

public class ErrorResponse extends ResponseDto{
    private String error;       // A brief error code or description
    private String message;     // A human-readable error message
    private String details;     // Additional details or context

    public ErrorResponse(String error, String message, String details) {
        this.error = error;
        this.message = message;
        this.details = details;
    }

    // Getters and Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

