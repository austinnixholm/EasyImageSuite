package com.eis.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.eis.SuiteGlobals.DEFAULT_FILE_EXTENSION;
import static com.eis.SuiteGlobals.STANDARD_FILE_EXTENSIONS;

/**
 * The ImageFileSystem class contains information regarding the directory of
 * image files that will be parsed & encrypted, and export information regarding
 * this filesystem.
 *
 * <p>Set the <code>rootPath</code> to the initial directory of your desired image files to encrypt/decrypt.</p>
 * <p>Either use a default export resource file extension, or set your own.</p>
 * <p>Either parse common, default file extensions or set your own.</p>
 *
 * @author Austin Nixholm
 */
public class ImageFileSystem {
    /**
     * The root path for file searching.
     */
    @Getter
    private final String rootPath;
    /**
     * The file extension that is added at the end of an exported file.
     * <p>Default file extension is '.JRC'</p>
     */
    @Getter
    @Setter
    private String exportFileExtension = DEFAULT_FILE_EXTENSION;

    /**
     * The list of accepted file extensions to search for when parsing images.
     * <p>List of default file extensions, if none are passed in the constructor:</p>
     * <p>"PNG", "png", "JPG", "jpg", "JPEG", "jpeg", "BMP", "bmp"</p>
     */
    @Getter
    private List<String> parsableFileExtensions = new ArrayList<>(STANDARD_FILE_EXTENSIONS);

    /**
     * A list of ignored folder names to skip when parsing image files
     * within the file system.
     */
    @Getter
    private final List<String> ignoredFolders = new ArrayList<>();

    /**
     * A list of ignored file names to skip over when parsing image files
     * within the file system. These file names may or may not include their extensions.
     */
    @Getter
    private final List<String> ignoredFileNames = new ArrayList<>();

    /**
     * The name of the folder containing any exported/importable resource files.
     */
    @Getter
    @Setter
    private String resourceFolder = "";

    /**
     * Single parameter constructor containing the root path of the file system.
     * See defaults for <code>parsableFileExtensions</code> and <code>exportFileExtension</code>.
     *
     * @param rootPath the root path for this file system.
     */
    public ImageFileSystem(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Two parameter constructor containing the root path of the file system
     * and a list of accepted image file extensions that may be parsed.
     *
     * @param rootPath           the root path of this file system.
     * @param parsableExtensions a list of Strings containing parsable file extensions.
     */
    public ImageFileSystem(String rootPath, String... parsableExtensions) {
        this.rootPath = rootPath;
        this.parsableFileExtensions = List.of(parsableExtensions);
    }

    /**
     * Two parameter constructor containing the root path of the file system
     * and the desired file extension name for exported resource files.
     *
     * @param rootPath            the root path of this file system.
     * @param exportFileExtension the custom file extension for exported resource files.
     */
    public ImageFileSystem(String rootPath, String exportFileExtension) {
        this.rootPath = rootPath;
        this.exportFileExtension = exportFileExtension;
    }

    /**
     * Three parameter constructor containing the root path, the exported file extension name
     * for resource files, and a list of parsable file extensions when exporting resources.
     *
     * @param rootPath            the root path for this file system.
     * @param exportFileExtension the custom file extension for exported resource files.
     * @param parsableExtensions  a list of Strings containing parsable file extensions.
     */
    public ImageFileSystem(String rootPath, String exportFileExtension, String... parsableExtensions) {
        this.rootPath = rootPath;
        this.exportFileExtension = exportFileExtension;
        this.parsableFileExtensions = List.of(parsableExtensions);
    }
}
