package com.smartling.api.sdk.file;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

public class FileApiClientAdapterTest
{
    private static final String  TEST_FILE_ENCODING = "UTF-8";
    private static final String  TEST_FILE_LOCATION = "resources/test.properties";
    private static final String  JAVA_PROPERTIES    = "javaProperties";
    private FileApiClientAdapter fileApiClientAdapter;

    private String locale;

    @Before
    public void setup()
    {
        String apiKey = System.getProperty("apiKey");
        String projectId = System.getProperty("projectId");
        locale = System.getProperty("locale");

        Assert.assertNotNull("The apiKey system property must be set", apiKey);
        Assert.assertNotNull("The projectId system property must be set", projectId);
        Assert.assertNotNull("The locale system property must be set", locale);

        fileApiClientAdapter = new FileApiClientAdapterImpl("https://api.smartling.com/v1", apiKey, projectId);
    }

    @Test
    public void testUploadFileThenDownloadOriginal() throws FileApiException, IOException
    {
        File fileForUpload = getTestFile();
        uploadFile(fileForUpload);

        // Null locale returns the original content of the file
        String fileContents = fileApiClientAdapter.getFile(fileForUpload.getName(), null);
        assertEquals(FileUtils.readFileToString(fileForUpload), fileContents);
    }

    @Test
    public void testUploadFileThenCheckListingOfFile() throws FileApiException
    {
        File fileForUpload = getTestFile();
        uploadFile(fileForUpload);

        String fileListResults = fileApiClientAdapter.getFilesList();
        assertTrue(fileListResults.contains(fileForUpload.getName()));
    }

    @Test
    public void testUploadFileThenCheckStatus() throws FileApiException
    {
        File fileForUpload = getTestFile();
        uploadFile(fileForUpload);

        String fileListResults = fileApiClientAdapter.getFileStatus(fileForUpload.getName(), locale);
        assertTrue(fileListResults.contains(fileForUpload.getName()));
    }

    private void uploadFile(File fileForUpload) throws FileApiException
    {
        fileApiClientAdapter.uploadFile(JAVA_PROPERTIES, fileForUpload.getName(), fileForUpload.getAbsolutePath(), TEST_FILE_ENCODING);
    }

    private File getTestFile()
    {
        return new File(FilenameUtils.separatorsToSystem(TEST_FILE_LOCATION));
    }
}
