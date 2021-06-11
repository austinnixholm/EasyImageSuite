package com.eis;

import com.eis.io.ResourceExporter;
import com.eis.io.ResourceImporter;
import com.eis.models.*;
import com.eis.models.error.SuiteError;
import com.eis.models.response.SuiteExportResponse;
import com.eis.models.response.SuiteCredentialGenerationResponse;
import com.eis.models.response.SuiteImportResponse;
import com.eis.models.response.SuiteResponse;
import com.eis.security.EncryptionFunctions;
import lombok.Getter;

import java.io.IOException;

import static com.eis.SuiteGlobals.*;

/**
 * @author Austin Nixholm
 * @version 0.1.1
 */
public class EasyImageSuite {
    @Getter
    private ImageFileSystem imageFileSystem;
    private final ResourceExporter exporter = new ResourceExporter();
    private final ResourceImporter importer = new ResourceImporter();

    private static boolean runningFromJar = false;

    /**
     * Sets the ImageFileSystem object, containing information on the filesystem
     * where files will be written to/read from during either exporting or importing.
     *
     * @param imageFileSystem the {@link ImageFileSystem} object
     * @return this instance.
     */
    public EasyImageSuite setImageFileSystem(ImageFileSystem imageFileSystem) {
        this.imageFileSystem = imageFileSystem;
        return this;
    }

    /**
     * Attempts to encrypt all resources available within this class' current {@link ImageFileSystem}
     * and returns a {@link SuiteExportResponse} containing the result from the operation.
     *
     * @param encryptionKey the encryption key
     * @param iv            the encryption IV
     * @return the export response.
     */
    public SuiteExportResponse encryptToResources(String encryptionKey, String iv) {
        SuiteExportResponse response = null;
        try {
            response = exporter.export(imageFileSystem, encryptionKey, iv);
            tryPrintErrors(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Attempts to decrypt and import all resource files from the specified
     * resource file within this class' current {@link ImageFileSystem} object,
     * and returns a {@link SuiteImportResponse} containing the result from the operation.
     *
     * @param decryptionKey the decryption key
     * @param iv            the decryption IV
     * @return the import response.
     */
    public SuiteImportResponse importResources(String decryptionKey, String iv) {
        SuiteImportResponse response = importer.importResources(imageFileSystem, decryptionKey, iv);
        tryPrintErrors(response);
        return response;
    }

    /**
     * Returns a {@link SuiteCredentialGenerationResponse} containing randomly generated credentials
     * for the {@link AlgorithmType} passed to the method.
     *
     * @param type the type of algorithm
     * @return the response containing credentials.
     */
    public SuiteCredentialGenerationResponse generateCredentials(AlgorithmType type) {
        return new SuiteCredentialGenerationResponse(EncryptionFunctions.generateKey(type), EncryptionFunctions.generateIV(type));
    }

    /**
     * Is this library being used in an application currently running as a JAR file?
     *
     * @param fromJar true if running from a JAR file.
     */
    public void setRunningFromJar(boolean fromJar) {
        EasyImageSuite.runningFromJar = fromJar;
    }

    private void tryPrintErrors(SuiteResponse response) {
        if (!response.sucessful)
            for (SuiteError err : response.getErrors())
                logErr(err.toString());
    }

    public ResourceExporter getExporter() {
        return exporter;
    }

    public ResourceImporter getImporter() {
        return importer;
    }

    public static boolean isRunningFromJar() {
        return runningFromJar;
    }
}
