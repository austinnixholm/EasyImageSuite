package com.eis.test;

import com.eis.EasyImageSuite;
import com.eis.models.ImageFileSystem;


public class ExportTest {

    public static void main(String[] args) {

        EasyImageSuite easyImageSuite = new EasyImageSuite()
                .setImageFileSystem(new ImageFileSystem("C:\\users\\pelic\\Documents\\someImages"));

        easyImageSuite.getImageFileSystem().getIgnoredFolders().add("image-sub2");
        easyImageSuite.getImageFileSystem().getIgnoredFolders().add("ignored-1");

        String key = "APPLESAUSS";
        String iv = "APPLESAUCEISGOOD";
        easyImageSuite.encryptToResources(key, iv);
    }

}
