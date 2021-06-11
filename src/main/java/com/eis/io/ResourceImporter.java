package com.eis.io;

import com.eis.interfaces.ResourceProgressObserver;
import com.eis.models.*;
import com.eis.models.response.SuiteImportResponse;
import com.eis.security.EncryptionFunctions;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.eis.SuiteGlobals.*;
import static com.eis.models.error.SuiteErrorType.*;

public class ResourceImporter {

    private final List<ResourceProgressObserver> observers = new ArrayList<>();

    @Getter
    @Setter
    private String resourceFileExtension = DEFAULT_FILE_EXTENSION;

    public SuiteImportResponse importResources(ImageFileSystem fileSystem, String decryptionKey, String iv) {
        SuiteImportResponse response = new SuiteImportResponse();
        log("Starting resource import\n");

        // Go through each resource file.
        try {
            ImageIO.setUseCache(false);

            int fileIndex = 1;
            int currentMaxIndices;
            int currentIndex = -1;
            // Create our data accumulator for our resource files.
            ResourceDataAccumulator accumulator = new ResourceDataAccumulator(fileSystem);

            // Get all file data from given filesystem information.
            ArrayList<ResourceFileData> resourceFileData = accumulator.getResourceFileData(this.resourceFileExtension);
            log("Attempting to gather resource data...");
            // Get the max number of files
            final int maxFiles = resourceFileData.size();
            // Go through each bit of data...
            for (ResourceFileData data : resourceFileData) {
                // Constant information from the data
                final byte[] bytes = data.getData();
                final String fileName = data.getFileName();
                final String filePath = data.getFilePath();

                // Decrypt this given the decryption key and IV
                String decryptedData = EncryptionFunctions.decrypt(new String(bytes), decryptionKey, iv);

                log("Reading '" + fileName + "'...");
                // Add the raw-decrypted text to the raw import data, with the original resource file path
                response.getRawImportData().add(new BasicKeyValuePair<>(filePath, decryptedData));

                // Copy the decrypted data to its own buffer string
                String buffer = String.valueOf(decryptedData);
                currentMaxIndices = buffer.length();
                boolean endOfFile = false;
                do {
                    // Find the delimiter for the max path length sector
                    final int totalStartIndex = decryptedData.indexOf(buffer);
                    int pathLenDelimIdx = buffer.indexOf(':');
                    if (pathLenDelimIdx == -1) {
                        notifyChange(fileIndex, maxFiles, currentIndex, currentMaxIndices, true);
                        break;
                    }
                    currentIndex = totalStartIndex + pathLenDelimIdx;
                    // Seek to the first header delimiter
                    String pathLengthSector = buffer.substring(0, pathLenDelimIdx).replace(":", "").replace(" ", "");

                    // Parse that sector
                    final int maxPathLen = Integer.parseInt(pathLengthSector);
                    buffer = buffer.substring(pathLenDelimIdx);
                    // maxPathLen + 1 to include delimiter
                    String pathSector = buffer.substring(0, maxPathLen + 1).replace(":", "");
                    int fileExtIdx = pathSector.lastIndexOf('.');
                    String sectorEnding = pathSector.substring(fileExtIdx);
                    // Replace the original ending of this sector with the non-spaced version to create the image path
                    final String imagePath = pathSector.replace(sectorEnding, sectorEnding.replace(" ", ""));

                    // Replace the buffer with the next section and on
                    buffer = buffer.substring(maxPathLen + 2);

                    final int imgCharsLenSeek = 20;

                    // SEEK 20 + 1 (for delimiter) and remove the delimiter
                    String imageCharsLengthSector = buffer.substring(0, imgCharsLenSeek + 1).replace(" ", "").replace(":", "");
                    final int imageCharsLen = Integer.parseInt(imageCharsLengthSector);

                    // Replace the buffer with the next section past delimiter
                    buffer = buffer.substring(imgCharsLenSeek + 1);

                    byte[] imageBytes = EncryptionFunctions.toDecodedBase64String(buffer.substring(0, imageCharsLen));
                    InputStream is = new ByteArrayInputStream(imageBytes);
                    BufferedImage image = ImageIO.read(is);

                    response.getResourceCache().addResource(new ResourceImage(imagePath, image));

                    if (imageCharsLen >= decryptedData.length() - 1)
                        endOfFile = true;
                    else
                        buffer = buffer.substring(imageCharsLen);
                    notifyChange(fileIndex, maxFiles, currentIndex, currentMaxIndices, endOfFile);
                } while (!endOfFile);

                fileIndex++;
            }
        } catch (Exception e) {
            return response.setError(JAVA_EXCEPTION, e + "\n" + Arrays.toString(e.getStackTrace()));
        }
        response.sucessful = true;
        return response;
    }

    private void notifyChange(int fileCount, int maxFileCount, int currentIndex, int maxIndex, boolean eof) {
        observers.forEach(o -> o.update(fileCount, maxFileCount, currentIndex, maxIndex, eof));
    }

    public void addObserver(ResourceProgressObserver observer) {
        observers.add(observer);
    }

}
