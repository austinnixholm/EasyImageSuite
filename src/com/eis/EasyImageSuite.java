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

    public void encryptToResources(String encryptionKey) {
        SuiteResponse response = exporter.export(imageFileSystem, encryptionKey);
        if (!response.isSuccessful()) {
            for (String err : response.getErrors()) {
                System.out.println(err);
            }
        }
    }

    public void importResources(String decryptionKey) {
        SuiteResponse response = importer.importResources(imageFileSystem, decryptionKey);
    }

}
