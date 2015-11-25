package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationCommand;
import com.smartling.api.sdk.auth.AuthenticationContext;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.AuthorizedLocales;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileListItem;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.file.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.smartling.api.sdk.file.FileType.JAVA_PROPERTIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SmartlingApiManagerTest
{
    private static final String FILE_URI = "/testfile.properties";
    private static final String FILE_URI_RENAMED = "/testfileRenamed.properties";

    private static final String CHARSET = "UTF-8";
    private SmartlingApiManager smartlingApiManager;
    private File fileToUpload;
    private File translatedFileToUpload;
    private static final String LOCALE = "en-US";
    private static final String LOCALE_ES = "es";

    @Before
    public void setup() throws SmartlingApiException
    {
        final String userId = System.getProperty("userId");
        final String userSecret = System.getProperty("userSecret");
        final String projectId = System.getProperty("projectId");

        ProxyConfiguration proxyConfiguration = mock(ProxyConfiguration.class);
        final String baseAuthApiUrl = "https://api.smartling.com";
        final String baseFileApiUrl = "https://api.smartling.com";

        smartlingApiManager = new SmartlingApiManager(userId, userSecret, projectId, baseAuthApiUrl, baseFileApiUrl).withProxy(proxyConfiguration);
        fileToUpload = new File(getClass().getClassLoader().getResource("testfile.properties").getPath());
        translatedFileToUpload = new File(getClass().getClassLoader().getResource("testfileES.properties").getPath());
    }

    @Test
    public void testApi() throws Exception
    {
        smartlingApiManager.uploadFile(fileToUpload, CHARSET, new FileUploadParameterBuilder(JAVA_PROPERTIES, FILE_URI).localeIdsToAuthorize(Collections.singletonList("es")
                ).overwriteAuthorizedLocales(true));
        smartlingApiManager.importTranslations(translatedFileToUpload, LOCALE_ES, CHARSET, new FileImportParameterBuilder(JAVA_PROPERTIES, FILE_URI).overwrite(true));

        StringResponse response = smartlingApiManager.getFile(LOCALE_ES, new GetFileParameterBuilder(FILE_URI));

        // bug in FileAPi enable after fix and fix testFileES to proper Test de integración
        // assertEquals("UTF-16", response.getEncoding());
        assertEquals("test=Test de integracion", response.getContents());

        assertEquals(1, smartlingApiManager.getAuthorizedLocales(FILE_URI).getItems().size());

        smartlingApiManager.unAuthorizeLocales(FILE_URI, LOCALE_ES);

        assertEquals(0, smartlingApiManager.getAuthorizedLocales(FILE_URI).getItems().size());

        smartlingApiManager.authorizeLocales(FILE_URI, LOCALE_ES);

        final AuthorizedLocales authorizedLocales = smartlingApiManager.getAuthorizedLocales(FILE_URI);
        assertEquals(1, authorizedLocales.getItems().size());
        assertEquals("es", authorizedLocales.getItems().get(0));

        FileStatus status = smartlingApiManager.getFileStatus(FILE_URI);
        assertEquals(FILE_URI, status.getFileUri());
        assertEquals("javaProperties", status.getFileType());
        assertEquals(1, status.getTotalStringCount());
        assertEquals(1, status.getTotalWordCount());
        FileLocaleStatus localeStatus = smartlingApiManager.getFileLocaleStatus(LOCALE, FILE_URI);
        assertEquals(FILE_URI, localeStatus.getFileUri());
        assertEquals("javaProperties", localeStatus.getFileType());
        assertEquals(1, localeStatus.getTotalStringCount());
        assertEquals(1, localeStatus.getTotalWordCount());

        StringResponse originalFile = smartlingApiManager.getOriginalFile(LOCALE, new GetOriginalFileParameterBuilder(FILE_URI));
        assertEquals("test=integrationTest", originalFile.getContents());

        smartlingApiManager.renameFile(FILE_URI, FILE_URI_RENAMED);

        boolean renamedFileFound = false;
        FileList list = smartlingApiManager.getFilesList(new FileListSearchParameterBuilder());
        for(FileListItem item : list.getItems())
        {
            if (item.getFileUri().equals(FILE_URI_RENAMED))
            {
                renamedFileFound = true;
            }
        }
        assertTrue(renamedFileFound);

        FileLastModified fileLastModified = smartlingApiManager.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
        assertEquals(5, fileLastModified.getTotalCount());
        smartlingApiManager.deleteFile(FILE_URI);
    }

    @Test
    public void testGenerateAuthenticationContext() throws SmartlingApiException, InterruptedException
    {
        AuthApiClient authApiClient = mock(AuthApiClient.class);
        smartlingApiManager.authApiClient = authApiClient;
        Response<AuthenticationContext> response = new Response<>();
        AuthenticationContext context = new AuthenticationContext();
        context.setExpiresIn(2);
        context.setAccessToken("111");
        response.setData(context);
        when(authApiClient.authenticate(any(AuthenticationCommand.class), any(ProxyConfiguration.class), anyString())).thenReturn(response);

        smartlingApiManager.generateAuthenticationContext();
        smartlingApiManager.expireExecutor.shutdown();
        smartlingApiManager.expireExecutor.awaitTermination(30, TimeUnit.MINUTES);
    }
}