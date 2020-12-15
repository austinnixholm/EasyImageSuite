package com.eis.models;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

public class SuiteResponse {
    public boolean sucessful;
    @Getter
    private final List<SuiteError> errors = new ArrayList<>();

    public SuiteResponse setError(SuiteErrorType type, String message) {
        this.sucessful = false;
        this.addError(type, message);
        return this;
    }

    public void addError(SuiteErrorType type, String errorMessage) { getErrors().add(new SuiteError(type, errorMessage)); }
}
