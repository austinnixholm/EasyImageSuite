package com.eis.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SuiteImportResponse extends SuiteResponse {
    @Getter
    private ResourceCache resourceCache = new ResourceCache();
    @Getter
    private final transient List<BasicKeyValuePair<String, String>> rawImportData = new ArrayList<>();
}
