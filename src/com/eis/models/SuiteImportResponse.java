package com.eis.models;

import lombok.Getter;

public class SuiteImportResponse extends SuiteResponse {
    @Getter
    private ResourceCache resourceCache = new ResourceCache();
}
