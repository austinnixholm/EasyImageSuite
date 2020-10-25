package com.eis.io;

import com.eis.models.ImageFileSystem;
import com.eis.models.SuiteResponse;

public class Importer {

    public SuiteResponse importResources(ImageFileSystem fileSystem, String decryptionKey) {
        SuiteResponse response = new SuiteResponse();



        response.setSuccessful(true);
        return response;
    }

}
