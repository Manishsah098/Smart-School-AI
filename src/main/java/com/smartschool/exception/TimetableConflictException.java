package com.smartschool.exception;

public class TimetableConflictException extends RuntimeException {
    private final String conflictDetail;

    public TimetableConflictException(String message, String conflictDetail) {
        super(message);
        this.conflictDetail = conflictDetail;
    }

    public String getConflictDetail() { return conflictDetail; }
}
