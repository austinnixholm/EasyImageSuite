package com.eis.io;

import com.eis.SuiteGlobals;
import com.eis.models.ExportAttributes;
import com.eis.models.ImageFileSystem;
import com.eis.models.SuiteResponse;
import com.eis.security.EncryptionSuite;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.eis.SuiteGlobals.*;

public class Exporter {

    @Getter
    private ExportAttributes exportAttributes = new ExportAttributes();

    public Exporter setExportAttributes(ExportAttributes attributes) {
        this.exportAttributes = attributes;
        return this;
    }

    @SneakyThrows
    public SuiteResponse export(ImageFileSystem fileSystem, String encryptionKey) {
        SuiteResponse response = new SuiteResponse();

        ArrayList<String> ignoredFolders = (ArrayList<String>) fileSystem.getIgnoredFolders();
        ArrayList<String> ignoredFiles = (ArrayList<String>) fileSystem.getIgnoredFileNames();
        ArrayList<String> parsableExtensions = (ArrayList<String>) fileSystem.getParsableFileExtensions();
        File directory = new File(fileSystem.getRootPath());
        ExportAttributes attributes = getExportAttributes();
        String exportFileExt = fileSystem.getExportFileExtension();

        StringBuilder resourceBuffer = new StringBuilder();
        int resourceFileIndex = 1;

        // End with an unsuccessful response if the dir doesn't exist
        if (!directory.exists()) {
            response.setSuccessful(false);
            response.addError("Root directory for path: '" + fileSystem.getRootPath() + "' does not exist!");
            return response;
        }

        ArrayList<File> directoryListing = (ArrayList<File>) SuiteGlobals.listf(fileSystem.getRootPath());
        // End with an unsuccessful response if there are no files in this directory.
        if (directoryListing.size() == 0) {
            response.setSuccessful(false);
            response.addError("Root directory for path: '" + fileSystem.getRootPath() + "' contains no files!");
            return response;
        }

        String resourceFolderPath = pathOrAvailable(fileSystem.getRootPath() + File.separator + attributes.getExportFolderName());
        Files.createDirectories(Paths.get(resourceFolderPath));
        String currentResourceFilePath = createResourceFile(resourceFolderPath, exportFileExt, resourceFileIndex);

        // Go through all accepted files of our file system, and encrypt them.
        for (File file : directoryListing) {
            String absolutePath = file.getAbsolutePath();
            String filePath = file.getPath();
            String imagePath = absolutePath.replace(fileSystem.getRootPath(), "");
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1);

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
            String encryptedText = new String(EncryptionSuite.encrypt(bytes, encryptionKey));
            String encryptedImagePath = new String(EncryptionSuite.encrypt(newImgPath.getBytes(), encryptionKey));
            tempBuffer.append(encryptedImagePath);
            tempBuffer.append(encryptedText).append("]");
            tempBuffer.append('\n');

            byte[] oldBufferBytes = currentResourceBuffer.getBytes();
            byte[] tempBufferBytes = tempBuffer.toString().getBytes();
            long oldSizeMegabytes = bytesToMegaBytes(oldBufferBytes.length);
            long estimatedFileSizeMegabytes = oldSizeMegabytes + bytesToMegaBytes(tempBufferBytes.length);

            // If the estimated size in bytes of all data combined is larger than the desired maximum...
            if ((int) estimatedFileSizeMegabytes >= attributes.getMaximumFileSize()) {
                // Write all the previous data to the current resource file
                writeToResourceFile(currentResourceFilePath, currentResourceBuffer);
                // Reset the current resource buffer text with the newly generated data to start it off
                resourceBuffer = new StringBuilder().append(tempBuffer);
                // Set the new current resource file path to a newly generated resource file with incremented index.
                currentResourceFilePath = createResourceFile(resourceFolderPath, exportFileExt, ++resourceFileIndex);
            } else
                // Otherwise, simply add the new data to the resource buffer.
                resourceBuffer.append(tempBuffer);

            log("Processed " + imagePath);
        }
        if (!resourceBuffer.toString().isEmpty())
            writeToResourceFile(currentResourceFilePath, resourceBuffer.toString());

        response.setSuccessful(true);
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

    private String pathOrAvailable(String folderPath) {
        String original = folderPath;
        int index = 1;
        boolean goodPath = false;
        while (!goodPath) {
            if (new File(folderPath).exists()) {
                index++;
                folderPath = original + index;
            } else
                goodPath = true;
        }
        return folderPath;
    }

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

    private void log(String message) {
        System.out.println(ANSI_YELLOW + "[EasyImageSuite Exporter] " + message);
    }
}
