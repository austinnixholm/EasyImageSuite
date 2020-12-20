package com.eis.models.error;

public class SuiteError {
    public SuiteErrorType type;
    public String message;
    public SuiteError(SuiteErrorType type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        return type.toString() + ": " + message;
    }
}
