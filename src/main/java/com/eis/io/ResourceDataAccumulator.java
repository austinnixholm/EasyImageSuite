package com.eis.io;

import com.eis.EasyImageSuite;
import com.eis.SuiteGlobals;
import com.eis.models.ImageFileSystem;
import com.eis.models.ResourceFileData;
import com.eis.models.streams.WinZipInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.eis.SuiteGlobals.*;

/**
 * Accumulates data from resource files. Helps with gathering information from
 * resources that are either in a local filesystem, or within the filesystem of the running jar this
 * library has been packaged within.
 *
 * @author Austin Nixholm
 */
public class ResourceDataAccumulator {

    private final ImageFileSystem fileSystemInfo;

    public ResourceDataAccumulator(ImageFileSystem fileSystemInfo) {
        this.fileSystemInfo = fileSystemInfo;
    }

    /**
     * Gets an ArrayList of byte[] objects containing data of all resource files within the
     * {@link ImageFileSystem} root folder path.
     * <p>
     * Appropriately parses resources based on if they are within a jar file, or
     * local filesystem.
     *
     * @param resourceExtension the resource file extension.
     * @return a list of byte[] data from each resource file, or null if an error occurred.
     */
    public ArrayList<ResourceFileData> getResourceFileData(String resourceExtension) {
        ArrayList<ResourceFileData> list = new ArrayList<>();
        try {
            // Indicate that we're either using jar file as the file system or not.
            if (EasyImageSuite.isRunningFromJar()) {
                log("Loading from archive...");
                // Get the code source from this class's location.
                CodeSource src = ResourceDataAccumulator.class.getProtectionDomain().getCodeSource();
                if (src == null) {
                    logErr("Issue occurred gathering resource file data. CodeSource null.");
                    return null;
                }
                // Grab a URL for the source location (this jar, a zip file)
                URL jar = src.getLocation();
                // Determine if this is an EXE file... For cases of a .JAR being wrapped in a .EXE
                boolean isExe = jar.getPath().toLowerCase().endsWith(".exe");
                // Open a ZipInputStream for this zipped file. Open a custom WinZipInputStream if the current application is a .EXE file.
                ZipInputStream zipInputStream = isExe ? new ZipInputStream(new WinZipInputStream(new FileInputStream(jar.getPath()))) : new ZipInputStream(jar.openStream());
                ZipEntry entry;
                // Grab each entry within this zip file
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    // Ensure that any file we're trying to get bytes from IS a resource file
                    if (!entry.getName().endsWith(resourceExtension))
                        continue;
                    // Get the bytes from this input stream, given the size (length in stream)
//                    log("Found zip entry: " + entry.getName());
                    byte[] bytes = SuiteGlobals.inputStreamToBytes(zipInputStream);
                    list.add(new ResourceFileData(bytes, new File(entry.getName()).getName(), entry.getName()));
                }
            } else {
                log("Loading from default filesystem...");
                // Get all filesystem information from the ImageFileSystem object
                String resourceFolderName = fileSystemInfo.getResourceFolder();
                String resourceFolderPath = fileSystemInfo.getRootPath() + File.separator + resourceFolderName;
                File resourceFolderFile = new File(resourceFolderPath);
                if (!resourceFolderFile.isDirectory())
                    return null;
                // Ensure that this is a directory and get all the files from it
                File[] files = resourceFolderFile.listFiles();
                if (files == null)
                    return null;
                // Go through each file, ensure it's a valid resource file, and convert it to a byte array.
                for (File file : files) {
                    String extension = getFileExtension(file);
                    String absolutePath = file.getAbsolutePath();
                    boolean invalidResourceFile = !extension.equalsIgnoreCase(resourceExtension);

                    if (invalidResourceFile) continue;
                    byte[] bytes = fileToBytes(absolutePath);
                    list.add(new ResourceFileData(bytes, file.getName(), file.getCanonicalPath()));
                }
            }
        } catch (Exception e) {
            logErr(e.toString());
        }
        return list;
    }
}
