package com.eis.test;

import com.eis.EasyImageSuite;
import com.eis.models.ImageFileSystem;

public class ImportTest {
    public static void main(String[] args) {
        EasyImageSuite easyImageSuite = new EasyImageSuite()
                .setImageFileSystem(new ImageFileSystem("C:\\users\\pelic\\Documents\\someImages"));
        easyImageSuite.getImageFileSystem().setResourceFolder("suite-exports2");
        String key = "APPLESAUSS";
        String iv = "APPLESAUCEISGOOD";
        easyImageSuite.importResources(key, iv);
    }
}
