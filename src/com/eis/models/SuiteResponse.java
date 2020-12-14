package com.eis.models;

import lombok.Getter;
import java.util.ArrayList;

public class SuiteResponse {
    @Getter
    public boolean sucessful;
    @Getter
    private ArrayList<String> errors = new ArrayList<>();

    public SuiteResponse setError(String message) {
        this.sucessful = false;
        this.addError(message);
        return this;
    }

    public void addError(String error) { getErrors().add(error); }
}
