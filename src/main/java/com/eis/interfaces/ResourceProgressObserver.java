package com.eis.interfaces;

/**
 * @author Austin Nixholm
 */
public interface ResourceProgressObserver {
    void update(int fileCount, int maxFileCount, int currentDataIndex, int maxDataIndex, boolean eof);
}
