package com.eis.models;

import lombok.Getter;
import lombok.Setter;

import static com.eis.SuiteGlobals.*;

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

    public ExportAttributes(int maximumFileSize) {
        this.maximumFileSize = maximumFileSize;
    }

    public ExportAttributes(int maximumFileSize, String exportFolderName) {
        this.maximumFileSize = maximumFileSize;
        this.exportFolderName = exportFolderName;
    }
}
