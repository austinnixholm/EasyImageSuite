package com.eis.io;

import com.eis.models.ImageFileSystem;
import com.eis.models.SuiteImportResponse;
import com.eis.models.SuiteResponse;
import com.eis.security.EncryptionFunctions;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Objects;

import static com.eis.SuiteGlobals.*;

public class Importer {

    @Getter
    @Setter
    private String resourceFileExtension = DEFAULT_FILE_EXTENSION;

    @SneakyThrows
    public SuiteResponse importResources(ImageFileSystem fileSystem, String decryptionKey, String iv) {
        SuiteImportResponse response = new SuiteImportResponse();
        String resourceFolderName = fileSystem.getResourceFolder();

        // Ensure that the the resource folder name is valid.
        if (resourceFolderName.isEmpty())
            return response.setError("Please enter a resource folder to import from.");

        String resourceFolderPath = fileSystem.getRootPath() + File.separator + resourceFolderName;
        File resourceFolderFile = new File(resourceFolderPath);

        if (!resourceFolderFile.isDirectory())
            return response.setError("The entered resource folder name was not a valid directory.");

        // Go through each resource file.
        for (File file : Objects.requireNonNull(resourceFolderFile.listFiles())) {
            String extension = getFileExtension(file);
            String absolutePath = file.getAbsolutePath();
            boolean invalidResourceFile = !extension.toLowerCase().equals(this.resourceFileExtension.toLowerCase());

            if (invalidResourceFile) continue;
            byte[] bytes = fileToBytes(absolutePath);
            String decryptedData = EncryptionFunctions.decrypt(new String(bytes), decryptionKey, iv);
                    //DecryptionSuite.decrypt(bytes, decryptionKey);
            System.out.println(decryptedData);

        }

        response.sucessful = true;
        return response;
    }



}
