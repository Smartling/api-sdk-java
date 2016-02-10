package com.smartling.api.sdk.file;

import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.AuthorizedLocales;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileImportSmartlingData;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.util.DateFormatter;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileApiClientImplTest
{
    private static final String CALLBACK_URL = "callbackUrl";
    private static final String PROJECT_ID = "testProject";
    private static final String LOCALE = "en-US";
    private static final String FILE_URI = "fileUri";
    private static final String FILE_URI2 = "fileUri2";
    private static final String CHARSET = "UTF-8";
    private static final String USER_TOKEN = "userSecret BEARER";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private FileApiClientImpl fileApiClientImpl;
    private HttpUtils httpUtils;
    private StringResponse response;
    private ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
    private ProxyConfiguration proxyConfiguration;

    @Before
    public void setup() throws SmartlingApiException
    {
        proxyConfiguration = mock(ProxyConfiguration.class);
        fileApiClientImpl = new FileApiClientImpl.Builder(PROJECT_ID)
                .authWithUserIdAndSecret("userId", "userSecret")
                .proxyConfiguration(proxyConfiguration)
                .build();
        httpUtils = mock(HttpUtils.class);
        fileApiClientImpl.setHttpUtils(httpUtils);
        response = mock(StringResponse.class);
        TokenProvider tokenProvider = mock(TokenProvider.class);
        fileApiClientImpl.setTokenProvider(tokenProvider);
        when(tokenProvider.getAuthenticationToken()).thenReturn(new AuthenticationToken("userSecret", "BEARER"));
        when(response.isSuccess()).thenReturn(true);
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);
    }

    @Test
    public void testUploadFile() throws Exception
    {
        FileUploadParameterBuilder fileUploadParameterBuilder = getFileUploadParameterBuilder().charset(CHARSET);
        File fileToUpload = mock(File.class);
        when(response.getContents()).thenReturn(ResponseExamples.UPLOAD_RESPONSE);
        UploadFileData uploadFileDataResponse = fileApiClientImpl.uploadFile(fileToUpload, fileUploadParameterBuilder);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertNotNull(((HttpPost)request).getEntity());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file", request.getURI().toString());
        assertEquals(false, uploadFileDataResponse.isOverwritten());
        assertEquals(1, uploadFileDataResponse.getStringCount());
        assertEquals(2, uploadFileDataResponse.getWordCount());
    }

    @Test
    public void testUploadFileFromStream() throws Exception
    {
        FileUploadParameterBuilder fileUploadParameterBuilder = getFileUploadParameterBuilder().charset(CHARSET);
        InputStream inputStream = mock(InputStream.class);
        when(response.getContents()).thenReturn(ResponseExamples.UPLOAD_RESPONSE);
        fileApiClientImpl.uploadFile(inputStream, FILE_URI, fileUploadParameterBuilder);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertNotNull(((HttpPost)request).getEntity());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file", request.getURI().toString());
    }

    @Test
    public void testDeleteFile() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.EMPTY_RESPONSE);
        EmptyResponse apiResponse = fileApiClientImpl.deleteFile(FILE_URI);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/delete", request.getURI().toString());
        assertEquals(EmptyResponse.class, apiResponse.getClass());
    }

    @Test
    public void testRenameFile() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.EMPTY_RESPONSE);
        EmptyResponse apiResponse = fileApiClientImpl.renameFile(FILE_URI, FILE_URI2);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/rename", request.getURI().toString());
        assertEquals(EmptyResponse.class, apiResponse.getClass());
    }

    @Test
    public void testGetLastModified() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.LAST_MODIFICATION_RESPONSE);
        FileLastModified apiResponse = fileApiClientImpl.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/last-modified?fileUri=fileUri", request.getURI().toString());
        assertEquals(5, apiResponse.getItems().size());
        assertEquals(5, apiResponse.getTotalCount());
        assertEquals("be-BY", apiResponse.getItems().get(0).getLocaleId());
        assertEquals(DateFormatter.parse("2015-09-15T21:24:42+0000").getTime(), apiResponse.getItems().get(0).getLastModified().getTime());
    }

    @Test
    public void testGetLastModifiedWhenGetWrongResponse() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.NOT_EXISTING_CODE_RESPONSE);

        expectedEx.expect(SmartlingApiException.class);
        expectedEx.expectMessage("Response hasn't been parsed correctly [response=");

        fileApiClientImpl.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
    }

    @Test
    public void testGetLastModifiedShouldThrowSmartlingApiExceptionWhenEmptyContent() throws Exception
    {
        when(this.response.getContents()).thenReturn("");

        expectedEx.expect(SmartlingApiException.class);
        expectedEx.expectMessage("Response hasn't been parsed correctly [response=");

        fileApiClientImpl.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
    }

    @Test
    public void testGetFile() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.GET_FILE_RESPONSE);
        fileApiClientImpl.getFile(new GetFileParameterBuilder(FILE_URI, LOCALE));
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/locales/" + LOCALE + "/file?fileUri=fileUri", request.getURI().toString());
    }

    @Test
    public void testGetOriginalFile() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.GET_FILE_RESPONSE);
        fileApiClientImpl.getOriginalFile(new GetOriginalFileParameterBuilder(FILE_URI));
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file?fileUri=fileUri", request.getURI().toString());
    }

    @Test
    public void testGetFilesList() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.FILE_LIST_RESPONSE);
        FileList apiResponse = fileApiClientImpl.getFilesList(new FileListSearchParameterBuilder());
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/files/list?", request.getURI().toString());
        assertEquals(2, apiResponse.getItems().size());
        assertEquals(2, apiResponse.getTotalCount());
        assertEquals("FileListItem[fileUri=3-namespace-explicit.xml,lastUploaded=2015-07-29T10:34:30+0000,fileType=xml]", apiResponse.getItems().get(0).toString());
    }

    @Test
    public void testGetFileLocaleStatus() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.FILE_LOCALES_STATUS_RESPONSE);
        FileLocaleStatus apiResponse = fileApiClientImpl.getFileLocaleStatus(FILE_URI, LOCALE);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/locales/en-US/file/status?fileUri=fileUri", request.getURI().toString());
        assertEquals(
                "FileLocaleStatus[fileUri=test-cancel-translation.xml,totalStringCount=1,totalWordCount=3,authorizedStringCount=0,authorizedWordCount=0,completedStringCount=0,"
                        + "completedWordCount=0,excludedStringCount=0,excludedWordCount=0,lastUploaded=2015-09-15T21:28:25+0000,fileType=xml]",
                apiResponse.toString()
        );
    }

    @Test
    public void testGetFileStatus() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.FILE_STATUS_RESPONSE);
        FileStatus apiResponse = fileApiClientImpl.getFileStatus(FILE_URI);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/status?fileUri=fileUri", request.getURI().toString());
        assertEquals(5, apiResponse.getItems().size());
        assertEquals(5, apiResponse.getTotalCount());
        assertEquals(3, apiResponse.getTotalWordCount());
        assertEquals(1, apiResponse.getTotalStringCount());
        assertEquals(
                "FileStatusItem[localeId=be-BY,authorizedStringCount=0,authorizedWordCount=0,completedStringCount=0,completedWordCount=0,excludedStringCount=0,"
                        + "excludedWordCount=0]",
                apiResponse.getItems().get(0).toString()
        );
    }

    @Test
    public void testImportTranslations() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.IMPORT_TRANSLATIONS_RESPONSE);
        File fileToImport = mock(File.class);
        FileImportSmartlingData apiResponse = fileApiClientImpl
                .importTranslations(new FileImportParameterBuilder(fileToImport, LOCALE, CHARSET, FileType.CSV, FILE_URI));
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/locales/en-US/file/import", request.getURI().toString());
        assertEquals(0, apiResponse.getStringCount());
        assertEquals(0, apiResponse.getTranslationImportErrors().size());
    }

    @Test
    public void testGetAuthorizedLocales() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.FILE_AUTHORIZED_LOCALES_RESPONSE);
        AuthorizedLocales apiResponse = fileApiClientImpl.getAuthorizedLocales(FILE_URI);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/authorized-locales?fileUri=fileUri", request.getURI().toString());
        assertEquals(2, apiResponse.getItems().size());
        assertEquals("it-IT", apiResponse.getItems().get(0));
    }

    @Test
    public void testGetAuthorizedLocalesWithExtraFields() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.FILE_AUTHORIZED_LOCALES_EXTRA_FIELDS_RESPONSE);
        AuthorizedLocales apiResponse = fileApiClientImpl.getAuthorizedLocales(FILE_URI);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/authorized-locales?fileUri=fileUri", request.getURI().toString());
        assertEquals(2, apiResponse.getItems().size());
        assertEquals("it-IT", apiResponse.getItems().get(0));
    }


    @Test
    public void testAuthorizeLocales() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.EMPTY_RESPONSE);
        EmptyResponse apiResponse = fileApiClientImpl.authorizeLocales(FILE_URI, LOCALE);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/authorized-locales", request.getURI().toString());
        assertEquals(EmptyResponse.class, apiResponse.getClass());
    }

    @Test
    public void testUnAuthorizeLocales() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.EMPTY_RESPONSE);
        EmptyResponse apiResponse = fileApiClientImpl.unAuthorizeLocales(FILE_URI, LOCALE);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpDelete.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/authorized-locales?fileUri=fileUri&localeIds%5B%5D=en-US", request.getURI().toString());
        assertEquals(EmptyResponse.class, apiResponse.getClass());
    }

    private FileUploadParameterBuilder getFileUploadParameterBuilder()
    {
        FileUploadParameterBuilder fileUploadParameterBuilder = new FileUploadParameterBuilder(FileType.JAVA_PROPERTIES, FILE_URI);
        fileUploadParameterBuilder.approveContent(true);
        fileUploadParameterBuilder.callbackUrl(CALLBACK_URL);
        fileUploadParameterBuilder.localeIdsToAuthorize(Collections.singletonList(LOCALE));
        fileUploadParameterBuilder.overwriteAuthorizedLocales(true);
        return fileUploadParameterBuilder;
    }
}