package com.eis.models;

import lombok.Getter;

/**
 * @author Austin Nixholm
 */
public class ResourceFileData {

    @Getter
    private final byte[] data;
    @Getter
    private final String fileName;
    @Getter
    private final String filePath;

    public ResourceFileData(byte[] data, String fileName, String filePath) {
        this.data = data;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
