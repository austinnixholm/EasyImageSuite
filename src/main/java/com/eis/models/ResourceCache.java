package com.eis.models;

import java.util.ArrayList;
import java.util.List;

/**
 * A resource cache object containing a list of {@link ResourceImage} objects.
 *
 * @see com.eis.models.response.SuiteImportResponse
 */
public class ResourceCache {
    private final transient List<ResourceImage> cachedResources = new ArrayList<>();

    /**
     * Adds a resource image to the cached resources list.
     *
     * @param resourceImage the resource image to add
     */
    public void addResource(ResourceImage resourceImage) {
        this.cachedResources.add(resourceImage);
    }

    /**
     * @return the {@link List} of {@link ResourceImage} objects.
     */
    public List<ResourceImage> getCachedResources() {
        return cachedResources;
    }
}
