package com.eis.models;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

public class SuiteResponse {
    @Getter
    @Setter
    public boolean successful;
    @Getter
    private ArrayList<String> errors = new ArrayList<>();
    public void addError(String error) { getErrors().add(error); }
}
