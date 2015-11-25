package com.smartling.api.sdk;

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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static com.smartling.api.sdk.file.FileType.JAVA_PROPERTIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SmartlingApiGatewayTest
{
    private static final String FILE_URI = "/testfile.properties";
    private static final String FILE_URI_RENAMED = "/testfileRenamed.properties";

    private static final String CHARSET = "UTF-8";
    private SmartlingApiGateway smartlingApiGateway;
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

        smartlingApiGateway = new SmartlingApiGatewayImpl(userId, userSecret, projectId, proxyConfiguration);
        fileToUpload = new File(getClass().getClassLoader().getResource("testfile.properties").getPath());
        translatedFileToUpload = new File(getClass().getClassLoader().getResource("testfileES.properties").getPath());
    }

    @Test
    public void testApi() throws Exception
    {
        smartlingApiGateway.uploadFile(fileToUpload, CHARSET, new FileUploadParameterBuilder(JAVA_PROPERTIES, FILE_URI).localeIdsToAuthorize(Collections.singletonList("es")
                ).overwriteAuthorizedLocales(true));
        smartlingApiGateway.importTranslations(translatedFileToUpload, LOCALE_ES, CHARSET, new FileImportParameterBuilder(JAVA_PROPERTIES, FILE_URI).overwrite(true));

        StringResponse response = smartlingApiGateway.getFile(LOCALE_ES, new GetFileParameterBuilder(FILE_URI));

        // bug in FileAPi enable after fix and fix testFileES to proper Test de integración
        // assertEquals("UTF-16", response.getEncoding());
        assertEquals("test=Test de integracion", response.getContents());

        assertEquals(1, smartlingApiGateway.getAuthorizedLocales(FILE_URI).getItems().size());

        smartlingApiGateway.unAuthorizeLocales(FILE_URI, LOCALE_ES);

        assertEquals(0, smartlingApiGateway.getAuthorizedLocales(FILE_URI).getItems().size());

        smartlingApiGateway.authorizeLocales(FILE_URI, LOCALE_ES);

        final AuthorizedLocales authorizedLocales = smartlingApiGateway.getAuthorizedLocales(FILE_URI);
        assertEquals(1, authorizedLocales.getItems().size());
        assertEquals("es", authorizedLocales.getItems().get(0));

        FileStatus status = smartlingApiGateway.getFileStatus(FILE_URI);
        assertEquals(FILE_URI, status.getFileUri());
        assertEquals("javaProperties", status.getFileType());
        assertEquals(1, status.getTotalStringCount());
        assertEquals(1, status.getTotalWordCount());
        FileLocaleStatus localeStatus = smartlingApiGateway.getFileLocaleStatus(LOCALE, FILE_URI);
        assertEquals(FILE_URI, localeStatus.getFileUri());
        assertEquals("javaProperties", localeStatus.getFileType());
        assertEquals(1, localeStatus.getTotalStringCount());
        assertEquals(1, localeStatus.getTotalWordCount());

        StringResponse originalFile = smartlingApiGateway.getOriginalFile(new GetOriginalFileParameterBuilder(FILE_URI));
        assertEquals("test=integrationTest", originalFile.getContents());

        smartlingApiGateway.renameFile(FILE_URI, FILE_URI_RENAMED);

        boolean renamedFileFound = false;
        FileList list = smartlingApiGateway.getFilesList(new FileListSearchParameterBuilder());
        for(FileListItem item : list.getItems())
        {
            if (item.getFileUri().equals(FILE_URI_RENAMED))
            {
                renamedFileFound = true;
            }
        }
        assertTrue(renamedFileFound);

        FileLastModified fileLastModified = smartlingApiGateway.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
        assertEquals(5, fileLastModified.getTotalCount());
        smartlingApiGateway.deleteFile(FILE_URI);
    }

}