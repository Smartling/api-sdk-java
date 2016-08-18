package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationCommand;
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
import static org.mockito.Mockito.mock;

public class IntegrationTest
{
    private static final String FILE_URI = "/testfile.properties";
    private static final String FILE_URI_RENAMED = "/testfileRenamed.properties";

    private static final String CHARSET = "UTF-8";
    private FileApiClient fileApiClient;
    private File fileToUpload;
    private File translatedFileToUpload;
    private static final String LOCALE = "en-US";
    private static final String LOCALE_ES = "es";
    private AuthApiClient authApiClient;

    @Before
    public void setup() throws SmartlingApiException
    {
        final String userId = System.getProperty("userId");
        final String userSecret = System.getProperty("userSecret");
        final String projectId = System.getProperty("projectId");

        ProxyConfiguration proxyConfiguration = mock(ProxyConfiguration.class);
        fileApiClient = new FileApiClientImpl.Builder(projectId)
                                                   .authWithUserIdAndSecret(userId, userSecret)
                                                   .proxyConfiguration(proxyConfiguration)
                                                   .build();

        fileToUpload = new File(getClass().getClassLoader().getResource("testfile.properties").getPath());
        translatedFileToUpload = new File(getClass().getClassLoader().getResource("testfileES.properties").getPath());
    }

    @Test
    public void testApi() throws Exception
    {
        fileApiClient.uploadFile(fileToUpload, new FileUploadParameterBuilder(JAVA_PROPERTIES, FILE_URI).charset(CHARSET).localeIdsToAuthorize(Collections.singletonList("es")
                ).overwriteAuthorizedLocales(true));
        fileApiClient.importTranslations(new FileImportParameterBuilder(translatedFileToUpload, LOCALE_ES, CHARSET, JAVA_PROPERTIES, FILE_URI).overwrite(true));

        StringResponse response = fileApiClient.getFile(new GetFileParameterBuilder(FILE_URI, LOCALE_ES));
        
        assertEquals("test=Test de integración", response.getContents());

        FileStatus status = fileApiClient.getFileStatus(FILE_URI);
        assertEquals(FILE_URI, status.getFileUri());
        assertEquals("javaProperties", status.getFileType());
        assertEquals(1, status.getTotalStringCount());
        assertEquals(1, status.getTotalWordCount());
        FileLocaleStatus localeStatus = fileApiClient.getFileLocaleStatus(FILE_URI, LOCALE);
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
        assertEquals(5, fileLastModified.getTotalCount());

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

    @Test
    public void testRefreshToken() throws SmartlingApiException
    {
        authApiClient = new AuthApiClient();
        String refreshToken = authApiClient.authenticate(new AuthenticationCommand(System.getProperty("userId"), System.getProperty("userSecret"))).getData().getRefreshToken();
        assertTrue(authApiClient.refresh(refreshToken).getData().getAccessToken().length() > 10);
    }
}