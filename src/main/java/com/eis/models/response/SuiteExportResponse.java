package com.eis.models.response;

import com.eis.models.BasicKeyValuePair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SuiteExportResponse extends SuiteResponse {
    @Getter
    private final transient List<BasicKeyValuePair<String, String>> rawExportData = new ArrayList<>();
}
