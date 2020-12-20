package com.eis.models.response;

import lombok.Getter;

public class SuiteCredentialGenerationResponse extends SuiteResponse {
    @Getter
    private String generatedKey;
    @Getter
    private String generatedIV;

    public SuiteCredentialGenerationResponse(String generatedKey, String generatedIV) {
        this.generatedKey = generatedKey;
        this.generatedIV = generatedIV;
        this.sucessful = true;
    }
}
