import com.eis.EasyImageSuite;
import com.eis.models.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

public class SuiteTests {

    private static final String TEST_KEY = "AAAABBBBCCCCDDDD";
    private static final String TEST_IV = "6sjse(3G]a(fQ~:7";

    @Test
    public void noRestrictionExportImportTest() {
        // NOTE: Make sure that there are no 'suite-exports' folders already generated in the resource folder
        Path path = new File(getClass().getResource("export-source").getFile()).toPath();
        String resourceFolderPath = path.toString();
        EasyImageSuite easyImageSuite = new EasyImageSuite()
                .setImageFileSystem(new ImageFileSystem(resourceFolderPath));

        SuiteExportResponse exportResponse = easyImageSuite.encryptToResources(TEST_KEY, TEST_IV);

        if (failed(exportResponse)) return;

        easyImageSuite.getImageFileSystem().setResourceFolder("suite-exports");
        SuiteImportResponse importResponse = easyImageSuite.importResources(TEST_KEY, TEST_IV);

        if (failed(importResponse)) return;

        int exportRawSize = exportResponse.getRawExportData().size();
        int importRawSize = importResponse.getRawImportData().size();

        if (exportRawSize != importRawSize)
            fail("Exported/Imported file counts do not match.");
        else {
            for (int i = 0; i < exportRawSize; i++) {
                // Go through each export index, get the raw data (before encryption),
                // and assert that the decrypted data is equal.
                String exportRawVal = exportResponse.getRawExportData().get(i).getValue();
                String importRawVal = importResponse.getRawImportData().get(i).getValue();
                Assertions.assertEquals(exportRawVal, importRawVal);
            }
        }
    }

    private boolean failed(SuiteResponse response) {
        if (!response.sucessful) {
            StringBuilder sb = new StringBuilder();
            for (SuiteError suiteError : response.getErrors())
                sb.append(suiteError).append("\n");
            String allErrors = sb.toString();
            fail(allErrors);
            return true;
        }
        return false;
    }

}
