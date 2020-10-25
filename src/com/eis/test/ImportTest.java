package com.eis.test;

import com.eis.EasyImageSuite;
import com.eis.models.ImageFileSystem;

public class ImportTest {
    public static void main(String[] args) {
        EasyImageSuite easyImageSuite = new EasyImageSuite()
                .setImageFileSystem(new ImageFileSystem("C:\\users\\pelic\\Documents\\someImages"));
        easyImageSuite.getImageFileSystem().setResourceFolder("suite-exports");
    }
}
