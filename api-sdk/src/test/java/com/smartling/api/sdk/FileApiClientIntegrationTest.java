package com.smartling.api.sdk;

import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.FileApiClient;
import com.smartling.api.sdk.file.FileApiClientImpl;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileListItem;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static com.smartling.api.sdk.file.FileType.JAVA_PROPERTIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class FileApiClientIntegrationTest
{
    private static final String FILE_URI = "/testfile.properties";
    private static final String FILE_URI_RENAMED = "/testfileRenamed.properties";
    private static final String TEST_LOCALE_CODE = "es";

    private static final String CHARSET = "UTF-8";

    private FileApiClient fileApiClient;

    private File fileToUpload;
    private File translatedFileToUpload;

    @Before
    public void setup() throws SmartlingApiException
    {
        final String userId = System.getProperty("userId");
        final String userSecret = System.getProperty("userSecret");
        final String projectId = System.getProperty("projectId");

        fileApiClient = new FileApiClientImpl.Builder(projectId)
                .authWithUserIdAndSecret(userId, userSecret)
                .build();

        fileToUpload = new File(getClass().getClassLoader().getResource("testfile.properties").getPath());
        translatedFileToUpload = new File(getClass().getClassLoader().getResource("testfileES.properties").getPath());
    }

    @Test(timeout = 5 * 60 * 1000)
    public void testApi() throws Exception
    {
        uploadFile();

        waitForFileToBeProcessed();

        fileApiClient.importTranslations(new FileImportParameterBuilder(translatedFileToUpload, TEST_LOCALE_CODE, CHARSET, JAVA_PROPERTIES, FILE_URI).overwrite(true));

        StringResponse response = fileApiClient.getFile(new GetFileParameterBuilder(FILE_URI, TEST_LOCALE_CODE));
        assertEquals("test=Test de integración", response.getContents());

        FileStatus status = fileApiClient.getFileStatus(FILE_URI);
        assertEquals(FILE_URI, status.getFileUri());
        assertEquals("javaProperties", status.getFileType());
        assertEquals(1, status.getTotalStringCount());
        assertEquals(1, status.getTotalWordCount());

        FileLocaleStatus localeStatus = fileApiClient.getFileLocaleStatus(FILE_URI, TEST_LOCALE_CODE);
        assertEquals(FILE_URI, localeStatus.getFileUri());
        assertEquals("javaProperties", localeStatus.getFileType());
        assertEquals(1, localeStatus.getTotalStringCount());
        assertEquals(1, localeStatus.getTotalWordCount());

        StringResponse originalFile = fileApiClient.getOriginalFile(new GetOriginalFileParameterBuilder(FILE_URI));
        assertEquals("test=integrationTest", originalFile.getContents());

        fileApiClient.renameFile(FILE_URI, FILE_URI_RENAMED);

        boolean renamedFileFound = false;
        FileList list = fileApiClient.getFilesList(new FileListSearchParameterBuilder());
        for(FileListItem item : list.getItems())
        {
            if (item.getFileUri().equals(FILE_URI_RENAMED))
            {
                renamedFileFound = true;
            }
        }
        assertTrue(renamedFileFound);

        FileLastModified fileLastModified = fileApiClient.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI_RENAMED));
        assertTrue(fileLastModified.getTotalCount() > 0);

        fileApiClient.deleteFile(FILE_URI_RENAMED);

        try
        {
            fileApiClient.deleteFile(FILE_URI);
        }
        catch (SmartlingApiException ex)
        {
            assertEquals("file.not.found", ex.getOriginalErrors().get(0).getKey());
            assertNotEquals("N/A", ex.getRequestId());
            assertNotEquals(HttpStatus.SC_OK, ex.getStatusCode());
            assertNotEquals(0, ex.getResponseHeaders().size());
        }
    }

    private void waitForFileToBeProcessed() throws InterruptedException
    {
        while (true) {
            Thread.sleep(2000);
            try
            {
                fileApiClient.getFileStatus(FILE_URI);
                break;
            }
            catch (SmartlingApiException e) {
                System.out.println("Still waiting for file to be processed: " + e);
            }
        }
    }

    private void uploadFile() throws SmartlingApiException
    {
        FileUploadParameterBuilder parameterBuilder = new FileUploadParameterBuilder(JAVA_PROPERTIES, FILE_URI)
                .charset(CHARSET)
                .directives(Collections.singletonMap("smartling.namespace", "test-namespace"))
                .localeIdsToAuthorize(Collections.singletonList("es"))
                .overwriteAuthorizedLocales(true);
        fileApiClient.uploadFile(fileToUpload, parameterBuilder);
    }
}