package com.eis.models.response;

import com.eis.models.BasicKeyValuePair;
import com.eis.models.ResourceCache;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SuiteImportResponse extends SuiteResponse {
    @Getter
    private ResourceCache resourceCache = new ResourceCache();
    @Getter
    private final transient List<BasicKeyValuePair<String, String>> rawImportData = new ArrayList<>();
}
