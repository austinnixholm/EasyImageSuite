package com.eis;

import com.eis.io.Exporter;
import com.eis.io.Importer;
import com.eis.models.*;
import com.eis.models.error.SuiteError;
import com.eis.models.response.SuiteExportResponse;
import com.eis.models.response.SuiteImportResponse;
import com.eis.models.response.SuiteResponse;
import lombok.Getter;

import java.io.IOException;

import static com.eis.SuiteGlobals.*;

/**
 * @author Austin Nixholm
 * @version 0.0.1
 */
public class EasyImageSuite {
    @Getter
    private ImageFileSystem imageFileSystem;
    @Getter
    private final Exporter exporter = new Exporter();
    @Getter
    private final Importer importer = new Importer();

    public EasyImageSuite setImageFileSystem(ImageFileSystem imageFileSystem) {
        this.imageFileSystem = imageFileSystem;
        return this;
    }

    public SuiteExportResponse encryptToResources(String encryptionKey, String iv) {
        SuiteExportResponse response = null;
        try {
            response = exporter.export(imageFileSystem, encryptionKey, iv);
            tryPrintErrors(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public SuiteImportResponse importResources(String decryptionKey, String iv) {
        SuiteImportResponse response = importer.importResources(imageFileSystem, decryptionKey, iv);
        tryPrintErrors(response);
        return response;
    }

    private void tryPrintErrors(SuiteResponse response) {
        if (!response.sucessful)
            for (SuiteError err : response.getErrors())
                logErr(err.toString());
    }

}
