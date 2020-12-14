package com.eis;

import com.eis.io.Exporter;
import com.eis.io.Importer;
import com.eis.models.SuiteResponse;
import com.eis.models.ImageFileSystem;
import lombok.Getter;

/**
 * @author Austin Nixholm
 * @version 0.0.1
 */
public class EasyImageSuite {
    @Getter
    private ImageFileSystem imageFileSystem;
    @Getter
    private Exporter exporter = new Exporter();
    @Getter
    private Importer importer = new Importer();

    public EasyImageSuite setImageFileSystem(ImageFileSystem imageFileSystem) {
        this.imageFileSystem = imageFileSystem;
        return this;
    }

    public void encryptToResources(String encryptionKey, String iv) {
        SuiteResponse response = exporter.export(imageFileSystem, encryptionKey, iv);
        tryPrintErrors(response);
    }

    public void importResources(String decryptionKey, String iv) {
        SuiteResponse response = importer.importResources(imageFileSystem, decryptionKey, iv);
        tryPrintErrors(response);
    }

    private void tryPrintErrors(SuiteResponse response) {
        if (!response.isSucessful())
            for (String err : response.getErrors())
                System.out.println(err);
    }

}
