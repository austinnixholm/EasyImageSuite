package com.eis.io;

import com.eis.models.*;
import com.eis.models.response.SuiteImportResponse;
import com.eis.security.EncryptionFunctions;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Objects;

import static com.eis.SuiteGlobals.*;
import static com.eis.models.error.SuiteErrorType.*;

public class Importer {

    @Getter
    @Setter
    private String resourceFileExtension = DEFAULT_FILE_EXTENSION;

    @SneakyThrows
    public SuiteImportResponse importResources(ImageFileSystem fileSystem, String decryptionKey, String iv) {
        SuiteImportResponse response = new SuiteImportResponse();
        String resourceFolderName = fileSystem.getResourceFolder();
        log("Starting resource import\n");

        // Ensure that the the resource folder name is valid.
        if (resourceFolderName.isEmpty())
            return response.setError(NO_RESOURCE_FOLDER_SPECIFIED, "Please enter a resource folder to import from.");

        String resourceFolderPath = fileSystem.getRootPath() + File.separator + resourceFolderName;
        File resourceFolderFile = new File(resourceFolderPath);

        if (!resourceFolderFile.isDirectory())
            return response.setError(INVALID_RESOURCE_DIRECTORY, "The entered resource folder name was not a valid directory.\n" + resourceFolderFile.getAbsolutePath());

        // Go through each resource file.
        for (File file : Objects.requireNonNull(resourceFolderFile.listFiles())) {
            String extension = getFileExtension(file);
            String absolutePath = file.getAbsolutePath();
            boolean invalidResourceFile = !extension.toLowerCase().equals(this.resourceFileExtension.toLowerCase());

            if (invalidResourceFile) continue;
            byte[] bytes = fileToBytes(absolutePath);
            String decryptedData = EncryptionFunctions.decrypt(new String(bytes), decryptionKey, iv);

            response.getRawImportData().add(new BasicKeyValuePair<>(absolutePath, decryptedData));
            log("Decrypting '" + file.getName() + "'...");

        }
        response.sucessful = true;
        return response;
    }



}
