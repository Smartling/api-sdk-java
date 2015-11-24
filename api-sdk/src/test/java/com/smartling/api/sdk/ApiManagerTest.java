package com.smartling.api.sdk;

import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameter;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.AuthorizedLocales;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileListItem;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static com.smartling.api.sdk.file.FileType.JAVA_PROPERTIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ApiManagerTest
{
    private static final String FILE_URI = "/testfile.properties";
    private static final String FILE_URI_RENAMED = "/testfileRenamed.properties";

    private static final String CHARSET = "UTF-8";
    private ApiManager apiManager;
    private String userId;
    private String userSecret;
    private String projectId;
    private File fileToUpload;
    private File translatedFileToUpload;
    private static final String LOCALE = "en-US";
    private static final String LOCALE_ES = "es";
    private String baseAuthApiUrl = "https://api.smartling.com";
    private String baseFileApiUrl = "https://api.smartling.com";

    @Before
    public void setup() throws SmartlingApiException
    {
        userId = System.getProperty("userId");
        userSecret = System.getProperty("userSecret");
        projectId = System.getProperty("projectId");

        ProxyConfiguration proxyConfiguration = mock(ProxyConfiguration.class);
        apiManager = new ApiManager(userId, userSecret, projectId, baseAuthApiUrl,baseFileApiUrl).withProxy(proxyConfiguration);
        fileToUpload = new File(getClass().getClassLoader().getResource("testfile.properties").getPath());
        translatedFileToUpload = new File(getClass().getClassLoader().getResource("testfileES.properties").getPath());
    }

    @Test
    public void testApi() throws Exception
    {
        apiManager.uploadFile(fileToUpload, CHARSET, new FileUploadParameterBuilder(JAVA_PROPERTIES, FILE_URI).localeIdsToAuthorize(Collections.singletonList("es")
                ).overwriteAuthorizedLocales(true));
        apiManager.importTranslations(translatedFileToUpload, LOCALE_ES, CHARSET, new FileImportParameterBuilder(JAVA_PROPERTIES, FILE_URI).overwrite(true));

        StringResponse response = apiManager.getFile(LOCALE_ES, new GetFileParameterBuilder(FILE_URI));

        // bug in FileAPi enable after fix and fix testFileES to proper Test de integración
        // assertEquals("UTF-16", response.getEncoding());
        assertEquals("test=Test de integracion", response.getContents());

        assertEquals(1, apiManager.getAuthorizedLocales(FILE_URI).getItems().size());

        apiManager.unAuthorizeLocales(FILE_URI, LOCALE_ES);

        assertEquals(0, apiManager.getAuthorizedLocales(FILE_URI).getItems().size());

        apiManager.authorizeLocales(FILE_URI, LOCALE_ES);

        final AuthorizedLocales authorizedLocales = apiManager.getAuthorizedLocales(FILE_URI);
        assertEquals(1, authorizedLocales.getItems().size());
        assertEquals("es", authorizedLocales.getItems().get(0));

        FileStatus status = apiManager.getFileStatus(FILE_URI);
        assertEquals(FILE_URI, status.getFileUri());
        assertEquals("javaProperties", status.getFileType());
        assertEquals(1, status.getTotalStringCount());
        assertEquals(1, status.getTotalWordCount());
        FileLocaleStatus localeStatus = apiManager.getFileLocaleStatus(LOCALE, FILE_URI);
        assertEquals(FILE_URI, localeStatus.getFileUri());
        assertEquals("javaProperties", localeStatus.getFileType());
        assertEquals(1, localeStatus.getTotalStringCount());
        assertEquals(1, localeStatus.getTotalWordCount());

        StringResponse originalFile = apiManager.getOriginalFile(LOCALE, new GetOriginalFileParameterBuilder(FILE_URI));
        assertEquals("test=integrationTest", originalFile.getContents());

        apiManager.renameFile(FILE_URI, FILE_URI_RENAMED);

        boolean renamedFileFound = false;
        FileList list = apiManager.getFilesList(new FileListSearchParameter());
        for(FileListItem item : list.getItems())
        {
            if (item.getFileUri().equals(FILE_URI_RENAMED))
            {
                renamedFileFound = true;
            }
        }
        assertTrue(renamedFileFound);

        FileLastModified fileLastModified = apiManager.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
        assertEquals(5, fileLastModified.getTotalCount());
        apiManager.deleteFile(FILE_URI);
    }
}