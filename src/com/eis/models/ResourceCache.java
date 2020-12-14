package com.eis.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ResourceCache {
    @Getter
    private transient List<ResourceImage> cachedResources = new ArrayList<>();
    public void addResource(ResourceImage resourceImage) {
        this.cachedResources.add(resourceImage);
    }
}
