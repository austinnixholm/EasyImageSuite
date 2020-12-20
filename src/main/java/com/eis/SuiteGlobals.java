package com.eis;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class SuiteGlobals {

    private static final String[] FILE_EXTS = new String[]{"PNG", "png", "JPG", "jpg", "JPEG", "jpeg", "BMP", "bmp"};
    public static final String DEFAULT_EXPORT_FOLDER_NAME = "suite-exports";
    public static final String DEFAULT_FILE_EXTENSION = "JRC";
    public static final List<String> STANDARD_FILE_EXTENSIONS = List.of(FILE_EXTS);
    public static final int DEFAULT_MAXIMUM_FILE_SIZE_MB = 10;
    private static final long MEGABYTE = 1024L * 1024L;
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";

    /**
     * Converts a file into an array of bytes.
     *
     * @param filePath the path to the file.
     * @return a byte[] of data from the file.
     */
    public static byte[] fileToBytes(String filePath) {
        File f = new File(filePath);
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        byte[] content = null;
        try {
            content = new byte[Objects.requireNonNull(is).available()];
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            is.read(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * Gets a list of all files within a specific directory, including subfolders.
     *
     * @param directoryPath the path to the file directory.
     * @return a list of ALL files within that directory.
     */
    public static List<File> listf(String directoryPath) {
        File directory = new File(directoryPath);
        File[] fList = directory.listFiles();
        List<File> resultList = new ArrayList<>(Arrays.asList(fList));
        Arrays.stream(fList).filter(File::isDirectory).map(file -> listf(file.getAbsolutePath())).forEach(resultList::addAll);
        return resultList;
    }

    /**
     * Gets the file extension of a given file.
     *
     * @param file the file.
     * @return the file's extension as a String.
     */
    public static String getFileExtension(File file) {
        String filePath = file.getPath();
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }

    public static long bytesToMegaBytes(int bytes) {
        return bytes / MEGABYTE;
    }

    public static void log(String message) {
        System.out.println(ANSI_YELLOW + "[EasyImageSuite] " + message);
    }
    public static void logErr(String message) { System.out.println(SuiteGlobals.ANSI_RED + "[EasyImageSuite ERROR] " + message); }

}