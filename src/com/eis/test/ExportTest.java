package com.eis.test;

import com.eis.EasyImageSuite;
import com.eis.models.ImageFileSystem;


public class ExportTest {

    public static void main(String[] args) {

        EasyImageSuite easyImageSuite = new EasyImageSuite()
                .setImageFileSystem(new ImageFileSystem("C:\\users\\pelic\\Documents\\someImages"));

        easyImageSuite.getExporter().getExportAttributes().setMaximumFileSize(1);

        String key = "abc123";
        easyImageSuite.encryptToResources(key);
    }

}
