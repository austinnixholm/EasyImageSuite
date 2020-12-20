package com.eis.io;

import com.eis.SuiteGlobals;
import com.eis.models.*;
import com.eis.models.response.SuiteExportResponse;
import com.eis.security.EncryptionFunctions;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static com.eis.SuiteGlobals.*;
import static com.eis.models.error.SuiteErrorType.*;

public class Exporter {

    @Getter
    private ExportAttributes exportAttributes = new ExportAttributes();

    public Exporter setExportAttributes(ExportAttributes attributes) {
        this.exportAttributes = attributes;
        return this;
    }

    /**
     * Attempts to encrypt and export all resources available within an {@link ImageFileSystem},
     * and returns a {@link SuiteExportResponse} determining the success, and containing raw export data
     * related to this export operation.
     *
     * @param fileSystem    the {@link ImageFileSystem}
     * @param encryptionKey the encryption key
     * @param iv            the encryption IV
     * @return the {@link SuiteExportResponse} for this operation.
     * @throws IOException
     */
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

        @SuppressWarnings("OptionalGetWithoutIsPresent") final int highestFilePathLength = directoryListing.stream().map(f -> f.getAbsolutePath().replace(fileSystem.getRootPath(), "").length()).max(Integer::compare).get();

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

            StringBuilder headerBuffer = new StringBuilder();
            // sectors separated by ':' delimiter
            // Seek 4, remove spaces, returns next seek length for the path sector
            char[] maxPathLengthSector = new char[4];
            char[] szChars = String.valueOf(highestFilePathLength).toCharArray();
            System.arraycopy(szChars, 0, maxPathLengthSector, 0, szChars.length);
            int szCharLength = szChars.length;
            int fillCount = maxPathLengthSector.length - szCharLength;
            // If we need to fill any more characters to this sector, fill it with spaces
            if (fillCount > 0)
                IntStream.range(0, fillCount).forEach(i -> maxPathLengthSector[szCharLength + i] = ' ');

            // Append this sector to the header buffer with separator
            headerBuffer.append(maxPathLengthSector);
            headerBuffer.append(':');

            // Create the sector of characters, giving the size of the highest possible path length
            // Get the chars from the image path, record its length
            char[] pathSector = new char[highestFilePathLength];
            char[] imagePathChars = imagePath.toCharArray();
            int imgPathCharsLen = imagePathChars.length;
            // Copy the characters from the image path to the start of the pathSector char array
            System.arraycopy(imagePathChars, 0, pathSector, 0, imgPathCharsLen);

            // Determine if we have to fill any characters to this sector
            fillCount = pathSector.length - imgPathCharsLen;
            // If so, fill it with spaces
            if (fillCount > 0)
                IntStream.range(0, fillCount).forEach(i -> pathSector[imgPathCharsLen + i] = ' ');

            // Append the header with the separator
            headerBuffer.append(pathSector);
            headerBuffer.append(':');

            // Retrieve bytes from this image
            FileInputStream imageInFile = new FileInputStream(new File(absolutePath));
            byte[] imageBytes = new byte[(int) file.length()];
            imageInFile.read(imageBytes);
            // Convert the bytes from this image into an encoded base64 string
            String imageStr = toEncodedBase64String(imageBytes);

            // Get the image bytes, record its length
            int imageBytesLen = imageStr.length();
            char[] imageCharLengthSector = new char[20];
            char[] imageCharLenChars = String.valueOf(imageBytesLen).toCharArray();
            // This naming... copy the characters of the length of the image bytes char array to this sector
            int imageCharLenCharsLen = imageCharLenChars.length;
            System.arraycopy(imageCharLenChars, 0, imageCharLengthSector, 0, imageCharLenCharsLen);

            fillCount = imageCharLengthSector.length - imageCharLenCharsLen;
            if (fillCount > 0)
                IntStream.range(0, fillCount).forEach(i -> imageCharLengthSector[imageCharLenCharsLen + i] = ' ');

            // Append the sector determining the amount of chars to seek from the image bytes ahead.
            headerBuffer.append(imageCharLengthSector);
            headerBuffer.append(':');

            // Get the header as a string
            String headerString = headerBuffer.toString();
            byte[] headerBytes = headerString.getBytes();

            tempBuffer.append(new String(headerBytes));
            tempBuffer.append(imageStr);

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

    /**
     * Attempts to create a resource file within a specific directory, constructing
     * the name of the file from the parameters passed.
     *
     * @param directory the path to the directory for this file
     * @param fileExt   the extension to be created with this file name
     * @param index     the index of this file within the directory
     * @return the path of the created (or potentially existing) file
     */
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
