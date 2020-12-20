package com.eis.io;

import com.eis.models.*;
import com.eis.models.response.SuiteImportResponse;
import com.eis.security.EncryptionFunctions;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
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
        try {
            ImageIO.setUseCache(false);
            for (File file : Objects.requireNonNull(resourceFolderFile.listFiles())) {
                String extension = getFileExtension(file);
                String absolutePath = file.getAbsolutePath();
                boolean invalidResourceFile = !extension.toLowerCase().equals(this.resourceFileExtension.toLowerCase());

                if (invalidResourceFile) continue;
                byte[] bytes = fileToBytes(absolutePath);
                String decryptedData = EncryptionFunctions.decrypt(new String(bytes), decryptionKey, iv);

                log("Decrypting '" + file.getName() + "'...");
                response.getRawImportData().add(new BasicKeyValuePair<>(file.getCanonicalPath(), decryptedData));

                // Copy the decrypted data to its own buffer string
                String buffer = String.valueOf(decryptedData);
                boolean endOfFile = false;
                do {
                    // Find the delimiter for the max path length sector
                    int pathLenDelimIdx = buffer.indexOf(':');
                    if (pathLenDelimIdx == -1) break;
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

                    byte[] imageBytes = toDecodedBase64String(buffer.substring(0, imageCharsLen));
                    InputStream is = new ByteArrayInputStream(imageBytes);
                    BufferedImage image = ImageIO.read(is);

                    response.getResourceCache().addResource(new ResourceImage(imagePath, image));

                    if (imageCharsLen >= decryptedData.length() - 1)
                        endOfFile = true;
                    else
                        buffer = buffer.substring(imageCharsLen);
                } while (!endOfFile);

            }
        } catch (Exception e) {
            return response.setError(JAVA_EXCEPTION, e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        response.sucessful = true;
        return response;
    }





}
