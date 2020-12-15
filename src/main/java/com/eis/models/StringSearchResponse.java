package com.eis.models;

import lombok.Getter;

public class StringSearchResponse {
    @Getter
    public boolean success;
    @Getter
    public String stringResponse;
    /**
     * @param response the stringResponse to return with this object.
     * @return this response in a successful state.
     */
    public StringSearchResponse successful(String response) {
        this.success = true;
        this.stringResponse = response;
        return this;
    }
    /**
     * @return this response in a failed state.
     */
    public StringSearchResponse failed() {
        this.success = false;
        return this;
    }
}
