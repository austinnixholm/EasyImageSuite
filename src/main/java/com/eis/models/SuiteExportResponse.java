package com.eis.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SuiteExportResponse extends SuiteResponse {
    @Getter
    private final transient List<BasicKeyValuePair<String, String>> rawExportData = new ArrayList<>();
}
