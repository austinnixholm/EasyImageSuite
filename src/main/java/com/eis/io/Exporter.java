package com.eis.io;

import com.eis.SuiteGlobals;
import com.eis.models.*;
import com.eis.security.EncryptionFunctions;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.eis.SuiteGlobals.*;
import static com.eis.models.SuiteErrorType.*;

public class Exporter {

    @Getter
    private ExportAttributes exportAttributes = new ExportAttributes();

    public Exporter setExportAttributes(ExportAttributes attributes) {
        this.exportAttributes = attributes;
        return this;
    }

    public SuiteExportResponse export(ImageFileSystem fileSystem, String encryptionKey, String iv) throws IOException {
        SuiteExportResponse response = new SuiteExportResponse();

        ArrayList<String> ignoredFolders = (ArrayList<String>) fileSystem.getIgnoredFolders();
        ArrayList<String> ignoredFiles = (ArrayList<String>) fileSystem.getIgnoredFileNames();
        ArrayList<String> parsableExtensions = (ArrayList<String>) fileSystem.getParsableFileExtensions();
        File directory = new File(fileSystem.getRootPath());
        ExportAttributes attributes = getExportAttributes();
        String exportFileExt = fileSystem.getExportFileExtension();

        StringBuilder resourceBuffer = new StringBuilder();
        int resourceFileIndex = 1;

        log("Starting resource export\n");

        // End with an unsuccessful response if the dir doesn't exist
        if (!directory.exists()) {
            response.sucessful = false;
            response.addError(INVALID_RESOURCE_DIRECTORY, "Root directory for path: '" + fileSystem.getRootPath() + "' does not exist!");
            return response;
        }

        ArrayList<File> directoryListing = (ArrayList<File>) SuiteGlobals.listf(fileSystem.getRootPath());
        // End with an unsuccessful response if there are no files in this directory.
        if (directoryListing.size() == 0) {
            response.sucessful = false;
            response.addError(NO_FILES_IN_RESOURCE_DIRECTORY, "Root directory for path: '" + fileSystem.getRootPath() + "' contains no files!");
            return response;
        }

        String resourceFolderPath = getOrCreateExportPath(fileSystem.getRootPath() + File.separator + attributes.getExportFolderName());
        Files.createDirectories(Paths.get(resourceFolderPath));
        String currentResourceFilePath = createResourceFile(resourceFolderPath, exportFileExt, resourceFileIndex);

        // Go through all accepted files of our file system, and encrypt them.
        for (File file : directoryListing) {
            String absolutePath = file.getAbsolutePath();
            String imagePath = absolutePath.replace(fileSystem.getRootPath(), "");
            String extension = getFileExtension(file);

            boolean isIgnoredFile = ignoredFiles.stream().anyMatch(f -> f.toLowerCase().equals(file.getName().toLowerCase()));
            boolean isInIgnoredFolder = ignoredFolders.stream().anyMatch(f -> file.getAbsolutePath().contains(f));
            boolean invalidExtension = !parsableExtensions.contains(extension);

            if (isIgnoredFile || isInIgnoredFolder || invalidExtension) continue;

            // Create a temporary copy of our current resource string buffer
            String currentResourceBuffer = resourceBuffer.toString();
            StringBuilder tempBuffer = new StringBuilder();

            // Wrap image path in brackets to identify later
            String newImgPath = "[" + imagePath + "]";
            byte[] bytes = fileToBytes(absolutePath);
            byte[] imgPathBytes = newImgPath.getBytes();
            byte[] destBytes = new byte[bytes.length + imgPathBytes.length];

            System.arraycopy(imgPathBytes, 0, destBytes, 0, imgPathBytes.length);
            System.arraycopy(bytes, 0, destBytes, imgPathBytes.length, bytes.length);

            tempBuffer.append(new String(destBytes));

            byte[] oldBufferBytes = currentResourceBuffer.getBytes();
            byte[] tempBufferBytes = tempBuffer.toString().getBytes();
            long oldSizeMegabytes = bytesToMegaBytes(oldBufferBytes.length);
            long estimatedFileSizeMegabytes = oldSizeMegabytes + bytesToMegaBytes(tempBufferBytes.length);

            // If the estimated size in bytes of all data combined is larger than the desired maximum...
            if ((int) estimatedFileSizeMegabytes >= attributes.getMaximumFileSize()) {
                // Encrypt all the previous data, and store that into a string
                String encryptedText = EncryptionFunctions.encrypt(currentResourceBuffer, encryptionKey, iv);
                // Write all the previous data to the current resource file
                writeToResourceFile(currentResourceFilePath, encryptedText);
                // Write the current raw, non encrypted data with the resource file path as the key to the response
                response.getRawExportData().add(new BasicKeyValuePair<>(currentResourceFilePath, currentResourceBuffer));
                // Reset the current resource buffer text with the newly generated data to start it off
                resourceBuffer = new StringBuilder().append(tempBuffer);
                // Set the new current resource file path to a newly generated resource file with incremented index.
                currentResourceFilePath = createResourceFile(resourceFolderPath, exportFileExt, ++resourceFileIndex);
            } else
                // Otherwise, simply add the new data to the resource buffer.
                resourceBuffer.append(tempBuffer);

            log("Processed " + imagePath);
        }
        // Take everything that's left in the resource buffer and encrypt it.
        if (!resourceBuffer.toString().isEmpty()) {
            String encryptedText = EncryptionFunctions.encrypt(resourceBuffer.toString(), encryptionKey, iv);
            writeToResourceFile(currentResourceFilePath, encryptedText);
            // Write the current raw, non encrypted data with the resource file path as the key to the response
            response.getRawExportData().add(new BasicKeyValuePair<>(currentResourceFilePath, resourceBuffer.toString()));
        }

        int filesExported = response.getRawExportData().size();
        log("Successfully exported " + filesExported + " file" + (filesExported > 1 ? "s" : "") + "\n");
        response.sucessful = true;
        return response;
    }

    private String createResourceFile(String directory, String fileExt, int index) {
        File resourceFile = new File(directory + File.separator + "resource-" + index + "." + fileExt);
        try {
            if (resourceFile.createNewFile())
                log("Created resource file: " + resourceFile.getAbsolutePath());
        } catch (IOException exception) {
            log("Could not create resource file! Exception: ");
            exception.printStackTrace();
        }
        return resourceFile.getAbsolutePath();
    }

    /**
     * Returns the current valid export directory file path.
     * <p>
     * If the desired path already exists, then a folder with an incremented
     * number will be created instead, which this method returns.
     *
     * @param desiredExportFolderPath the desired name of this export folder.
     * @return the path of the valid export folder.
     */
    private String getOrCreateExportPath(String desiredExportFolderPath) {
        String exportFolderPath = desiredExportFolderPath;
        int index = 1;
        boolean goodPath = false;

        do {
            if (new File(exportFolderPath).exists())
                exportFolderPath = desiredExportFolderPath + index++;
            else
                goodPath = true;
        } while (!goodPath);
        return exportFolderPath;
    }

    /**
     * Writes a string to a desired file.
     *
     * @param filePath the path to the file
     * @param buffer   the string to write to the file
     */
    private void writeToResourceFile(String filePath, String buffer) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(buffer);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
