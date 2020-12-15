package com.eis.models;

import java.util.ArrayList;
import java.util.List;

public class ResourceCache {
    private final transient List<ResourceImage> cachedResources = new ArrayList<>();
    public void addResource(ResourceImage resourceImage) {
        this.cachedResources.add(resourceImage);
    }
}
