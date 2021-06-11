package com.eis.models;

import com.eis.io.ResourceExporter;
import lombok.Getter;
import lombok.Setter;

import static com.eis.SuiteGlobals.*;

/**
 * Specific attributes associated with resource file exporting.
 *
 * @see ResourceExporter
 */
public class ExportAttributes {
    /**
     * The maximum file size for an export resource file, in megabytes.
     * <p>Default is 10 megabytes.</p>
     */
    @Getter
    @Setter
    private int maximumFileSize = DEFAULT_MAXIMUM_FILE_SIZE_MB;
    /**
     * The name of the folder where exported resource files will go.
     * <p>Default folder name is "suite-exports"</p>
     */
    @Getter
    private String exportFolderName = DEFAULT_EXPORT_FOLDER_NAME;

    public ExportAttributes() { }

    /**
     * Constructor passing in the amount of maximum MB file size that resources
     * can be when exported.
     *
     * @param maximumFileSize the maximum resource file size in megabytes
     */
    public ExportAttributes(int maximumFileSize) {
        this.maximumFileSize = maximumFileSize;
    }

    /**
     * Constructor passing in the amount of maximum MB file size that resources
     * can be when exported, and the desired name of the export folder.
     *
     * @param maximumFileSize  the maximum resource file size in megabytes
     * @param exportFolderName the desired export folder name
     */
    public ExportAttributes(int maximumFileSize, String exportFolderName) {
        this.maximumFileSize = maximumFileSize;
        this.exportFolderName = exportFolderName;
    }
}
