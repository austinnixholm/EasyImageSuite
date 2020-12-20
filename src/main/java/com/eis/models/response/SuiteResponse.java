package com.eis.models.response;

import com.eis.models.error.SuiteError;
import com.eis.models.error.SuiteErrorType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class SuiteResponse {

    /**
     * Boolean indicating whether this response was successful or not.
     */
    public boolean sucessful;

    /**
     * A list of all {@code SuiteError} objects attached to this response.
     */
    @Getter
    private final List<SuiteError> errors = new ArrayList<>();

    /**
     * @param type    the type of error
     * @param message the error message
     * @param <T>     the generic type that extends this SuiteResponse parent class
     * @return Returns this SuiteResponse object after setting it unsuccessful, with an error.
     */
    @SuppressWarnings("unchecked")
    public <T extends SuiteResponse> T setError(SuiteErrorType type, String message) {
        this.sucessful = false;
        this.addError(type, message);
        return (T) this;
    }

    /**
     * Adds an error to this response object.
     *
     * @param type         the error type
     * @param errorMessage the error message
     */
    public void addError(SuiteErrorType type, String errorMessage) {
        getErrors().add(new SuiteError(type, errorMessage));
    }
}
